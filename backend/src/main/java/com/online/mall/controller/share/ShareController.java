package com.online.mall.controller.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.online.mall.common.Result;
import com.online.mall.dto.share.CommentCreateDTO;
import com.online.mall.dto.share.ForwardCreateDTO;
import com.online.mall.dto.share.ShareCreateDTO;
import com.online.mall.service.share.*;
import com.online.mall.vo.share.CommentVO;
import com.online.mall.vo.share.ShareVO;
import com.online.mall.vo.share.UserSimpleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 分享控制器
 */
@Slf4j
@RestController
@RequestMapping("/share")
@Validated
@Tag(name = "分享管理", description = "分享相关接口")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CollectService collectService;

    @Autowired
    private ForwardService forwardService;

    @Operation(summary = "发布分享")
    @PostMapping
    public Result<ShareVO> createShare(
            HttpServletRequest request,
            @RequestBody @Valid ShareCreateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        ShareVO shareVO = shareService.createShare(userId, dto);
        return Result.success(shareVO, "发布成功");
    }

    @Operation(summary = "获取分享详情")
    @GetMapping("/{shareId}")
    public Result<ShareVO> getShareDetail(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId) {
        Long userId = (Long) request.getAttribute("userId");
        ShareVO shareVO = shareService.getShareDetail(shareId, userId);
        return Result.success(shareVO);
    }

    @Operation(summary = "获取我的分享")
    @GetMapping("/my")
    public Result<IPage<ShareVO>> getMyShares(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<ShareVO> page = shareService.getMyShares(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取好友圈动态")
    @GetMapping("/friends")
    public Result<IPage<ShareVO>> getFriendShares(
            HttpServletRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<ShareVO> page = shareService.getFriendShares(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取热门分享")
    @GetMapping("/hot")
    public Result<IPage<ShareVO>> getHotShares(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ShareVO> page = shareService.getHotShares(pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取用户分享列表")
    @GetMapping("/user/{userId}")
    public Result<IPage<ShareVO>> getUserShares(
            HttpServletRequest request,
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long currentUserId = (Long) request.getAttribute("userId");
        IPage<ShareVO> page = shareService.getUserShares(userId, currentUserId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "删除分享")
    @DeleteMapping("/{shareId}")
    public Result<Void> deleteShare(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId) {
        Long userId = (Long) request.getAttribute("userId");
        shareService.deleteShare(shareId, userId);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/{shareId}/like")
    public Result<Map<String, Object>> toggleLike(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = likeService.toggleLike(shareId, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取点赞列表")
    @GetMapping("/{shareId}/likes")
    public Result<IPage<UserSimpleVO>> getLikeUsers(
            @Parameter(description = "分享ID") @PathVariable Long shareId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        IPage<UserSimpleVO> page = likeService.getLikeUsers(shareId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "发表评论")
    @PostMapping("/{shareId}/comment")
    public Result<CommentVO> createComment(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId,
            @RequestBody @Valid CommentCreateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        CommentVO commentVO = commentService.createComment(shareId, userId, dto);
        return Result.success(commentVO, "评论成功");
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/{shareId}/comments")
    public Result<IPage<CommentVO>> getComments(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<CommentVO> page = commentService.getComments(shareId, userId, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{shareId}/comment/{commentId}")
    public Result<Void> deleteComment(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId,
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.deleteComment(commentId, userId);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/{shareId}/collect")
    public Result<Map<String, Object>> toggleCollect(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = collectService.toggleCollect(shareId, userId);
        return Result.success(result);
    }

    @Operation(summary = "转发分享")
    @PostMapping("/{shareId}/forward")
    public Result<ShareVO> forwardShare(
            HttpServletRequest request,
            @Parameter(description = "分享ID") @PathVariable Long shareId,
            @RequestBody @Valid ForwardCreateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        ShareVO shareVO = forwardService.forwardShare(shareId, userId, dto);
        return Result.success(shareVO, "转发成功");
    }
}
