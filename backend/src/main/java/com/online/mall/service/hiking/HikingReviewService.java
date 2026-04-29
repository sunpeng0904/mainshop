package com.online.mall.service.hiking;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.hiking.HikingReview;

import java.util.Map;

/**
 * 路线评价Service接口
 */
public interface HikingReviewService extends IService<HikingReview> {

    /**
     * 提交评价
     */
    boolean submitReview(Long userId, String username, String avatar, HikingReview review);

    /**
     * 分页查询评价列表
     */
    Page<HikingReview> getReviewList(Integer page, Integer pageSize, Long routeId, String status);

    /**
     * 审核评价
     */
    boolean approveReview(Long id);

    /**
     * 拒绝评价
     */
    boolean rejectReview(Long id);

    /**
     * 回复评价
     */
    boolean replyReview(Long id, String reply);

    /**
     * 获取评分统计
     */
    Map<String, Object> getRatingStats(Long routeId);

    /**
     * 更新路线评分
     */
    void updateRouteRating(Long routeId);
}
