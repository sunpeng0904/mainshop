package com.online.mall.controller.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.online.mall.common.Result;
import com.online.mall.service.share.NotificationService;
import com.online.mall.vo.share.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/notification")
@Validated
@Tag(name = "通知管理", description = "通知相关接口")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping("/list")
    public Result<IPage<NotificationVO>> getNotifications(
            HttpServletRequest request,
            @Parameter(description = "通知类型") @RequestParam(required = false) String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<NotificationVO> page = notificationService.getNotifications(userId, type, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Integer> count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @Operation(summary = "标记通知已读")
    @PutMapping("/{notificationId}/read")
    public Result<Void> markAsRead(
            HttpServletRequest request,
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(notificationId, userId);
        return Result.success(null, "标记成功");
    }

    @Operation(summary = "标记所有通知已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(
            HttpServletRequest request,
            @Parameter(description = "通知类型") @RequestParam(required = false) String type) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId, type);
        return Result.success(null, "标记成功");
    }
}
