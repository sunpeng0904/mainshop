package com.online.mall.service.hiking;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.hiking.HikingRoute;

import java.util.List;

/**
 * 徒步路线Service接口
 */
public interface HikingRouteService extends IService<HikingRoute> {

    /**
     * 分页查询路线列表
     */
    Page<HikingRoute> getRouteList(Integer page, Integer pageSize, String difficulty,
                                    String location, String keyword, String status);

    /**
     * 获取路线详情
     */
    HikingRoute getRouteDetail(Long id);

    /**
     * 获取热门路线
     */
    List<HikingRoute> getHotRoutes(Integer limit);

    /**
     * 创建路线
     */
    boolean createRoute(HikingRoute route);

    /**
     * 更新路线
     */
    boolean updateRoute(HikingRoute route);

    /**
     * 删除路线
     */
    boolean deleteRoute(Long id);

    /**
     * 切换路线上/下线状态
     */
    boolean toggleStatus(Long id, String status);

    /**
     * 更新路线评分和评价数量
     */
    boolean updateRating(Long routeId, java.math.BigDecimal rating, Integer reviewCount);
}
