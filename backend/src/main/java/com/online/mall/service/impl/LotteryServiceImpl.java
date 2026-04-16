package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.entity.LotteryPrize;
import com.online.mall.entity.LotteryRecord;
import com.online.mall.mapper.LotteryPrizeMapper;
import com.online.mall.mapper.LotteryRecordMapper;
import com.online.mall.service.LotteryService;
import com.online.mall.vo.LotteryPrizeVO;
import com.online.mall.vo.LotteryRecordVO;
import com.online.mall.vo.LotteryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 抽奖服务实现
 */
@Slf4j
@Service
public class LotteryServiceImpl extends ServiceImpl<LotteryPrizeMapper, LotteryPrize> implements LotteryService {

    @Autowired
    private LotteryRecordMapper recordMapper;

    // 每天免费抽奖次数
    private static final int DAILY_FREE_TIMES = 3;

    // 抽奖次数缓存 key 前缀
    private static final String LOTTERY_TIMES_KEY = "lottery:times:";

    // 内存存储抽奖次数（生产环境建议使用Redis）
    private final Map<String, Integer> lotteryTimesCache = new ConcurrentHashMap<>();

    @Override
    public List<LotteryPrizeVO> getPrizeList() {
        List<LotteryPrize> prizes = this.list(
            new LambdaQueryWrapper<LotteryPrize>()
                .eq(LotteryPrize::getStatus, 1)
                .orderByAsc(LotteryPrize::getSort)
        );
        return prizes.stream()
            .map(this::convertToPrizeVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LotteryVO doLottery(Long userId, String username) {
        // 检查抽奖次数
        Integer remainingTimes = getRemainingTimes(userId);
        if (remainingTimes <= 0) {
            throw new BusinessException("今日抽奖次数已用完，明天再来吧！");
        }

        // 扣减抽奖次数（内存存储）
        String key = LOTTERY_TIMES_KEY + userId + ":" + LocalDate.now();
        lotteryTimesCache.put(key, remainingTimes - 1);

        // 获取所有启用的奖品
        List<LotteryPrize> prizes = this.list(
            new LambdaQueryWrapper<LotteryPrize>()
                .eq(LotteryPrize::getStatus, 1)
                .gt(LotteryPrize::getStock, 0)
                .orderByAsc(LotteryPrize::getSort)
        );

        if (prizes.isEmpty()) {
            throw new BusinessException("活动奖品已发完");
        }

        // 执行抽奖算法
        LotteryPrize winPrize = draw(prizes);

        // 计算转盘旋转角度
        int angle = calculateAngle(prizes, winPrize);

        // 创建抽奖记录
        LotteryRecord record = new LotteryRecord();
        record.setUserId(userId);
        record.setUsername(username);
        record.setPrizeId(winPrize.getId());
        record.setPrizeName(winPrize.getName());
        record.setPrizeLevel(winPrize.getLevel());
        record.setLotteryTime(LocalDateTime.now());
        record.setReceiveStatus(winPrize.getLevel() < 4 ? 0 : 1); // 谢谢参与自动已领取
        recordMapper.insert(record);

        // 扣减库存（谢谢参与不扣减）- 使用乐观锁重试
        if (winPrize.getLevel() < 4) {
            boolean success = deductStockWithRetry(winPrize.getId());
            if (!success) {
                log.warn("用户 {} 中奖 {} 但库存扣减失败，降级为谢谢参与", username, winPrize.getName());
                // 库存扣减失败，降级为谢谢参与
                record.setPrizeLevel(4);
                record.setPrizeName("谢谢参与");
                recordMapper.updateById(record);
                winPrize = prizes.stream()
                    .filter(p -> p.getLevel() == 4)
                    .findFirst()
                    .orElse(winPrize);
            }
        }

        // 返回结果
        LotteryVO vo = new LotteryVO();
        vo.setWin(winPrize.getLevel() < 4);
        vo.setPrizeId(winPrize.getId());
        vo.setPrizeName(winPrize.getName());
        vo.setPrizeLevel(winPrize.getLevel());
        vo.setPrizeImage(winPrize.getImage());
        vo.setPrizeValue(winPrize.getValue());
        vo.setRemainingTimes(getRemainingTimes(userId));
        vo.setRotateAngle(angle);

        log.info("用户 {} 抽奖结果: {}", username, winPrize.getName());
        return vo;
    }

    /**
     * 使用乐观锁扣减库存（带重试）
     */
    private boolean deductStockWithRetry(Long prizeId) {
        int maxRetry = 3;
        for (int i = 0; i < maxRetry; i++) {
            LotteryPrize prize = this.getById(prizeId);
            if (prize == null || prize.getIssued() >= prize.getStock()) {
                return false;
            }
            prize.setIssued(prize.getIssued() + 1);
            boolean updated = this.updateById(prize);
            if (updated) {
                return true;
            }
            log.debug("乐观锁重试第 {} 次", i + 1);
        }
        return false;
    }

    @Override
    public Integer getRemainingTimes(Long userId) {
        String key = LOTTERY_TIMES_KEY + userId + ":" + LocalDate.now();
        Integer times = lotteryTimesCache.get(key);
        if (times == null) {
            // 初始化今日抽奖次数
            lotteryTimesCache.put(key, DAILY_FREE_TIMES);
            return DAILY_FREE_TIMES;
        }
        return times;
    }

    @Override
    public List<LotteryRecordVO> getUserRecords(Long userId) {
        List<LotteryRecord> records = recordMapper.selectList(
            new LambdaQueryWrapper<LotteryRecord>()
                .eq(LotteryRecord::getUserId, userId)
                .orderByDesc(LotteryRecord::getLotteryTime)
                .last("LIMIT 10")
        );
        return records.stream()
            .map(this::convertToRecordVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void receivePrize(Long userId, Long recordId) {
        LotteryRecord record = recordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("记录不存在");
        }
        if (record.getReceiveStatus() == 1) {
            throw new BusinessException("奖品已领取");
        }
        if (record.getPrizeLevel() == 4) {
            throw new BusinessException("该奖品无需领取");
        }

        record.setReceiveStatus(1);
        record.setReceiveTime(LocalDateTime.now());
        recordMapper.updateById(record);
    }

    @Override
    public Page<LotteryRecordVO> getRecordPage(int pageNum, int pageSize, Integer prizeLevel) {
        Page<LotteryRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LotteryRecord> wrapper = new LambdaQueryWrapper<>();
        if (prizeLevel != null) {
            wrapper.eq(LotteryRecord::getPrizeLevel, prizeLevel);
        }
        wrapper.orderByDesc(LotteryRecord::getLotteryTime);
        page = recordMapper.selectPage(page, wrapper);

        Page<LotteryRecordVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage, "records");
        voPage.setRecords(page.getRecords().stream()
            .map(this::convertToRecordVO)
            .collect(Collectors.toList()));
        return voPage;
    }

    @Override
    @Transactional
    public void initDefaultPrizes() {
        // 检查是否已有奖品
        long count = this.count();
        if (count > 0) {
            return;
        }

        // 初始化五一活动奖品
        savePrize("笔记本电脑", 1, new BigDecimal("5000"), new BigDecimal("0.1"), 10, 1);
        savePrize("智能手机", 2, new BigDecimal("2000"), new BigDecimal("1"), 50, 2);
        savePrize("保温杯", 3, new BigDecimal("100"), new BigDecimal("5"), 500, 3);
        savePrize("谢谢参与", 4, BigDecimal.ZERO, new BigDecimal("93.9"), 999999, 4);

        log.info("初始化五一活动奖品成功");
    }

    private void savePrize(String name, int level, BigDecimal value, BigDecimal probability, int stock, int sort) {
        LotteryPrize prize = new LotteryPrize();
        prize.setName(name);
        prize.setLevel(level);
        prize.setValue(value);
        prize.setProbability(probability);
        prize.setStock(stock);
        prize.setIssued(0);
        prize.setStatus(1);
        prize.setSort(sort);
        this.save(prize);
    }

    /**
     * 抽奖算法
     */
    private LotteryPrize draw(List<LotteryPrize> prizes) {
        // 生成0-100的随机数
        double random = Math.random() * 100;
        double cumulative = 0;

        for (LotteryPrize prize : prizes) {
            cumulative += prize.getProbability().doubleValue();
            if (random <= cumulative) {
                // 检查库存
                if (prize.getStock() > prize.getIssued()) {
                    return prize;
                }
            }
        }

        // 默认返回谢谢参与
        return prizes.stream()
            .filter(p -> p.getLevel() == 4)
            .findFirst()
            .orElse(prizes.get(prizes.size() - 1));
    }

    /**
     * 计算转盘旋转角度
     */
    private int calculateAngle(List<LotteryPrize> prizes, LotteryPrize winPrize) {
        int prizeIndex = -1;
        for (int i = 0; i < prizes.size(); i++) {
            if (prizes.get(i).getId().equals(winPrize.getId())) {
                prizeIndex = i;
                break;
            }
        }

        if (prizeIndex == -1) {
            prizeIndex = prizes.size() - 1;
        }

        // 每个奖品占用的角度
        int anglePerPrize = 360 / prizes.size();

        // 奖品 i 的中心角度位置（从左侧开始顺时针测量）
        // 前端第一个扇区从 -90 度开始，中心在 -90 + anglePerPrize/2
        double prizeCenterAngle = -90 + (prizeIndex + 0.5) * anglePerPrize;

        // 要让奖品停在顶部（90度位置），计算需要旋转的角度
        int targetAngle = (int) (90 - prizeCenterAngle);
        // 确保角度为正
        if (targetAngle < 0) {
            targetAngle += 360;
        }

        // 加上随机偏移，避免总是停在扇区正中间
        int offset = (int) (Math.random() * (anglePerPrize * 0.4) - anglePerPrize * 0.2);

        // 加上多转几圈
        int extraSpins = 5 + (int) (Math.random() * 3);

        return extraSpins * 360 + targetAngle + offset;
    }

    private LotteryPrizeVO convertToPrizeVO(LotteryPrize prize) {
        LotteryPrizeVO vo = new LotteryPrizeVO();
        BeanUtils.copyProperties(prize, vo);
        return vo;
    }

    private LotteryRecordVO convertToRecordVO(LotteryRecord record) {
        LotteryRecordVO vo = new LotteryRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}