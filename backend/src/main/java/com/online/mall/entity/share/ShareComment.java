package com.online.mall.entity.share;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分享评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("share_comment")
public class ShareComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 评论者ID
     */
    private Long userId;

    /**
     * 父评论ID，0表示一级评论
     */
    private Long parentId;

    /**
     * 回复用户ID
     */
    private Long replyUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态: 0删除 1正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
