package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.share.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分享Mapper接口
 */
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

    /**
     * 获取好友ID列表对应的所有可见分享
     */
    @Select("<script>" +
            "SELECT * FROM share WHERE user_id IN " +
            "<foreach item='id' collection='friendIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND visibility IN (1, 2) AND status = 1 AND deleted = 0 " +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Share> selectFriendShares(@Param("friendIds") List<Long> friendIds);
}
