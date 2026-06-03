package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddr;
import com.online.mall.mapper.UserAddrMapper;
import com.online.mall.service.RegionService;
import com.online.mall.service.UserAddrService;
import com.online.mall.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户地址服务实现（符合ATTRC2E词根规范）
 *
 * 优化点：
 * 1. 优化数据库查询，添加索引
 * 2. 增强输入验证
 * 3. 优化默认地址查询逻辑
 */
@Slf4j
@Service
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService {

    @Autowired
    private RegionService regionService;

    @Override
    public List<AddressVO> getAddrList(String userId) {
        log.info("获取用户地址列表: userId={}", userId);

        List<UserAddr> addresses = list(new QueryWrapper<UserAddr>()
                .eq("user_id", userId)
                .orderByDesc("dft_indc")
                .orderByDesc("entr_time"));

        List<AddressVO> voList = new ArrayList<>();
        for (UserAddr address : addresses) {
            voList.add(convertToVO(address));
        }

        return voList;
    }

    @Override
    public AddressVO getAddrById(String userId, String addrId) {
        log.info("获取地址详情: userId={}, addrId={}", userId, addrId);

        UserAddr address = getById(addrId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        return convertToVO(address);
    }

    @Override
    @Transactional
    public AddressVO addAddr(String userId, AddressDTO addressDTO) {
        log.info("添加地址: userId={}", userId);

        // 验证输入数据
        validateAddressDTO(addressDTO);

        UserAddr address = new UserAddr();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);

        // 如果是第一个地址或设置为默认，则设为默认地址
        Long count = count(new QueryWrapper<UserAddr>().eq("user_id", userId));
        if (count == 0 || "Y".equals(addressDTO.getDftIndc())) {
            // 先取消其他默认地址
            update(new UpdateWrapper<UserAddr>()
                    .eq("user_id", userId)
                    .set("dft_indc", "N"));
            address.setDftIndc("Y");
        } else {
            address.setDftIndc("N");
        }

        save(address);

        return convertToVO(address);
    }

    @Override
    @Transactional
    public AddressVO updateAddr(String userId, AddressDTO addressDTO) {
        log.info("更新地址: userId={}, addrId={}", userId, addressDTO.getId());

        // 验证输入数据
        validateAddressDTO(addressDTO);

        UserAddr address = getById(addressDTO.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        BeanUtils.copyProperties(addressDTO, address);

        // 如果设置为默认地址
        if ("Y".equals(addressDTO.getDftIndc())) {
            // 先取消其他默认地址
            update(new UpdateWrapper<UserAddr>()
                    .eq("user_id", userId)
                    .set("dft_indc", "N"));
            address.setDftIndc("Y");
        }

        updateById(address);

        return convertToVO(address);
    }

    @Override
    @Transactional
    public void deleteAddr(String userId, String addrId) {
        log.info("删除地址: userId={}, addrId={}", userId, addrId);

        UserAddr address = getById(addrId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        removeById(addrId);

        // 如果删除的是默认地址，将第一个地址设为默认
        if ("Y".equals(address.getDftIndc())) {
            UserAddr firstAddress = getOne(new QueryWrapper<UserAddr>()
                    .eq("user_id", userId)
                    .orderByDesc("entr_time")
                    .last("LIMIT 1"));

            if (firstAddress != null) {
                firstAddress.setDftIndc("Y");
                updateById(firstAddress);
            }
        }
    }

    @Override
    @Transactional
    public void setDftAddr(String userId, String addrId) {
        log.info("设置默认地址: userId={}, addrId={}", userId, addrId);

        UserAddr address = getById(addrId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("address.not.found");
        }

        // 取消其他默认地址
        update(new UpdateWrapper<UserAddr>()
                .eq("user_id", userId)
                .set("dft_indc", "N"));

        // 设置当前地址为默认
        address.setDftIndc("Y");
        updateById(address);
    }

    @Override
    public AddressVO getDftAddr(String userId) {
        log.info("获取默认地址: userId={}", userId);

        UserAddr address = getOne(new QueryWrapper<UserAddr>()
                .eq("user_id", userId)
                .eq("dft_indc", "Y"));

        if (address == null) {
            // 如果没有默认地址，返回第一个地址
            address = getOne(new QueryWrapper<UserAddr>()
                    .eq("user_id", userId)
                    .orderByDesc("entr_time")
                    .last("LIMIT 1"));
        }

        return address != null ? convertToVO(address) : null;
    }

    /**
     * 验证地址DTO
     */
    private void validateAddressDTO(AddressDTO addressDTO) {
        // 去除手机号中的空格后再验证
        if (addressDTO.getRcvrTel() != null) {
            String tel = addressDTO.getRcvrTel().replaceAll("\\s", "");
            if (!tel.matches("^\\d{11}$")) {
                throw new BusinessException("address.invalid.phone");
            }
            addressDTO.setRcvrTel(tel);
        }

        // 验证收货人姓名长度
        if (addressDTO.getRcvrName() != null && addressDTO.getRcvrName().length() > 100) {
            throw new BusinessException("address.receiver.name.too.long");
        }

        // 验证详细地址长度
        if (addressDTO.getDtlAddr() != null && addressDTO.getDtlAddr().length() > 500) {
            throw new BusinessException("address.detail.address.too.long");
        }
    }

    /**
     * 转换为VO
     */
    private AddressVO convertToVO(UserAddr address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);

        // 设置码值
        vo.setPrvcCde(address.getPrvcCde());
        vo.setCityCde(address.getCityCde());
        vo.setDstrctCde(address.getDstrctCde());

        // 查询区域名称
        List<String> codes = new ArrayList<>();
        if (address.getPrvcCde() != null) {
            codes.add(address.getPrvcCde());
        }
        if (address.getCityCde() != null) {
            codes.add(address.getCityCde());
        }
        if (address.getDstrctCde() != null) {
            codes.add(address.getDstrctCde());
        }

        if (!codes.isEmpty()) {
            Map<String, String> nameMap = regionService.getRegionNamesByCodes(codes);
            vo.setPrvcName(nameMap.get(address.getPrvcCde()));
            vo.setCityName(nameMap.get(address.getCityCde()));
            vo.setDstrctName(nameMap.get(address.getDstrctCde()));
        }

        // 拼接完整地址
        StringBuilder fullAddress = new StringBuilder();
        if (vo.getPrvcName() != null) {
            fullAddress.append(vo.getPrvcName());
        }
        if (vo.getCityName() != null) {
            fullAddress.append(vo.getCityName());
        }
        if (vo.getDstrctName() != null) {
            fullAddress.append(vo.getDstrctName());
        }
        if (address.getDtlAddr() != null) {
            fullAddress.append(address.getDtlAddr());
        }
        vo.setFullAddr(fullAddress.toString());

        return vo;
    }
}
