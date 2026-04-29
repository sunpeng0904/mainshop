package com.online.mall.mapper.hiking;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.entity.hiking.HikingRoute;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 徒步路线Mapper接口
 */
public interface HikingRouteMapper extends BaseMapper<HikingRoute> {

    /**
     * 分页查询路线列表
     */
    @Select("<script>" +
            "SELECT * FROM t_hiking_route " +
            "WHERE deleted = 0 " +
            "<if test='difficulty != null'> AND difficulty = #{difficulty} </if>" +
            "<if test='location != null'> AND location LIKE CONCAT('%', #{location}, '%') </if>" +
            "<if test='keyword != null'> AND (name LIKE CONCAT('%', #{keyword}, '%') OR location LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "ORDER BY is_hot DESC, rating DESC, id DESC" +
            "</script>")
    Page<HikingRoute> selectRoutePage(Page<HikingRoute> page,
                                       @Param("difficulty") String difficulty,
                                       @Param("location") String location,
                                       @Param("keyword") String keyword,
                                       @Param("status") String status);

    /**
     * 获取热门路线
     */
    @Select("SELECT * FROM t_hiking_route WHERE deleted = 0 AND status = 'published' AND is_hot = 1 ORDER BY rating DESC LIMIT #{limit}")
    List<HikingRoute> selectHotRoutes(@Param("limit") Integer limit);

    /**
     * 更新路线评分
     */
    @Update("UPDATE t_hiking_route SET rating = #{rating}, review_count = #{reviewCount} WHERE id = #{routeId}")
    int updateRating(@Param("routeId") Long routeId,
                     @Param("rating") java.math.BigDecimal rating,
                     @Param("reviewCount") Integer reviewCount);
}
