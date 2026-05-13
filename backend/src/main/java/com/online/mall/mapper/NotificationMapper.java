package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.share.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
