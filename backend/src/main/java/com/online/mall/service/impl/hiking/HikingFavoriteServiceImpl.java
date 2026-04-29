package com.online.mall.service.impl.hiking;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.hiking.HikingFavorite;
import com.online.mall.mapper.hiking.HikingFavoriteMapper;
import com.online.mall.service.hiking.HikingFavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 路线收藏Service实现类
 */
@Service
public class HikingFavoriteServiceImpl extends ServiceImpl<HikingFavoriteMapper, HikingFavorite> implements HikingFavoriteService {

    @Override
    public boolean favoriteRoute(Long userId, Long routeId) {
        // 检查是否已收藏
        if (isFavorited(userId, routeId)) {
            return true;
        }
        HikingFavorite favorite = new HikingFavorite();
        favorite.setUserId(userId);
        favorite.setRouteId(routeId);
        return save(favorite);
    }

    @Override
    public boolean unfavoriteRoute(Long userId, Long routeId) {
        QueryWrapper<HikingFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("route_id", routeId);
        return remove(wrapper);
    }

    @Override
    public boolean isFavorited(Long userId, Long routeId) {
        return baseMapper.checkFavorite(userId, routeId) > 0;
    }

    @Override
    public List<Map<String, Object>> getUserFavorites(Long userId) {
        return baseMapper.selectUserFavorites(userId);
    }
}
