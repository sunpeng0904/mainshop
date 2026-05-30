package com.online.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.mall.entity.UserAddr;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户地址Mapper（符合ATTRC2E词根规范）
 */
@Mapper
public interface UserAddrMapper extends BaseMapper<UserAddr> {
}
