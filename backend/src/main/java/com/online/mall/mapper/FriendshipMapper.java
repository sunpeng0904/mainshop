package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.share.Friendship;
import org.apache.ibatis.annotations.Mapper;

/**
 * 好友关系Mapper接口
 */
@Mapper
public interface FriendshipMapper extends BaseMapper<Friendship> {
}
