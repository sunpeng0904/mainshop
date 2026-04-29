package com.online.mall.service.hiking;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.hiking.HikingFavorite;

import java.util.List;
import java.util.Map;

/**
 * 路线收藏Service接口
 */
public interface HikingFavoriteService extends IService<HikingFavorite> {

    /**
     * 收藏路线
     */
    boolean favoriteRoute(Long userId, Long routeId);

    /**
     * 取消收藏
     */
    boolean unfavoriteRoute(Long userId, Long routeId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long routeId);

    /**
     * 获取用户收藏列表
     */
    List<Map<String, Object>> getUserFavorites(Long userId);
}
