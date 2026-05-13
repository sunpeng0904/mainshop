package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.User;
import com.online.mall.entity.share.Share;
import com.online.mall.entity.share.ShareLike;
import com.online.mall.mapper.ShareLikeMapper;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.share.LikeService;
import com.online.mall.service.share.NotificationService;
import com.online.mall.service.share.ShareService;
import com.online.mall.vo.share.UserSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点赞服务实现类
 */
@Slf4j
@Service
public class LikeServiceImpl extends ServiceImpl<ShareLikeMapper, ShareLike> implements LikeService {

    @Lazy
    @Autowired
    private ShareService shareService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleLike(Long shareId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 检查是否已点赞
        LambdaQueryWrapper<ShareLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareLike::getShareId, shareId)
                .eq(ShareLike::getUserId, userId);
        ShareLike existingLike = this.getOne(wrapper);

        Share share = shareService.getById(shareId);
        if (share == null) {
            throw new RuntimeException("分享不存在");
        }

        if (existingLike != null) {
            // 取消点赞
            this.removeById(existingLike.getId());
            share.setLikeCount(share.getLikeCount() - 1);
            result.put("isLiked", false);
        } else {
            // 点赞
            ShareLike like = new ShareLike();
            like.setShareId(shareId);
            like.setUserId(userId);
            this.save(like);
            share.setLikeCount(share.getLikeCount() + 1);
            result.put("isLiked", true);

            // 发送通知
            if (!share.getUserId().equals(userId)) {
                notificationService.sendNotification(share.getUserId(), "LIKE", userId, shareId, "赞了你的分享");
            }
        }

        shareService.updateById(share);
        result.put("likeCount", share.getLikeCount());

        return result;
    }

    @Override
    public boolean isLiked(Long shareId, Long userId) {
        if (userId == null) {
            return false;
        }
        LambdaQueryWrapper<ShareLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareLike::getShareId, shareId)
                .eq(ShareLike::getUserId, userId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Map<Long, Boolean> batchCheckLiked(List<Long> shareIds, Long userId) {
        Map<Long, Boolean> result = new HashMap<>();
        if (userId == null || shareIds == null || shareIds.isEmpty()) {
            return result;
        }

        LambdaQueryWrapper<ShareLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ShareLike::getShareId, shareIds)
                .eq(ShareLike::getUserId, userId);
        List<ShareLike> likes = this.list(wrapper);

        Map<Long, Boolean> likedMap = likes.stream()
                .collect(Collectors.toMap(ShareLike::getShareId, like -> true));

        for (Long shareId : shareIds) {
            result.put(shareId, likedMap.getOrDefault(shareId, false));
        }

        return result;
    }

    @Override
    public IPage<UserSimpleVO> getLikeUsers(Long shareId, Integer pageNum, Integer pageSize) {
        Page<ShareLike> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ShareLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareLike::getShareId, shareId)
                .orderByDesc(ShareLike::getCreateTime);
        Page<ShareLike> likePage = this.page(page, wrapper);

        Page<UserSimpleVO> voPage = new Page<>(likePage.getCurrent(), likePage.getSize(), likePage.getTotal());
        List<UserSimpleVO> voList = likePage.getRecords().stream().map(like -> {
            User user = userMapper.selectById(like.getUserId());
            UserSimpleVO vo = new UserSimpleVO();
            if (user != null) {
                vo.setUserId(user.getId());
                vo.setNickname(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }
}
