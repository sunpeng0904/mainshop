package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddr;
import com.online.mall.vo.AddressVO;

import java.util.List;

/**
 * 用户地址服务接口（符合ATTRC2E词根规范）
 */
public interface UserAddrService extends IService<UserAddr> {

    /**
     * 获取用户地址列表
     */
    List<AddressVO> getAddrList(String userId);

    /**
     * 获取地址详情
     */
    AddressVO getAddrById(String userId, String addrId);

    /**
     * 添加地址
     */
    AddressVO addAddr(String userId, AddressDTO addressDTO);

    /**
     * 更新地址
     */
    AddressVO updateAddr(String userId, AddressDTO addressDTO);

    /**
     * 删除地址
     */
    void deleteAddr(String userId, String addrId);

    /**
     * 设置默认地址
     */
    void setDftAddr(String userId, String addrId);

    /**
     * 获取默认地址
     */
    AddressVO getDftAddr(String userId);
}
