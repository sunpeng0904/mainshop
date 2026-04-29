package com.online.mall.controller.hiking;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.entity.User;
import com.online.mall.entity.hiking.HikingFavorite;
import com.online.mall.entity.hiking.HikingReview;
import com.online.mall.entity.hiking.HikingRoute;
import com.online.mall.service.hiking.HikingFavoriteService;
import com.online.mall.service.hiking.HikingReviewService;
import com.online.mall.service.hiking.HikingRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 徒步路线Controller
 */
@RestController
@RequestMapping("/hiking")
public class HikingController {

    @Autowired
    private HikingRouteService hikingRouteService;

    @Autowired
    private HikingFavoriteService hikingFavoriteService;

    @Autowired
    private HikingReviewService hikingReviewService;

    // ==================== 路线管理 ====================

    /**
     * 获取路线列表
     */
    @GetMapping("/routes")
    public Result<Page<HikingRoute>> getRouteList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword) {

        Page<HikingRoute> routePage = hikingRouteService.getRouteList(page, pageSize, difficulty, location, keyword, "published");
        return Result.success(routePage);
    }

    /**
     * 获取路线详情
     */
    @GetMapping("/routes/{id}")
    public Result<HikingRoute> getRouteDetail(@PathVariable Long id) {
        HikingRoute route = hikingRouteService.getRouteDetail(id);
        if (route == null) {
            return Result.error("路线不存在");
        }
        return Result.success(route);
    }

    /**
     * 获取热门路线
     */
    @GetMapping("/routes/hot")
    public Result<List<HikingRoute>> getHotRoutes(@RequestParam(defaultValue = "8") Integer limit) {
        List<HikingRoute> routes = hikingRouteService.getHotRoutes(limit);
        return Result.success(routes);
    }

    /**
     * 获取路线轨迹数据
     */
    @GetMapping("/routes/{id}/track")
    public Result<String> getRouteTrack(@PathVariable Long id) {
        HikingRoute route = hikingRouteService.getRouteDetail(id);
        if (route == null) {
            return Result.error("路线不存在");
        }
        return Result.success(route.getTrackData());
    }

    // ==================== 收藏功能 ====================

    /**
     * 收藏路线
     */
    @PostMapping("/routes/{id}/favorite")
    public Result<Void> favoriteRoute(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        boolean success = hikingFavoriteService.favoriteRoute(currentUser.getId(), id);
        return success ? Result.success() : Result.error("收藏失败");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/routes/{id}/favorite")
    public Result<Void> unfavoriteRoute(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        boolean success = hikingFavoriteService.unfavoriteRoute(currentUser.getId(), id);
        return success ? Result.success() : Result.error("取消收藏失败");
    }

    /**
     * 获取用户收藏的路线
     */
    @GetMapping("/favorites")
    public Result<List<Map<String, Object>>> getUserFavorites() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        List<Map<String, Object>> favorites = hikingFavoriteService.getUserFavorites(currentUser.getId());
        return Result.success(favorites);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/routes/{id}/favorite/status")
    public Result<Boolean> checkFavoriteStatus(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.success(false);
        }
        boolean isFavorited = hikingFavoriteService.isFavorited(currentUser.getId(), id);
        return Result.success(isFavorited);
    }

    // ==================== 评价功能 ====================

    /**
     * 获取路线评价列表
     */
    @GetMapping("/routes/{id}/reviews")
    public Result<Page<HikingReview>> getRouteReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<HikingReview> reviewPage = hikingReviewService.getReviewList(page, pageSize, id, "approved");
        return Result.success(reviewPage);
    }

    /**
     * 提交评价
     */
    @PostMapping("/routes/{id}/reviews")
    public Result<Void> submitReview(@PathVariable Long id, @RequestBody HikingReview review) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        review.setRouteId(id);
        boolean success = hikingReviewService.submitReview(
                currentUser.getId(),
                currentUser.getNickname(),
                currentUser.getAvatar(),
                review
        );
        return success ? Result.success() : Result.error("提交失败");
    }

    // ==================== 私有方法 ====================

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
