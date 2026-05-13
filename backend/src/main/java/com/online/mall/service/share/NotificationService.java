package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.share.Notification;
import com.online.mall.vo.share.NotificationVO;

import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 发送通知
     */
    void sendNotification(Long userId, String type, Long senderId, Long relatedId, String content);

    /**
     * 获取通知列表
     */
    IPage<NotificationVO> getNotifications(Long userId, String type, Integer pageNum, Integer pageSize);

    /**
     * 获取未读通知数量
     */
    Map<String, Integer> getUnreadCount(Long userId);

    /**
     * 标记通知已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知已读
     */
    void markAllAsRead(Long userId, String type);
}
