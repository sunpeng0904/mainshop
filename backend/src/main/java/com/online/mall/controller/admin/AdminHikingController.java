package com.online.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.entity.hiking.HikingReview;
import com.online.mall.entity.hiking.HikingRoute;
import com.online.mall.service.hiking.HikingReviewService;
import com.online.mall.service.hiking.HikingRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台管理 - 徒步路线管理Controller
 */
@RestController
@RequestMapping("/admin/hiking")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHikingController {

    @Autowired
    private HikingRouteService hikingRouteService;

    @Autowired
    private HikingReviewService hikingReviewService;

    // ==================== 路线管理 ====================

    /**
     * 获取路线列表（管理后台）
     */
    @GetMapping("/routes")
    public Result<Page<HikingRoute>> getRouteList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status) {

        Page<HikingRoute> routePage = hikingRouteService.getRouteList(page, pageSize, difficulty, location, name, status);
        return Result.success(routePage);
    }

    /**
     * 创建路线
     */
    @PostMapping("/routes")
    public Result<Void> createRoute(@RequestBody HikingRoute route) {
        boolean success = hikingRouteService.createRoute(route);
        return success ? Result.success() : Result.error("创建失败");
    }

    /**
     * 更新路线
     */
    @PutMapping("/routes/{id}")
    public Result<Void> updateRoute(@PathVariable Long id, @RequestBody HikingRoute route) {
        route.setId(id);
        boolean success = hikingRouteService.updateRoute(route);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除路线
     */
    @DeleteMapping("/routes/{id}")
    public Result<Void> deleteRoute(@PathVariable Long id) {
        boolean success = hikingRouteService.deleteRoute(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 切换路线上/下线
     */
    @PutMapping("/routes/{id}/status")
    public Result<Void> toggleRouteStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = hikingRouteService.toggleStatus(id, status);
        return success ? Result.success() : Result.error("操作失败");
    }

    // ==================== 评价管理 ====================

    /**
     * 获取评价列表（管理后台）
     */
    @GetMapping("/reviews")
    public Result<Page<HikingReview>> getReviewList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) String status) {

        Page<HikingReview> reviewPage = hikingReviewService.getReviewList(page, pageSize, routeId, status);
        return Result.success(reviewPage);
    }

    /**
     * 审核通过评价
     */
    @PutMapping("/reviews/{id}/approve")
    public Result<Void> approveReview(@PathVariable Long id) {
        boolean success = hikingReviewService.approveReview(id);
        return success ? Result.success() : Result.error("审核失败");
    }

    /**
     * 拒绝评价
     */
    @PutMapping("/reviews/{id}/reject")
    public Result<Void> rejectReview(@PathVariable Long id) {
        boolean success = hikingReviewService.rejectReview(id);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 回复评价
     */
    @PutMapping("/reviews/{id}/reply")
    public Result<Void> replyReview(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String reply = params.get("reply");
        boolean success = hikingReviewService.replyReview(id, reply);
        return success ? Result.success() : Result.error("回复失败");
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/reviews/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        boolean success = hikingReviewService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取评分统计
     */
    @GetMapping("/routes/{id}/rating-stats")
    public Result<Map<String, Object>> getRatingStats(@PathVariable Long id) {
        Map<String, Object> stats = hikingReviewService.getRatingStats(id);
        return Result.success(stats);
    }
}
