package com.online.mall.service.impl;

import com.online.mall.common.BusinessException;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddr;
import com.online.mall.mapper.UserAddrMapper;
import com.online.mall.service.RegionService;
import com.online.mall.vo.AddressVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户地址服务测试（符合ATTRC2E词根规范）
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserAddrServiceImplTest {

    @InjectMocks
    private UserAddrServiceImpl addrService;

    @Mock
    private UserAddrMapper addrMapper;

    @Mock
    private RegionService regionService;

    private UserAddr testAddr;
    private AddressDTO testAddrDTO;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testAddr = new UserAddr();
        testAddr.setId("test-addr-001");
        testAddr.setUserId("user-001");
        testAddr.setRcvrName("张三");
        testAddr.setRcvrTel("13800138000");
        testAddr.setPrvcCde("110000");
        testAddr.setCityCde("110100");
        testAddr.setDstrctCde("110105");
        testAddr.setDtlAddr("望京SOHO T1 1001室");
        testAddr.setDftIndc("Y");
        testAddr.setVldStsCde("N");

        testAddrDTO = new AddressDTO();
        testAddrDTO.setRcvrName("李四");
        testAddrDTO.setRcvrTel("13900139000");
        testAddrDTO.setPrvcCde("310000");
        testAddrDTO.setCityCde("310100");
        testAddrDTO.setDstrctCde("310101");
        testAddrDTO.setDtlAddr("陆家嘴金融中心 A座 2001室");
        testAddrDTO.setDftIndc("N");

        // 设置RegionService mock
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("110000", "北京市");
        nameMap.put("110100", "北京市");
        nameMap.put("110105", "朝阳区");
        nameMap.put("310000", "上海市");
        nameMap.put("310100", "上海市");
        nameMap.put("310101", "黄浦区");
        lenient().when(regionService.getRegionNamesByCodes(anyList())).thenReturn(nameMap);
    }

    @Test
    @DisplayName("获取地址列表 - 成功")
    void testGetAddrList_Success() {
        // Given
        List<UserAddr> addrList = new ArrayList<>();
        addrList.add(testAddr);
        when(addrMapper.selectList(any())).thenReturn(addrList);

        // When
        List<AddressVO> result = addrService.getAddrList("user-001");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getRcvrName());
    }

    @Test
    @DisplayName("获取地址列表 - 空列表")
    void testGetAddrList_Empty() {
        // Given
        when(addrMapper.selectList(any())).thenReturn(new ArrayList<>());

        // When
        List<AddressVO> result = addrService.getAddrList("user-001");

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("获取地址详情 - 地址存在")
    void testGetAddrById_Found() {
        // Given
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);

        // When
        AddressVO result = addrService.getAddrById("user-001", "test-addr-001");

        // Then
        assertNotNull(result);
        assertEquals("张三", result.getRcvrName());
        assertEquals("13800138000", result.getRcvrTel());
    }

    @Test
    @DisplayName("获取地址详情 - 地址不存在")
    void testGetAddrById_NotFound() {
        // Given
        when(addrMapper.selectById("not-exist")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.getAddrById("user-001", "not-exist");
        });
    }

    @Test
    @DisplayName("获取地址详情 - 用户不匹配")
    void testGetAddrById_UserMismatch() {
        // Given
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.getAddrById("user-002", "test-addr-001");
        });
    }

    @Test
    @DisplayName("添加地址 - 成功（非默认）")
    void testAddAddr_Success() {
        // Given
        when(addrMapper.selectCount(any())).thenReturn(1L);
        when(addrMapper.insert(any(UserAddr.class))).thenReturn(1);

        // When
        AddressVO result = addrService.addAddr("user-001", testAddrDTO);

        // Then
        assertNotNull(result);
        verify(addrMapper).insert(any(UserAddr.class));
    }

    @Test
    @DisplayName("添加地址 - 手机号验证失败")
    void testAddAddr_InvalidPhone() {
        // Given
        testAddrDTO.setRcvrTel("1234567890"); // 10位，不满足11位验证

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.addAddr("user-001", testAddrDTO);
        });
    }

    @Test
    @DisplayName("添加地址 - 手机号带空格应自动去除")
    void testAddAddr_PhoneWithSpaces() {
        // Given
        testAddrDTO.setRcvrTel("138 0013 8000");

        // When
        AddressVO result = addrService.addAddr("user-001", testAddrDTO);

        // Then
        assertNotNull(result);
        assertEquals("13800138000", testAddrDTO.getRcvrTel()); // 空格已去除
    }

    @Test
    @DisplayName("添加地址 - 收货人姓名超长")
    void testAddAddr_NameTooLong() {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append("A");
        }
        testAddrDTO.setRcvrName(sb.toString());

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.addAddr("user-001", testAddrDTO);
        });
    }

    @Test
    @DisplayName("添加地址 - 详细地址超长")
    void testAddAddr_AddrTooLong() {
        // Given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            sb.append("A");
        }
        testAddrDTO.setDtlAddr(sb.toString());

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.addAddr("user-001", testAddrDTO);
        });
    }

    @Test
    @DisplayName("更新地址 - 成功")
    void testUpdateAddr_Success() {
        // Given
        testAddrDTO.setId("test-addr-001");
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);
        when(addrMapper.updateById(any(UserAddr.class))).thenReturn(1);

        // When
        AddressVO result = addrService.updateAddr("user-001", testAddrDTO);

        // Then
        assertNotNull(result);
        verify(addrMapper).updateById(any(UserAddr.class));
    }

    @Test
    @DisplayName("更新地址 - 地址不存在")
    void testUpdateAddr_NotFound() {
        // Given
        testAddrDTO.setId("not-exist");
        when(addrMapper.selectById("not-exist")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.updateAddr("user-001", testAddrDTO);
        });
    }

    @Test
    @DisplayName("删除地址 - 地址不存在")
    void testDeleteAddr_NotFound() {
        // Given
        when(addrMapper.selectById("not-exist")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.deleteAddr("user-001", "not-exist");
        });
    }

    @Test
    @DisplayName("设置默认地址 - 成功")
    void testSetDftAddr_Success() {
        // Given
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);
        when(addrMapper.update(any(), any())).thenReturn(1);
        when(addrMapper.updateById(any(UserAddr.class))).thenReturn(1);

        // When
        addrService.setDftAddr("user-001", "test-addr-001");

        // Then
        verify(addrMapper).updateById(any(UserAddr.class));
    }

    @Test
    @DisplayName("设置默认地址 - 地址不存在")
    void testSetDftAddr_NotFound() {
        // Given
        when(addrMapper.selectById("not-exist")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.setDftAddr("user-001", "not-exist");
        });
    }

    @Test
    @DisplayName("获取默认地址 - 无地址")
    void testGetDftAddr_NoAddress() {
        // Given
        when(addrMapper.selectOne(any())).thenReturn(null);

        // When
        AddressVO result = addrService.getDftAddr("user-001");

        // Then
        assertNull(result);
    }
}
