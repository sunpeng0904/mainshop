package com.online.mall.service.impl.hiking;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.hiking.HikingRoute;
import com.online.mall.mapper.hiking.HikingRouteMapper;
import com.online.mall.service.hiking.HikingRouteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 徒步路线Service实现类
 */
@Service
public class HikingRouteServiceImpl extends ServiceImpl<HikingRouteMapper, HikingRoute> implements HikingRouteService {

    @Override
    public Page<HikingRoute> getRouteList(Integer page, Integer pageSize, String difficulty,
                                           String location, String keyword, String status) {
        Page<HikingRoute> pageParam = new Page<>(page, pageSize);
        return baseMapper.selectRoutePage(pageParam, difficulty, location, keyword, status);
    }

    @Override
    public HikingRoute getRouteDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<HikingRoute> getHotRoutes(Integer limit) {
        return baseMapper.selectHotRoutes(limit);
    }

    @Override
    public boolean createRoute(HikingRoute route) {
        route.setRating(java.math.BigDecimal.valueOf(5.0));
        route.setReviewCount(0);
        return save(route);
    }

    @Override
    public boolean updateRoute(HikingRoute route) {
        return updateById(route);
    }

    @Override
    public boolean deleteRoute(Long id) {
        return removeById(id);
    }

    @Override
    public boolean toggleStatus(Long id, String status) {
        HikingRoute route = new HikingRoute();
        route.setId(id);
        route.setStatus(status);
        return updateById(route);
    }

    @Override
    public boolean updateRating(Long routeId, java.math.BigDecimal rating, Integer reviewCount) {
        return baseMapper.updateRating(routeId, rating, reviewCount) > 0;
    }
}
