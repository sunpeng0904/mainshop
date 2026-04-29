package com.online.mall.mapper.hiking;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.hiking.HikingFavorite;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 路线收藏Mapper接口
 */
public interface HikingFavoriteMapper extends BaseMapper<HikingFavorite> {

    /**
     * 查询用户是否收藏
     */
    @Select("SELECT COUNT(*) FROM t_hiking_favorite WHERE user_id = #{userId} AND route_id = #{routeId} AND deleted = 0")
    int checkFavorite(@Param("userId") Long userId, @Param("routeId") Long routeId);

    /**
     * 获取用户收藏列表
     */
    @Select("SELECT f.*, r.name as routeName, r.cover_image as coverImage, r.difficulty, r.location, r.distance, r.duration, r.rating " +
            "FROM t_hiking_favorite f " +
            "LEFT JOIN t_hiking_route r ON f.route_id = r.id " +
            "WHERE f.user_id = #{userId} AND f.deleted = 0 AND r.deleted = 0 " +
            "ORDER BY f.create_time DESC")
    List<java.util.Map<String, Object>> selectUserFavorites(@Param("userId") Long userId);
}
