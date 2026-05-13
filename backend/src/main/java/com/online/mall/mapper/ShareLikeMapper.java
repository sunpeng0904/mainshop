package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.share.ShareLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分享点赞Mapper接口
 */
@Mapper
public interface ShareLikeMapper extends BaseMapper<ShareLike> {
}
