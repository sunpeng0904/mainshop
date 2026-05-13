package com.online.mall.controller.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.online.mall.common.Result;
import com.online.mall.service.share.FriendshipService;
import com.online.mall.vo.share.UserSimpleVO;
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
 * 好友控制器
 */
@Slf4j
@RestController
@RequestMapping("/friend")
@Validated
@Tag(name = "好友管理", description = "好友相关接口")
public class FriendController {

    @Autowired
    private FriendshipService friendshipService;

    @Operation(summary = "关注用户")
    @PostMapping("/follow/{userId}")
    public Result<Map<String, Object>> followUser(
            HttpServletRequest request,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        Long currentUserId = (Long) request.getAttribute("userId");
        Map<String, Object> result = friendshipService.followUser(currentUserId, userId);
        return Result.success(result, "关注成功");
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/follow/{userId}")
    public Result<Void> unfollowUser(
            HttpServletRequest request,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        Long currentUserId = (Long) request.getAttribute("userId");
        friendshipService.unfollowUser(currentUserId, userId);
        return Result.success(null, "取消关注成功");
    }

    @Operation(summary = "获取关注列表")
    @GetMapping("/following")
    public Result<IPage<UserSimpleVO>> getFollowing(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<UserSimpleVO> page = friendshipService.getFollowing(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取粉丝列表")
    @GetMapping("/followers")
    public Result<IPage<UserSimpleVO>> getFollowers(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<UserSimpleVO> page = friendshipService.getFollowers(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取好友列表（互关）")
    @GetMapping("/friends")
    public Result<IPage<UserSimpleVO>> getFriends(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<UserSimpleVO> page = friendshipService.getFriends(userId, pageNum, pageSize);
        return Result.success(page);
    }
}
