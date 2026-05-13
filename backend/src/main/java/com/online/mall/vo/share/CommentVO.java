package com.online.mall.vo.share;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
public class CommentVO {

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 用户信息
     */
    private UserSimpleVO user;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 回复的用户信息
     */
    private UserSimpleVO replyUser;

    /**
     * 子评论列表
     */
    private List<CommentVO> replyList;

    /**
     * 子评论总数
     */
    private Integer replyCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
