package com.online.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.service.UserService;
import com.online.mall.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@Tag(name = "管理员-用户管理", description = "管理员用户相关接口")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public Result<Page<UserVO>> getUserList(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserVO> userPage = userService.getAdminUserList(username, phone, status, pageNum, pageSize);
        return Result.success(userPage);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        UserVO userVO = userService.getAdminUserById(userId);
        return Result.success(userVO);
    }

    @Operation(summary = "禁用用户")
    @PutMapping("/disable/{userId}")
    public Result<Void> disableUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        userService.disableUser(userId);
        return Result.success(null, "用户已禁用");
    }

    @Operation(summary = "启用用户")
    @PutMapping("/enable/{userId}")
    public Result<Void> enableUser(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        userService.enableUser(userId);
        return Result.success(null, "用户已启用");
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/reset-password/{userId}")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.adminResetPassword(userId, newPassword);
        return Result.success(null, "密码重置成功");
    }

    @Operation(summary = "获取用户统计")
    @GetMapping("/statistics")
    public Result<UserService.UserStatisticsVO> getUserStatistics() {
        UserService.UserStatisticsVO statistics = userService.getUserStatistics();
        return Result.success(statistics);
    }
}
