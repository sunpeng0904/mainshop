package com.online.mall.entity.share;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 分享转发实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("share_forward")
public class ShareForward {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原分享ID
     */
    private Long shareId;

    /**
     * 转发者ID
     */
    private Long userId;

    /**
     * 转发评论
     */
    private String content;

    /**
     * 新分享ID
     */
    private Long newShareId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
