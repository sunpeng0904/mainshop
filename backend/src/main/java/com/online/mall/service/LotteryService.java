package com.online.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.LotteryPrize;
import com.online.mall.entity.LotteryRecord;
import com.online.mall.vo.LotteryPrizeVO;
import com.online.mall.vo.LotteryRecordVO;
import com.online.mall.vo.LotteryVO;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface LotteryService extends IService<LotteryPrize> {

    /**
     * 获取奖品列表（转盘展示用）
     */
    List<LotteryPrizeVO> getPrizeList();

    /**
     * 执行抽奖
     * @param userId 用户ID
     * @param username 用户名
     * @return 抽奖结果
     */
    LotteryVO doLottery(Long userId, String username);

    /**
     * 获取用户剩余抽奖次数
     */
    Integer getRemainingTimes(Long userId);

    /**
     * 获取用户中奖记录
     */
    List<LotteryRecordVO> getUserRecords(Long userId);

    /**
     * 领取奖品
     */
    void receivePrize(Long userId, Long recordId);

    /**
     * 获取中奖记录列表（管理端）
     */
    Page<LotteryRecordVO> getRecordPage(int pageNum, int pageSize, Integer prizeLevel);

    /**
     * 初始化默认奖品
     */
    void initDefaultPrizes();
}
