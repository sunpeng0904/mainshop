package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddress;
import com.online.mall.mapper.UserAddressMapper;
import com.online.mall.service.RegionService;
import com.online.mall.service.UserAddressService;
import com.online.mall.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户地址服务实现
 */
@Slf4j
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Autowired
    private RegionService regionService;

    @Override
    public List<AddressVO> getAddressList(Long userId) {
        log.info("获取用户地址列表: userId={}", userId);

        List<UserAddress> addresses = list(new QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .orderByDesc("is_default")
                .orderByDesc("create_time"));

        List<AddressVO> voList = new ArrayList<>();
        for (UserAddress address : addresses) {
            voList.add(convertToVO(address));
        }

        return voList;
    }

    @Override
    public AddressVO getAddressById(Long userId, Long addressId) {
        log.info("获取地址详情: userId={}, addressId={}", userId, addressId);

        UserAddress address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        return convertToVO(address);
    }

    @Override
    @Transactional
    public AddressVO addAddress(Long userId, AddressDTO addressDTO) {
        log.info("添加地址: userId={}", userId);

        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);

        // 如果是第一个地址或设置为默认，则设为默认地址
        Long count = count(new QueryWrapper<UserAddress>().eq("user_id", userId));
        if (count == 0 || (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1)) {
            // 先取消其他默认地址
            update(new UpdateWrapper<UserAddress>()
                    .eq("user_id", userId)
                    .set("is_default", 0));
            address.setIsDefault(1);
        } else {
            address.setIsDefault(0);
        }

        save(address);

        return convertToVO(address);
    }

    @Override
    @Transactional
    public AddressVO updateAddress(Long userId, AddressDTO addressDTO) {
        log.info("更新地址: userId={}, addressId={}", userId, addressDTO.getId());

        UserAddress address = getById(addressDTO.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        BeanUtils.copyProperties(addressDTO, address);

        // 如果设置为默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            // 先取消其他默认地址
            update(new UpdateWrapper<UserAddress>()
                    .eq("user_id", userId)
                    .set("is_default", 0));
            address.setIsDefault(1);
        }

        updateById(address);

        return convertToVO(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        log.info("删除地址: userId={}, addressId={}", userId, addressId);

        UserAddress address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        removeById(addressId);

        // 如果删除的是默认地址，将第一个地址设为默认
        if (address.getIsDefault() == 1) {
            UserAddress firstAddress = getOne(new QueryWrapper<UserAddress>()
                    .eq("user_id", userId)
                    .orderByDesc("create_time")
                    .last("LIMIT 1"));

            if (firstAddress != null) {
                firstAddress.setIsDefault(1);
                updateById(firstAddress);
            }
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("设置默认地址: userId={}, addressId={}", userId, addressId);

        UserAddress address = getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        // 取消其他默认地址
        update(new UpdateWrapper<UserAddress>()
                .eq("user_id", userId)
                .set("is_default", 0));

        // 设置当前地址为默认
        address.setIsDefault(1);
        updateById(address);
    }

    @Override
    public AddressVO getDefaultAddress(Long userId) {
        log.info("获取默认地址: userId={}", userId);

        UserAddress address = getOne(new QueryWrapper<UserAddress>()
                .eq("user_id", userId)
                .eq("is_default", 1));

        if (address == null) {
            // 如果没有默认地址，返回第一个地址
            address = getOne(new QueryWrapper<UserAddress>()
                    .eq("user_id", userId)
                    .orderByDesc("create_time")
                    .last("LIMIT 1"));
        }

        return address != null ? convertToVO(address) : null;
    }

    /**
     * 转换为VO
     */
    private AddressVO convertToVO(UserAddress address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);

        // 设置码值
        vo.setProvinceCode(address.getProvinceCode());
        vo.setCityCode(address.getCityCode());
        vo.setDistrictCode(address.getDistrictCode());

        // 查询区域名称
        List<String> codes = new ArrayList<>();
        if (address.getProvinceCode() != null) {
            codes.add(address.getProvinceCode());
        }
        if (address.getCityCode() != null) {
            codes.add(address.getCityCode());
        }
        if (address.getDistrictCode() != null) {
            codes.add(address.getDistrictCode());
        }

        if (!codes.isEmpty()) {
            Map<String, String> nameMap = regionService.getRegionNamesByCodes(codes);
            vo.setProvinceName(nameMap.get(address.getProvinceCode()));
            vo.setCityName(nameMap.get(address.getCityCode()));
            vo.setDistrictName(nameMap.get(address.getDistrictCode()));
        }

        // 拼接完整地址
        StringBuilder fullAddress = new StringBuilder();
        if (vo.getProvinceName() != null) {
            fullAddress.append(vo.getProvinceName());
        }
        if (vo.getCityName() != null) {
            fullAddress.append(vo.getCityName());
        }
        if (vo.getDistrictName() != null) {
            fullAddress.append(vo.getDistrictName());
        }
        if (address.getDetailAddress() != null) {
            fullAddress.append(address.getDetailAddress());
        }
        vo.setFullAddress(fullAddress.toString());

        return vo;
    }
}
