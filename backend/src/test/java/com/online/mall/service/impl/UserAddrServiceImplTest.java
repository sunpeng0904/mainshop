package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddr;
import com.online.mall.mapper.UserAddrMapper;
import com.online.mall.service.RegionService;
import com.online.mall.vo.AddressVO;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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
        // 注册实体表信息，使ServiceImpl.removeById等方法正常工作
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), UserAddr.class);

        //
        // 确保ServiceImpl的baseMapper正确注入
        ReflectionTestUtils.setField(addrService, "baseMapper", addrMapper);

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
        when(addrMapper.selectOne(any(), anyBoolean())).thenReturn(null);

        // When
        AddressVO result = addrService.getDftAddr("user-001");

        // Then
        assertNull(result);
    }

    // ========== 补充测试用例 ==========

    @Test
    @DisplayName("获取地址列表 - 多条地址，验证VO转换和区域名称解析")
    void testGetAddrList_MultipleAddresses() {
        // Given
        UserAddr addr2 = new UserAddr();
        addr2.setId("test-addr-002");
        addr2.setUserId("user-001");
        addr2.setRcvrName("王五");
        addr2.setRcvrTel("13700137000");
        addr2.setPrvcCde("310000");
        addr2.setCityCde("310100");
        addr2.setDstrctCde("310101");
        addr2.setDtlAddr("南京路100号");
        addr2.setDftIndc("N");
        addr2.setVldStsCde("N");

        List<UserAddr> addrList = new ArrayList<>();
        addrList.add(testAddr);
        addrList.add(addr2);
        when(addrMapper.selectList(any())).thenReturn(addrList);

        // When
        List<AddressVO> result = addrService.getAddrList("user-001");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getRcvrName());
        assertEquals("王五", result.get(1).getRcvrName());
        // 验证区域名称解析
        assertEquals("北京市", result.get(0).getPrvcName());
        assertEquals("北京市", result.get(0).getCityName());
        assertEquals("朝阳区", result.get(0).getDstrctName());
        // 验证完整地址拼接
        assertEquals("北京市北京市朝阳区望京SOHO T1 1001室", result.get(0).getFullAddr());
        assertEquals("上海市上海市黄浦区南京路100号", result.get(1).getFullAddr());
    }

    @Test
    @DisplayName("获取地址详情 - 验证VO字段完整性")
    void testGetAddrById_VerifyAllFields() {
        // Given
        testAddr.setEntrTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);

        // When
        AddressVO result = addrService.getAddrById("user-001", "test-addr-001");

        // Then
        assertNotNull(result);
        assertEquals("test-addr-001", result.getId());
        assertEquals("张三", result.getRcvrName());
        assertEquals("13800138000", result.getRcvrTel());
        assertEquals("110000", result.getPrvcCde());
        assertEquals("北京市", result.getPrvcName());
        assertEquals("110100", result.getCityCde());
        assertEquals("北京市", result.getCityName());
        assertEquals("110105", result.getDstrctCde());
        assertEquals("朝阳区", result.getDstrctName());
        assertEquals("望京SOHO T1 1001室", result.getDtlAddr());
        assertEquals("Y", result.getDftIndc());
        assertEquals("北京市北京市朝阳区望京SOHO T1 1001室", result.getFullAddr());
    }

    @Test
    @DisplayName("添加地址 - 第一个地址自动设为默认")
    void testAddAddr_FirstAddrAutoDefault() {
        // Given
        when(addrMapper.selectCount(any())).thenReturn(0L);
        when(addrMapper.update(any(), any())).thenReturn(0);
        when(addrMapper.insert(any(UserAddr.class))).thenReturn(1);

        // When
        AddressVO result = addrService.addAddr("user-001", testAddrDTO);

        // Then
        assertNotNull(result);
        verify(addrMapper).insert(argThat(addr -> "Y".equals(addr.getDftIndc())));
    }

    @Test
    @DisplayName("添加地址 - 设置默认地址时取消其他默认")
    void testAddAddr_SetDefaultClearsOthers() {
        // Given
        when(addrMapper.selectCount(any())).thenReturn(2L);
        testAddrDTO.setDftIndc("Y");
        when(addrMapper.update(any(), any())).thenReturn(1);
        when(addrMapper.insert(any(UserAddr.class))).thenReturn(1);

        // When
        AddressVO result = addrService.addAddr("user-001", testAddrDTO);

        // Then
        assertNotNull(result);
        // 验证取消了其他默认地址
        verify(addrMapper).update(any(), argThat(wrapper -> {
            String sql = wrapper.getSqlSet();
            return sql != null && sql.contains("dft_indc");
        }));
        // 验证新地址设为默认
        verify(addrMapper).insert(argThat(addr -> "Y".equals(addr.getDftIndc())));
    }

    @Test
    @DisplayName("更新地址 - 用户不匹配抛异常")
    void testUpdateAddr_UserMismatch() {
        // Given
        testAddrDTO.setId("test-addr-001");
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.updateAddr("user-002", testAddrDTO);
        });
    }

    @Test
    @DisplayName("更新地址 - 设置默认地址时取消其他默认")
    void testUpdateAddr_SetDefault() {
        // Given
        testAddrDTO.setId("test-addr-001");
        testAddrDTO.setDftIndc("Y");
        testAddr.setDftIndc("N");
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);
        when(addrMapper.update(any(), any())).thenReturn(1);
        when(addrMapper.updateById(any(UserAddr.class))).thenReturn(1);

        // When
        AddressVO result = addrService.updateAddr("user-001", testAddrDTO);

        // Then
        assertNotNull(result);
        verify(addrMapper).update(any(), argThat(wrapper -> {
            String sql = wrapper.getSqlSet();
            return sql != null && sql.contains("dft_indc");
        }));
        verify(addrMapper).updateById(argThat(addr -> "Y".equals(addr.getDftIndc())));
    }

    @Test
    @DisplayName("更新地址 - 手机号验证失败")
    void testUpdateAddr_InvalidPhone() {
        // Given
        testAddrDTO.setId("test-addr-001");
        testAddrDTO.setRcvrTel("abc");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.updateAddr("user-001", testAddrDTO);
        });
    }

    @Test
    @DisplayName("删除地址 - 成功（非默认地址）")
    void testDeleteAddr_SuccessNonDefault() {
        // Given
        testAddr.setDftIndc("N");
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);
        when(addrMapper.deleteById(any(UserAddr.class))).thenReturn(1);

        // When
        addrService.deleteAddr("user-001", "test-addr-001");

        // Then
        verify(addrMapper).deleteById(any(UserAddr.class));
        // 非默认地址删除后不应触发promote逻辑
        verify(addrMapper, never()).selectOne(any(), anyBoolean());
    }

    @Test
    @DisplayName("删除地址 - 删除默认地址后提升最近地址")
    void testDeleteAddr_DeleteDefaultPromotesNext() {
        // Given
        testAddr.setDftIndc("Y");
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);
        when(addrMapper.deleteById(any(UserAddr.class))).thenReturn(1);

        UserAddr nextAddr = new UserAddr();
        nextAddr.setId("test-addr-002");
        nextAddr.setUserId("user-001");
        nextAddr.setDftIndc("N");
        when(addrMapper.selectOne(any(), anyBoolean())).thenReturn(nextAddr);
        when(addrMapper.updateById(any(UserAddr.class))).thenReturn(1);

        // When
        addrService.deleteAddr("user-001", "test-addr-001");

        // Then
        verify(addrMapper).deleteById(any(UserAddr.class));
        verify(addrMapper).selectOne(any(), anyBoolean());
        verify(addrMapper).updateById(argThat(addr -> "Y".equals(addr.getDftIndc())));
    }

    @Test
    @DisplayName("删除地址 - 删除默认地址后无其他地址")
    void testDeleteAddr_DeleteDefaultNoRemaining() {
        // Given
        testAddr.setDftIndc("Y");
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);
        when(addrMapper.deleteById(any(UserAddr.class))).thenReturn(1);
        when(addrMapper.selectOne(any(), anyBoolean())).thenReturn(null);

        // When
        addrService.deleteAddr("user-001", "test-addr-001");

        // Then
        verify(addrMapper).deleteById(any(UserAddr.class));
        verify(addrMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("删除地址 - 用户不匹配抛异常")
    void testDeleteAddr_UserMismatch() {
        // Given
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.deleteAddr("user-002", "test-addr-001");
        });
    }

    @Test
    @DisplayName("设置默认地址 - 用户不匹配抛异常")
    void testSetDftAddr_UserMismatch() {
        // Given
        when(addrMapper.selectById("test-addr-001")).thenReturn(testAddr);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addrService.setDftAddr("user-002", "test-addr-001");
        });
    }

    @Test
    @DisplayName("获取默认地址 - 有默认地址")
    void testGetDftAddr_HasDefault() {
        // Given - getOne(wrapper) -> selectOne(wrapper, true)
        when(addrMapper.selectOne(any(), anyBoolean())).thenReturn(testAddr);

        // When
        AddressVO result = addrService.getDftAddr("user-001");

        // Then
        assertNotNull(result);
        assertEquals("张三", result.getRcvrName());
        assertEquals("Y", result.getDftIndc());
    }

    @Test
    @DisplayName("获取默认地址 - 无默认地址时返回最近地址")
    void testGetDftAddr_FallbackToLatest() {
        // Given - getOne(wrapper) -> selectOne(wrapper, true)
        when(addrMapper.selectOne(any(), anyBoolean()))
                .thenReturn(null)
                .thenReturn(testAddr);

        // When
        AddressVO result = addrService.getDftAddr("user-001");

        // Then
        assertNotNull(result);
        assertEquals("张三", result.getRcvrName());
    }

    @Test
    @DisplayName("地址VO转换 - 区域码为null时正确处理")
    void testConvertToVO_NullRegionCodes() {
        // Given
        UserAddr addrWithNullCodes = new UserAddr();
        addrWithNullCodes.setId("addr-null");
        addrWithNullCodes.setUserId("user-001");
        addrWithNullCodes.setRcvrName("测试用户");
        addrWithNullCodes.setRcvrTel("13800138000");
        addrWithNullCodes.setPrvcCde(null);
        addrWithNullCodes.setCityCde(null);
        addrWithNullCodes.setDstrctCde(null);
        addrWithNullCodes.setDtlAddr("无区域码地址");
        addrWithNullCodes.setDftIndc("N");

        when(addrMapper.selectById("addr-null")).thenReturn(addrWithNullCodes);
        when(regionService.getRegionNamesByCodes(anyList())).thenReturn(new HashMap<>());

        // When
        AddressVO result = addrService.getAddrById("user-001", "addr-null");

        // Then
        assertNotNull(result);
        assertNull(result.getPrvcName());
        assertNull(result.getCityName());
        assertNull(result.getDstrctName());
        assertEquals("无区域码地址", result.getFullAddr());
    }
}
