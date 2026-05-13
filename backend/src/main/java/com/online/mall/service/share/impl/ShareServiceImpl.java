package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.dto.share.ShareCreateDTO;
import com.online.mall.entity.User;
import com.online.mall.entity.share.Share;
import com.online.mall.entity.share.ShareImage;
import com.online.mall.mapper.ShareMapper;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.share.*;
import com.online.mall.vo.share.ShareVO;
import com.online.mall.vo.share.UserSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分享服务实现类
 */
@Slf4j
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Autowired
    private ShareImageService shareImageService;

    @Lazy
    @Autowired
    private LikeService likeService;

    @Lazy
    @Autowired
    private CollectService collectService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareVO createShare(Long userId, ShareCreateDTO dto) {
        // 创建分享
        Share share = new Share();
        share.setUserId(userId);
        share.setContent(dto.getContent());
        share.setLocation(dto.getLocation());
        share.setVisibility(dto.getVisibility() != null ? dto.getVisibility() : 1);
        share.setLikeCount(0);
        share.setCommentCount(0);
        share.setForwardCount(0);
        share.setCollectCount(0);
        share.setStatus(1);
        this.save(share);

        // 保存图片
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<ShareImage> images = new ArrayList<>();
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                ShareImage image = new ShareImage();
                image.setShareId(share.getId());
                image.setImageUrl(dto.getImageUrls().get(i));
                image.setThumbUrl(dto.getImageUrls().get(i)); // 简化处理，实际需要生成缩略图
                image.setSortOrder(i);
                images.add(image);
            }
            shareImageService.saveBatch(images);
        }

        // 处理@提及
        if (dto.getMentionUserIds() != null && !dto.getMentionUserIds().isEmpty()) {
            for (Long mentionUserId : dto.getMentionUserIds()) {
                notificationService.sendNotification(mentionUserId, "MENTION", userId, share.getId(), "在分享中@了你");
            }
        }

        // 返回分享详情
        return getShareDetail(share.getId(), userId);
    }

    @Override
    public ShareVO getShareDetail(Long shareId, Long currentUserId) {
        Share share = this.getById(shareId);
        if (share == null || share.getDeleted() == 1) {
            throw new RuntimeException("分享不存在");
        }

        // 检查可见性
        if (!checkVisibility(shareId, currentUserId)) {
            throw new RuntimeException("无权查看该分享");
        }

        return convertToShareVO(share, currentUserId);
    }

    @Override
    public IPage<ShareVO> getMyShares(Long userId, Integer pageNum, Integer pageSize) {
        Page<Share> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getUserId, userId)
                .eq(Share::getStatus, 1)
                .orderByDesc(Share::getCreateTime);
        Page<Share> sharePage = this.page(page, wrapper);

        return convertToShareVOPage(sharePage, userId);
    }

    @Override
    public IPage<ShareVO> getFriendShares(Long userId, Integer pageNum, Integer pageSize) {
        // 获取好友ID列表
        List<Long> friendIds = friendshipService.getFriendIds(userId);

        // 构建用户ID列表（包含自己和好友）
        List<Long> userIds = new ArrayList<>(friendIds);
        userIds.add(userId);

        // 查询好友圈分享（包含自己）
        Page<Share> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Share::getUserId, userIds)
                .in(Share::getVisibility, 1, 2) // 公开或好友可见
                .eq(Share::getStatus, 1)
                .orderByDesc(Share::getCreateTime);
        Page<Share> sharePage = this.page(page, wrapper);

        return convertToShareVOPage(sharePage, userId);
    }

    @Override
    public IPage<ShareVO> getHotShares(Integer pageNum, Integer pageSize) {
        Page<Share> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getVisibility, 1) // 公开
                .eq(Share::getStatus, 1)
                .orderByDesc(Share::getLikeCount)
                .orderByDesc(Share::getCreateTime);
        Page<Share> sharePage = this.page(page, wrapper);

        return convertToShareVOPage(sharePage, null);
    }

    @Override
    public IPage<ShareVO> getUserShares(Long userId, Long currentUserId, Integer pageNum, Integer pageSize) {
        Page<Share> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getUserId, userId)
                .eq(Share::getStatus, 1);

        // 如果不是自己，需要检查可见性
        if (!userId.equals(currentUserId)) {
            boolean isFriend = friendshipService.isFriend(userId, currentUserId);
            if (isFriend) {
                wrapper.in(Share::getVisibility, 1, 2);
            } else {
                wrapper.eq(Share::getVisibility, 1);
            }
        }

        wrapper.orderByDesc(Share::getCreateTime);
        Page<Share> sharePage = this.page(page, wrapper);

        return convertToShareVOPage(sharePage, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShare(Long shareId, Long userId) {
        Share share = this.getById(shareId);
        if (share == null) {
            throw new RuntimeException("分享不存在");
        }
        if (!share.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该分享");
        }
        this.removeById(shareId);
    }

    @Override
    public boolean checkVisibility(Long shareId, Long viewerId) {
        Share share = this.getById(shareId);
        if (share == null || share.getDeleted() == 1) {
            return false;
        }

        // 自己的分享总是可见
        if (share.getUserId().equals(viewerId)) {
            return true;
        }

        // 根据可见范围判断
        switch (share.getVisibility()) {
            case 1: // 公开
                return true;
            case 2: // 好友可见
                return friendshipService.isFriend(share.getUserId(), viewerId);
            case 5: // 仅自己
                return false;
            default:
                return false;
        }
    }

    /**
     * 转换为ShareVO
     */
    private ShareVO convertToShareVO(Share share, Long currentUserId) {
        ShareVO vo = new ShareVO();
        BeanUtils.copyProperties(share, vo);
        vo.setShareId(share.getId());

        // 用户信息
        User user = userMapper.selectById(share.getUserId());
        if (user != null) {
            UserSimpleVO userVO = new UserSimpleVO();
            userVO.setUserId(user.getId());
            userVO.setNickname(user.getUsername());
            userVO.setAvatar(user.getAvatar());
            vo.setUser(userVO);
        }

        // 图片列表
        LambdaQueryWrapper<ShareImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.eq(ShareImage::getShareId, share.getId())
                .orderByAsc(ShareImage::getSortOrder);
        List<ShareImage> images = shareImageService.list(imageWrapper);
        vo.setImageUrls(images.stream().map(ShareImage::getImageUrl).collect(Collectors.toList()));

        // 互动状态
        if (currentUserId != null) {
            vo.setIsLiked(likeService.isLiked(share.getId(), currentUserId));
            vo.setIsCollected(collectService.isCollected(share.getId(), currentUserId));
        } else {
            vo.setIsLiked(false);
            vo.setIsCollected(false);
        }

        return vo;
    }

    /**
     * 转换分页结果
     */
    private IPage<ShareVO> convertToShareVOPage(Page<Share> sharePage, Long currentUserId) {
        Page<ShareVO> voPage = new Page<>(sharePage.getCurrent(), sharePage.getSize(), sharePage.getTotal());

        if (sharePage.getRecords().isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }

        // 批量查询互动状态
        List<Long> shareIds = sharePage.getRecords().stream()
                .map(Share::getId)
                .collect(Collectors.toList());

        Map<Long, Boolean> likedMap = currentUserId != null ?
                likeService.batchCheckLiked(shareIds, currentUserId) : null;
        Map<Long, Boolean> collectedMap = currentUserId != null ?
                collectService.batchCheckCollected(shareIds, currentUserId) : null;

        List<ShareVO> voList = sharePage.getRecords().stream().map(share -> {
            ShareVO vo = convertToShareVO(share, currentUserId);
            if (likedMap != null) {
                vo.setIsLiked(likedMap.getOrDefault(share.getId(), false));
            }
            if (collectedMap != null) {
                vo.setIsCollected(collectedMap.getOrDefault(share.getId(), false));
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }
}
