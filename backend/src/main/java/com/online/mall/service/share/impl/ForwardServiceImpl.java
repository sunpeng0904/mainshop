package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.dto.share.ForwardCreateDTO;
import com.online.mall.dto.share.ShareCreateDTO;
import com.online.mall.entity.share.Share;
import com.online.mall.entity.share.ShareForward;
import com.online.mall.mapper.ShareForwardMapper;
import com.online.mall.service.share.ForwardService;
import com.online.mall.service.share.LikeService;
import com.online.mall.service.share.NotificationService;
import com.online.mall.service.share.ShareService;
import com.online.mall.vo.share.ShareVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 转发服务实现类
 */
@Slf4j
@Service
public class ForwardServiceImpl extends ServiceImpl<ShareForwardMapper, ShareForward> implements ForwardService {

    @Lazy
    @Autowired
    private ShareService shareService;

    @Lazy
    @Autowired
    private LikeService likeService;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareVO forwardShare(Long shareId, Long userId, ForwardCreateDTO dto) {
        Share originalShare = shareService.getById(shareId);
        if (originalShare == null) {
            throw new RuntimeException("分享不存在");
        }

        // 创建转发分享
        ShareCreateDTO createDTO = new ShareCreateDTO();
        createDTO.setContent(dto.getContent() != null ? dto.getContent() : "转发分享");
        createDTO.setVisibility(1); // 公开
        Share newShare = new Share();
        newShare.setUserId(userId);
        newShare.setContent(createDTO.getContent());
        newShare.setVisibility(1);
        newShare.setLikeCount(0);
        newShare.setCommentCount(0);
        newShare.setForwardCount(0);
        newShare.setCollectCount(0);
        newShare.setStatus(1);
        shareService.save(newShare);

        // 保存转发记录
        ShareForward forward = new ShareForward();
        forward.setShareId(shareId);
        forward.setUserId(userId);
        forward.setContent(dto.getContent());
        forward.setNewShareId(newShare.getId());
        this.save(forward);

        // 更新原分享转发数
        originalShare.setForwardCount(originalShare.getForwardCount() + 1);
        shareService.updateById(originalShare);

        // 如果同时点赞
        if (Boolean.TRUE.equals(dto.getAlsoLike())) {
            likeService.toggleLike(shareId, userId);
        }

        // 发送通知
        if (!originalShare.getUserId().equals(userId)) {
            notificationService.sendNotification(originalShare.getUserId(), "FORWARD", userId, shareId, "转发了你的分享");
        }

        return shareService.getShareDetail(newShare.getId(), userId);
    }

    @Override
    public IPage<ShareVO> getForwards(Long shareId, Integer pageNum, Integer pageSize) {
        Page<ShareForward> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ShareForward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareForward::getShareId, shareId)
                .orderByDesc(ShareForward::getCreateTime);
        Page<ShareForward> forwardPage = this.page(page);

        Page<ShareVO> voPage = new Page<>(forwardPage.getCurrent(), forwardPage.getSize(), forwardPage.getTotal());
        List<ShareVO> voList = new ArrayList<>();
        for (ShareForward forward : forwardPage.getRecords()) {
            if (forward.getNewShareId() != null) {
                ShareVO vo = shareService.getShareDetail(forward.getNewShareId(), null);
                if (vo != null) {
                    voList.add(vo);
                }
            }
        }

        voPage.setRecords(voList);
        return voPage;
    }
}
