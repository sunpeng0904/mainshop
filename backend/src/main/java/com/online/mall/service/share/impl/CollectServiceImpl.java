package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.share.Share;
import com.online.mall.entity.share.ShareCollect;
import com.online.mall.mapper.ShareCollectMapper;
import com.online.mall.service.share.CollectService;
import com.online.mall.service.share.ShareService;
import com.online.mall.vo.share.ShareVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
public class CollectServiceImpl extends ServiceImpl<ShareCollectMapper, ShareCollect> implements CollectService {

    @Lazy
    @Autowired
    private ShareService shareService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleCollect(Long shareId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 检查是否已收藏
        LambdaQueryWrapper<ShareCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareCollect::getShareId, shareId)
                .eq(ShareCollect::getUserId, userId);
        ShareCollect existingCollect = this.getOne(wrapper);

        Share share = shareService.getById(shareId);
        if (share == null) {
            throw new RuntimeException("分享不存在");
        }

        if (existingCollect != null) {
            // 取消收藏
            this.removeById(existingCollect.getId());
            share.setCollectCount(share.getCollectCount() - 1);
            result.put("isCollected", false);
        } else {
            // 收藏
            ShareCollect collect = new ShareCollect();
            collect.setShareId(shareId);
            collect.setUserId(userId);
            collect.setFolderId(0L);
            this.save(collect);
            share.setCollectCount(share.getCollectCount() + 1);
            result.put("isCollected", true);
        }

        shareService.updateById(share);
        result.put("collectCount", share.getCollectCount());

        return result;
    }

    @Override
    public boolean isCollected(Long shareId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<ShareCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareCollect::getShareId, shareId)
                .eq(ShareCollect::getUserId, userId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Map<Long, Boolean> batchCheckCollected(List<Long> shareIds, Long userId) {
        Map<Long, Boolean> result = new HashMap<>();
        if (userId == null || shareIds == null || shareIds.isEmpty()) {
            return result;
        }

        LambdaQueryWrapper<ShareCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ShareCollect::getShareId, shareIds)
                .eq(ShareCollect::getUserId, userId);
        List<ShareCollect> collects = this.list(wrapper);

        Map<Long, Boolean> collectedMap = collects.stream()
                .collect(Collectors.toMap(ShareCollect::getShareId, collect -> true));

        for (Long shareId : shareIds) {
            result.put(shareId, collectedMap.getOrDefault(shareId, false));
        }

        return result;
    }

    @Override
    public IPage<ShareVO> getCollects(Long userId, Integer pageNum, Integer pageSize) {
        Page<ShareCollect> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ShareCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareCollect::getUserId, userId)
                .orderByDesc(ShareCollect::getCreateTime);
        Page<ShareCollect> collectPage = this.page(page);

        // 获取分享ID列表
        List<Long> shareIds = collectPage.getRecords().stream()
                .map(ShareCollect::getShareId)
                .collect(Collectors.toList());

        if (shareIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 查询分享详情
        List<Share> shares = shareService.listByIds(shareIds);
        Map<Long, Share> shareMap = shares.stream()
                .collect(Collectors.toMap(Share::getId, share -> share));

        Page<ShareVO> voPage = new Page<>(collectPage.getCurrent(), collectPage.getSize(), collectPage.getTotal());
        List<ShareVO> voList = collectPage.getRecords().stream()
                .map(collect -> {
                    Share share = shareMap.get(collect.getShareId());
                    if (share != null) {
                        ShareVO vo = new ShareVO();
                        vo.setShareId(share.getId());
                        vo.setContent(share.getContent());
                        vo.setLikeCount(share.getLikeCount());
                        vo.setCommentCount(share.getCommentCount());
                        vo.setForwardCount(share.getForwardCount());
                        vo.setCollectCount(share.getCollectCount());
                        vo.setCreateTime(share.getCreateTime());
                        vo.setIsLiked(false);
                        vo.setIsCollected(true);
                        return vo;
                    }
                    return null;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }
}
