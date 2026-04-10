package com.online.mall.controller;

import com.online.mall.common.Result;
import com.online.mall.dto.ChangePasswordDTO;
import com.online.mall.dto.ResetPasswordDTO;
import com.online.mall.dto.UserLoginDTO;
import com.online.mall.dto.UserRegisterDTO;
import com.online.mall.dto.UserUpdateDTO;
import com.online.mall.service.UserService;
import com.online.mall.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return Result.success(userVO, "注册成功");
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token, "登录成功");
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<UserVO> updateUserInfo(HttpServletRequest request,
                                        @Valid @RequestBody UserUpdateDTO updateDTO) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        UserVO userVO = userService.updateUserInfo(userId, updateDTO);
        return Result.success(userVO, "更新成功");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request,
                                      @Valid @RequestBody ChangePasswordDTO passwordDTO) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.unauthorized("用户未登录");
        }
        userService.changePassword(userId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return Result.success(null, "密码修改成功");
    }

    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO passwordDTO) {
        userService.resetPassword(passwordDTO.getEmail(), passwordDTO.getNewPassword());
        return Result.success(null, "密码重置成功，请使用新密码登录");
    }

    @Operation(summary = "检查用户名是否已存在")
    @GetMapping("/check/username")
    public Result<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = userService.checkUsernameExists(username);
        return Result.success(exists);
    }

    @Operation(summary = "检查邮箱是否已存在")
    @GetMapping("/check/email")
    public Result<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email);
        return Result.success(exists);
    }

    @Operation(summary = "检查手机号是否已存在")
    @GetMapping("/check/phone")
    public Result<Boolean> checkPhoneExists(@RequestParam String phone) {
        boolean exists = userService.checkPhoneExists(phone);
        return Result.success(exists);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            userService.logout(userId);
        }
        return Result.success(null, "登出成功");
    }

    @Operation(summary = "服务健康检查")
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("User service is healthy");
    }
}