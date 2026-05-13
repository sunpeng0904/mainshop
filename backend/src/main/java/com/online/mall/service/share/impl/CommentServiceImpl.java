package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.dto.share.CommentCreateDTO;
import com.online.mall.entity.User;
import com.online.mall.entity.share.Share;
import com.online.mall.entity.share.ShareComment;
import com.online.mall.mapper.ShareCommentMapper;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.share.CommentService;
import com.online.mall.service.share.NotificationService;
import com.online.mall.service.share.ShareService;
import com.online.mall.vo.share.CommentVO;
import com.online.mall.vo.share.UserSimpleVO;
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
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<ShareCommentMapper, ShareComment> implements CommentService {

    @Lazy
    @Autowired
    private ShareService shareService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(Long shareId, Long userId, CommentCreateDTO dto) {
        Share share = shareService.getById(shareId);
        if (share == null) {
            throw new RuntimeException("分享不存在");
        }

        // 创建评论
        ShareComment comment = new ShareComment();
        comment.setShareId(shareId);
        comment.setUserId(userId);
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        comment.setReplyUserId(dto.getReplyUserId() != null ? dto.getReplyUserId() : 0L);
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        this.save(comment);

        // 更新分享评论数
        share.setCommentCount(share.getCommentCount() + 1);
        shareService.updateById(share);

        // 发送通知
        if (!share.getUserId().equals(userId)) {
            notificationService.sendNotification(share.getUserId(), "COMMENT", userId, shareId, "评论了你的分享: " + dto.getContent());
        }

        // 如果是回复评论，也通知被回复者
        if (dto.getReplyUserId() != null && dto.getReplyUserId() > 0 && !dto.getReplyUserId().equals(userId)) {
            notificationService.sendNotification(dto.getReplyUserId(), "COMMENT", userId, shareId, "回复了你的评论: " + dto.getContent());
        }

        return convertToCommentVO(comment, userId);
    }

    @Override
    public IPage<CommentVO> getComments(Long shareId, Long currentUserId, Integer pageNum, Integer pageSize) {
        Page<ShareComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ShareComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareComment::getShareId, shareId)
                .eq(ShareComment::getParentId, 0) // 一级评论
                .eq(ShareComment::getStatus, 1)
                .orderByDesc(ShareComment::getCreateTime);
        Page<ShareComment> commentPage = this.page(page);

        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        List<CommentVO> voList = commentPage.getRecords().stream().map(comment -> {
            CommentVO vo = convertToCommentVO(comment, currentUserId);
            // 获取子评论
            List<CommentVO> replies = getReplies(comment.getId(), currentUserId, 3);
            vo.setReplyList(replies);
            vo.setReplyCount(getReplyCount(comment.getId()));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<CommentVO> getReplies(Long commentId, Long currentUserId, Integer limit) {
        LambdaQueryWrapper<ShareComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareComment::getParentId, commentId)
                .eq(ShareComment::getStatus, 1)
                .orderByAsc(ShareComment::getCreateTime)
                .last("LIMIT " + limit);
        List<ShareComment> replies = this.list(wrapper);

        return replies.stream()
                .map(reply -> convertToCommentVO(reply, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        ShareComment comment = this.getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }

        this.removeById(commentId);

        // 更新分享评论数
        Share share = shareService.getById(comment.getShareId());
        if (share != null) {
            share.setCommentCount(Math.max(0, share.getCommentCount() - 1));
            shareService.updateById(share);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleCommentLike(Long commentId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        ShareComment comment = this.getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 简化实现：直接更新点赞数
        comment.setLikeCount(comment.getLikeCount() + 1);
        this.updateById(comment);

        result.put("likeCount", comment.getLikeCount());
        result.put("isLiked", true);

        return result;
    }

    /**
     * 获取回复数量
     */
    private int getReplyCount(Long commentId) {
        LambdaQueryWrapper<ShareComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareComment::getParentId, commentId)
                .eq(ShareComment::getStatus, 1);
        return (int) this.count(wrapper);
    }

    /**
     * 转换为CommentVO
     */
    private CommentVO convertToCommentVO(ShareComment comment, Long currentUserId) {
        CommentVO vo = new CommentVO();
        vo.setCommentId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount());
        vo.setIsLiked(false); // 简化实现
        vo.setCreateTime(comment.getCreateTime());

        // 用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            UserSimpleVO userVO = new UserSimpleVO();
            userVO.setUserId(user.getId());
            userVO.setNickname(user.getUsername());
            userVO.setAvatar(user.getAvatar());
            vo.setUser(userVO);
        }

        // 回复用户信息
        if (comment.getReplyUserId() != null && comment.getReplyUserId() > 0) {
            User replyUser = userMapper.selectById(comment.getReplyUserId());
            if (replyUser != null) {
                UserSimpleVO replyUserVO = new UserSimpleVO();
                replyUserVO.setUserId(replyUser.getId());
                replyUserVO.setNickname(replyUser.getUsername());
                replyUserVO.setAvatar(replyUser.getAvatar());
                vo.setReplyUser(replyUserVO);
            }
        }

        return vo;
    }
}
