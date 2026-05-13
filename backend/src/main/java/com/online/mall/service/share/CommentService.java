package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.share.CommentCreateDTO;
import com.online.mall.entity.share.ShareComment;
import com.online.mall.vo.share.CommentVO;

import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<ShareComment> {

    /**
     * 发表评论
     */
    CommentVO createComment(Long shareId, Long userId, CommentCreateDTO dto);

    /**
     * 获取评论列表（一级评论）
     */
    IPage<CommentVO> getComments(Long shareId, Long currentUserId, Integer pageNum, Integer pageSize);

    /**
     * 获取评论的回复列表
     */
    List<CommentVO> getReplies(Long commentId, Long currentUserId, Integer limit);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 点赞/取消点赞评论
     */
    Map<String, Object> toggleCommentLike(Long commentId, Long userId);
}
