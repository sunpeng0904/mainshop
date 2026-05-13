package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.User;
import com.online.mall.entity.share.Notification;
import com.online.mall.entity.share.Share;
import com.online.mall.mapper.NotificationMapper;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.share.NotificationService;
import com.online.mall.service.share.ShareService;
import com.online.mall.vo.share.NotificationVO;
import com.online.mall.vo.share.UserSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private UserMapper userMapper;

    @Lazy
    @Autowired
    private ShareService shareService;

    @Override
    public void sendNotification(Long userId, String type, Long senderId, Long relatedId, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setSenderId(senderId);
        notification.setRelatedId(relatedId);
        notification.setContent(content);
        notification.setIsRead(0);
        this.save(notification);
    }

    @Override
    public IPage<NotificationVO> getNotifications(Long userId, String type, Integer pageNum, Integer pageSize) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notification::getType, type);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        Page<Notification> notificationPage = this.page(page);

        Page<NotificationVO> voPage = new Page<>(notificationPage.getCurrent(), notificationPage.getSize(), notificationPage.getTotal());
        List<NotificationVO> voList = notificationPage.getRecords().stream()
                .map(this::convertToNotificationVO)
                .collect(java.util.stream.Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Map<String, Integer> getUnreadCount(Long userId) {
        Map<String, Integer> result = new HashMap<>();

        // 总未读数
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        int totalUnread = (int) this.count(wrapper);
        result.put("total", totalUnread);

        // 各类型未读数
        String[] types = {"LIKE", "COMMENT", "FORWARD", "FOLLOW", "MENTION"};
        for (String type : types) {
            LambdaQueryWrapper<Notification> typeWrapper = new LambdaQueryWrapper<>();
            typeWrapper.eq(Notification::getUserId, userId)
                    .eq(Notification::getType, type)
                    .eq(Notification::getIsRead, 0);
            result.put(type.toLowerCase(), (int) this.count(typeWrapper));
        }

        return result;
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, 1);
        this.update(wrapper);
    }

    @Override
    public void markAllAsRead(Long userId, String type) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notification::getType, type);
        }
        wrapper.set(Notification::getIsRead, 1);
        this.update(wrapper);
    }

    /**
     * 转换为NotificationVO
     */
    private NotificationVO convertToNotificationVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setNotificationId(notification.getId());
        vo.setType(notification.getType());
        vo.setContent(notification.getContent());
        vo.setIsRead(notification.getIsRead() == 1);
        vo.setCreateTime(notification.getCreateTime());

        // 发送者信息
        if (notification.getSenderId() != null) {
            User sender = userMapper.selectById(notification.getSenderId());
            if (sender != null) {
                UserSimpleVO senderVO = new UserSimpleVO();
                senderVO.setUserId(sender.getId());
                senderVO.setNickname(sender.getUsername());
                senderVO.setAvatar(sender.getAvatar());
                vo.setSender(senderVO);
            }
        }

        // 分享摘要
        if (notification.getRelatedId() != null) {
            Share share = shareService.getById(notification.getRelatedId());
            if (share != null && share.getContent() != null) {
                String summary = share.getContent();
                vo.setShareSummary(summary.length() > 50 ? summary.substring(0, 50) + "..." : summary);
            }
        }

        return vo;
    }
}
