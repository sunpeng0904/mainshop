package com.online.mall.vo.share;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 */
@Data
public class NotificationVO {

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 通知类型
     */
    private String type;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 发送者信息
     */
    private UserSimpleVO sender;

    /**
     * 关联的分享内容摘要
     */
    private String shareSummary;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
