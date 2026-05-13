package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.share.ShareComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分享评论Mapper接口
 */
@Mapper
public interface ShareCommentMapper extends BaseMapper<ShareComment> {
}
