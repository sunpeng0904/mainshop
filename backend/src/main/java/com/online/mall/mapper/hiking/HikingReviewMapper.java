package com.online.mall.mapper.hiking;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.entity.hiking.HikingReview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 路线评价Mapper接口
 */
public interface HikingReviewMapper extends BaseMapper<HikingReview> {

    /**
     * 分页查询评价列表
     */
    @Select("<script>" +
            "SELECT r.*, u.nickname as userNickname " +
            "FROM t_hiking_review r " +
            "LEFT JOIN t_user u ON r.user_id = u.id " +
            "WHERE r.route_id = #{routeId} AND r.deleted = 0 " +
            "<if test='status != null'> AND r.status = #{status} </if>" +
            "ORDER BY r.create_time DESC" +
            "</script>")
    Page<HikingReview> selectReviewPage(Page<HikingReview> page,
                                        @Param("routeId") Long routeId,
                                        @Param("status") String status);

    /**
     * 查询路线评分统计
     */
    @Select("SELECT rating, COUNT(*) as count FROM t_hiking_review WHERE route_id = #{routeId} AND deleted = 0 AND status = 'approved' GROUP BY rating")
    List<Map<String, Object>> selectRatingStats(@Param("routeId") Long routeId);

    /**
     * 计算路线平均评分
     */
    @Select("SELECT AVG(rating) FROM t_hiking_review WHERE route_id = #{routeId} AND deleted = 0 AND status = 'approved'")
    Double selectAverageRating(@Param("routeId") Long routeId);

    /**
     * 更新评价状态
     */
    @Update("UPDATE t_hiking_review SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 回复评价
     */
    @Update("UPDATE t_hiking_review SET reply = #{reply}, reply_time = NOW() WHERE id = #{id}")
    int updateReply(@Param("id") Long id, @Param("reply") String reply);
}
