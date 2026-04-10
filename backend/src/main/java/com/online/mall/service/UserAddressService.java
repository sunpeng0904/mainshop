package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddress;
import com.online.mall.vo.AddressVO;

import java.util.List;

/**
 * 用户地址服务接口
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户地址列表
     */
    List<AddressVO> getAddressList(Long userId);

    /**
     * 获取地址详情
     */
    AddressVO getAddressById(Long userId, Long addressId);

    /**
     * 添加地址
     */
    AddressVO addAddress(Long userId, AddressDTO addressDTO);

    /**
     * 更新地址
     */
    AddressVO updateAddress(Long userId, AddressDTO addressDTO);

    /**
     * 删除地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);

    /**
     * 获取默认地址
     */
    AddressVO getDefaultAddress(Long userId);
}
