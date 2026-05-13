package com.online.mall.entity.share;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收者ID
     */
    private Long userId;

    /**
     * 通知类型: LIKE, COMMENT, FORWARD, FOLLOW, MENTION
     */
    private String type;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 关联内容ID
     */
    private Long relatedId;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 是否已读: 0未读 1已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
