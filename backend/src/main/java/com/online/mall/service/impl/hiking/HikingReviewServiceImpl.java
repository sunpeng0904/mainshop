package com.online.mall.service.impl.hiking;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.hiking.HikingReview;
import com.online.mall.mapper.hiking.HikingReviewMapper;
import com.online.mall.service.hiking.HikingReviewService;
import com.online.mall.service.hiking.HikingRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路线评价Service实现类
 */
@Service
public class HikingReviewServiceImpl extends ServiceImpl<HikingReviewMapper, HikingReview> implements HikingReviewService {

    @Autowired
    private HikingRouteService hikingRouteService;

    @Override
    public boolean submitReview(Long userId, String username, String avatar, HikingReview review) {
        review.setUserId(userId);
        review.setUsername(username);
        review.setAvatar(avatar);
        review.setStatus("pending");
        boolean saved = save(review);
        if (saved) {
            // 更新路线评分
            updateRouteRating(review.getRouteId());
        }
        return saved;
    }

    @Override
    public Page<HikingReview> getReviewList(Integer page, Integer pageSize, Long routeId, String status) {
        Page<HikingReview> pageParam = new Page<>(page, pageSize);
        return baseMapper.selectReviewPage(pageParam, routeId, status);
    }

    @Override
    public boolean approveReview(Long id) {
        HikingReview review = getById(id);
        if (review == null) {
            return false;
        }
        review.setStatus("approved");
        boolean updated = updateById(review);
        if (updated) {
            updateRouteRating(review.getRouteId());
        }
        return updated;
    }

    @Override
    public boolean rejectReview(Long id) {
        HikingReview review = new HikingReview();
        review.setId(id);
        review.setStatus("rejected");
        return updateById(review);
    }

    @Override
    public boolean replyReview(Long id, String reply) {
        return baseMapper.updateReply(id, reply) > 0;
    }

    @Override
    public Map<String, Object> getRatingStats(Long routeId) {
        Map<String, Object> stats = new HashMap<>();
        List<Map<String, Object>> ratingList = baseMapper.selectRatingStats(routeId);

        int total = 0;
        int[] counts = new int[6]; // 1-5星
        for (Map<String, Object> item : ratingList) {
            Integer rating = (Integer) item.get("rating");
            Long count = (Long) item.get("count");
            counts[rating] = count.intValue();
            total += count.intValue();
        }

        stats.put("total", total);
        stats.put("counts", counts);
        stats.put("average", baseMapper.selectAverageRating(routeId));
        return stats;
    }

    @Override
    public void updateRouteRating(Long routeId) {
        Double avgRating = baseMapper.selectAverageRating(routeId);
        if (avgRating == null) {
            avgRating = 5.0;
        }
        BigDecimal rating = BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP);

        // 查询评价数量
        long reviewCount = count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<HikingReview>()
                .eq("route_id", routeId)
                .eq("status", "approved"));

        // 更新路线评分
        hikingRouteService.updateRating(routeId, rating, (int) reviewCount);
    }
}
