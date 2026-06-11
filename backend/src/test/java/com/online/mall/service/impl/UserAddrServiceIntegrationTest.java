package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddr;
import com.online.mall.mapper.UserAddrMapper;
import com.online.mall.service.UserAddrService;
import com.online.mall.vo.AddressVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 地址服务集成测试 — 使用 H2 内存数据库验证真实业务逻辑
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserAddrServiceIntegrationTest {

    @Autowired
    private UserAddrService addrService;

    @Autowired
    private UserAddrMapper addrMapper;

    private AddressDTO buildDTO(String name, String tel, String prvc, String city, String dstrct, String dtl, String dft) {
        AddressDTO dto = new AddressDTO();
        dto.setRcvrName(name);
        dto.setRcvrTel(tel);
        dto.setPrvcCde(prvc);
        dto.setCityCde(city);
        dto.setDstrctCde(dstrct);
        dto.setDtlAddr(dtl);
        dto.setDftIndc(dft);
        return dto;
    }

    // ========== 默认地址管理 ==========

    @Test
    @DisplayName("第一个地址自动成为默认地址")
    void firstAddress_autoDefault() {
        AddressDTO dto = buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "N");

        addrService.addAddr("user-001", dto);

        // 从数据库直接验证
        List<UserAddr> all = addrMapper.selectList(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));
        assertEquals(1, all.size());
        assertEquals("Y", all.get(0).getDftIndc());
    }

    @Test
    @DisplayName("添加非默认地址不影响已有默认地址")
    void addNonDefault_doesNotAffectExistingDefault() {
        // 先添加默认地址
        addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));

        // 再添加非默认地址
        addrService.addAddr("user-001",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "N"));

        List<UserAddr> all = addrMapper.selectList(
                new QueryWrapper<UserAddr>().eq("user_id", "user-001").orderByDesc("entr_time"));
        assertEquals(2, all.size());

        // 第一条添加的应该是默认
        UserAddr first = all.stream().filter(a -> "张三".equals(a.getRcvrName())).findFirst().orElseThrow(() -> new AssertionError("not found"));
        assertEquals("Y", first.getDftIndc());

        // 第二条是非默认
        UserAddr second = all.stream().filter(a -> "李四".equals(a.getRcvrName())).findFirst().orElseThrow(() -> new AssertionError("not found"));
        assertEquals("N", second.getDftIndc());
    }

    @Test
    @DisplayName("设置默认地址时，其他默认地址被清除")
    void setDftAddr_clearsOtherDefaults() {
        // 添加两个地址，第一个为默认
        AddressVO addr1 = addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));
        AddressVO addr2 = addrService.addAddr("user-001",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "N"));

        // 将第二个设为默认
        addrService.setDftAddr("user-001", addr2.getId());

        // 从数据库验证：只有 addr2 是默认
        List<UserAddr> all = addrMapper.selectList(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));
        long defaultCount = all.stream().filter(a -> "Y".equals(a.getDftIndc())).count();
        assertEquals(1, defaultCount, "只能有一个默认地址");

        UserAddr updatedAddr2 = addrMapper.selectById(addr2.getId());
        assertEquals("Y", updatedAddr2.getDftIndc());

        UserAddr updatedAddr1 = addrMapper.selectById(addr1.getId());
        assertEquals("N", updatedAddr1.getDftIndc());
    }

    @Test
    @DisplayName("添加地址设为默认时，清除其他默认")
    void addAddr_setDefaultClearsOthers() {
        addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));

        // 添加第二个并设为默认
        addrService.addAddr("user-001",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "Y"));

        List<UserAddr> all = addrMapper.selectList(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));
        long defaultCount = all.stream().filter(a -> "Y".equals(a.getDftIndc())).count();
        assertEquals(1, defaultCount, "只能有一个默认地址");

        UserAddr newDefault = all.stream().filter(a -> "李四".equals(a.getRcvrName())).findFirst().orElseThrow(() -> new AssertionError("not found"));
        assertEquals("Y", newDefault.getDftIndc());
    }

    // ========== 删除地址与默认提升 ==========

    @Test
    @DisplayName("删除默认地址后，最近添加的地址成为默认")
    void deleteDefaultAddr_promotesLatest() {
        // 手动插入并设置 entrTime，确保排序确定
        UserAddr a1 = new UserAddr();
        a1.setId("del-001"); a1.setUserId("user-001"); a1.setRcvrName("张三");
        a1.setRcvrTel("13800000001"); a1.setPrvcCde("110000"); a1.setCityCde("110100");
        a1.setDstrctCde("110105"); a1.setDtlAddr("朝阳路1号"); a1.setDftIndc("Y");
        a1.setEntrTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        addrMapper.insert(a1);

        UserAddr a2 = new UserAddr();
        a2.setId("del-002"); a2.setUserId("user-001"); a2.setRcvrName("李四");
        a2.setRcvrTel("13800000002"); a2.setPrvcCde("310000"); a2.setCityCde("310100");
        a2.setDstrctCde("310101"); a2.setDtlAddr("南京路1号"); a2.setDftIndc("N");
        a2.setEntrTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        addrMapper.insert(a2);

        UserAddr a3 = new UserAddr();
        a3.setId("del-003"); a3.setUserId("user-001"); a3.setRcvrName("王五");
        a3.setRcvrTel("13800000003"); a3.setPrvcCde("440000"); a3.setCityCde("440100");
        a3.setDstrctCde("440106"); a3.setDtlAddr("天河路1号"); a3.setDftIndc("N");
        a3.setEntrTime(LocalDateTime.of(2024, 1, 3, 10, 0));
        addrMapper.insert(a3);

        // 删除默认地址
        addrService.deleteAddr("user-001", "del-001");

        // 验证：最近添加的地址（王五，entrTime最晚）成为默认
        UserAddr promoted = addrMapper.selectById("del-003");
        assertNotNull(promoted);
        assertEquals("Y", promoted.getDftIndc());

        // 李四不是默认
        UserAddr notPromoted = addrMapper.selectById("del-002");
        assertNotNull(notPromoted);
        assertEquals("N", notPromoted.getDftIndc());
    }

    @Test
    @DisplayName("删除非默认地址不影响默认地址")
    void deleteNonDefault_doesNotAffectDefault() {
        AddressVO addr1 = addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));
        AddressVO addr2 = addrService.addAddr("user-001",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "N"));

        addrService.deleteAddr("user-001", addr2.getId());

        // 默认地址不变
        UserAddr defaultAddr = addrMapper.selectById(addr1.getId());
        assertNotNull(defaultAddr);
        assertEquals("Y", defaultAddr.getDftIndc());
    }

    @Test
    @DisplayName("删除唯一地址后列表为空，无异常")
    void deleteOnlyAddress_listEmpty() {
        AddressVO addr = addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));

        addrService.deleteAddr("user-001", addr.getId());

        List<UserAddr> remaining = addrMapper.selectList(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));
        assertTrue(remaining.isEmpty());
    }

    // ========== 更新地址 ==========

    @Test
    @DisplayName("更新地址后字段正确持久化到数据库")
    void updateAddr_persistsChanges() {
        AddressVO addr = addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));

        AddressDTO updateDTO = new AddressDTO();
        updateDTO.setId(addr.getId());
        updateDTO.setRcvrName("张三改名");
        updateDTO.setRcvrTel("13900000001");
        updateDTO.setPrvcCde("310000");
        updateDTO.setCityCde("310100");
        updateDTO.setDstrctCde("310101");
        updateDTO.setDtlAddr("陆家嘴1号");
        updateDTO.setDftIndc("Y");

        addrService.updateAddr("user-001", updateDTO);

        // 从数据库重新查询验证
        UserAddr updated = addrMapper.selectById(addr.getId());
        assertNotNull(updated);
        assertEquals("张三改名", updated.getRcvrName());
        assertEquals("13900000001", updated.getRcvrTel());
        assertEquals("310000", updated.getPrvcCde());
        assertEquals("310100", updated.getCityCde());
        assertEquals("310101", updated.getDstrctCde());
        assertEquals("陆家嘴1号", updated.getDtlAddr());
    }

    // ========== 用户隔离 ==========

    @Test
    @DisplayName("不同用户的地址互相隔离")
    void addrList_userIsolation() {
        addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));
        addrService.addAddr("user-002",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "Y"));

        List<AddressVO> list1 = addrService.getAddrList("user-001");
        List<AddressVO> list2 = addrService.getAddrList("user-002");

        assertEquals(1, list1.size());
        assertEquals(1, list2.size());
        assertEquals("张三", list1.get(0).getRcvrName());
        assertEquals("李四", list2.get(0).getRcvrName());
    }

    @Test
    @DisplayName("不能访问其他用户的地址详情")
    void getAddrById_cannotAccessOtherUser() {
        AddressVO addr = addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));

        assertThrows(Exception.class, () -> addrService.getAddrById("user-002", addr.getId()));
    }

    // ========== 输入验证 ==========

    @Test
    @DisplayName("手机号非法时地址不会写入数据库")
    void addAddr_invalidPhone_noRecordSaved() {
        long countBefore = addrMapper.selectCount(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));

        assertThrows(Exception.class, () ->
                addrService.addAddr("user-001",
                        buildDTO("张三", "12345", "110000", "110100", "110105", "朝阳路1号", "N")));

        long countAfter = addrMapper.selectCount(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));
        assertEquals(countBefore, countAfter, "验证失败不应写入数据");
    }

    @Test
    @DisplayName("收货人姓名超长时地址不会写入数据库")
    void addAddr_nameTooLong_noRecordSaved() {
        long countBefore = addrMapper.selectCount(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append("A");
        String longName = sb.toString();
        assertThrows(Exception.class, () ->
                addrService.addAddr("user-001",
                        buildDTO(longName, "13800000001", "110000", "110100", "110105", "朝阳路1号", "N")));

        long countAfter = addrMapper.selectCount(new QueryWrapper<UserAddr>().eq("user_id", "user-001"));
        assertEquals(countBefore, countAfter, "验证失败不应写入数据");
    }

    // ========== 查询排序与VO转换 ==========

    @Test
    @DisplayName("地址列表默认地址排最前")
    void addrList_defaultFirst() {
        addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));
        addrService.addAddr("user-001",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "N"));

        List<AddressVO> list = addrService.getAddrList("user-001");

        assertEquals(2, list.size());
        assertEquals("Y", list.get(0).getDftIndc(), "默认地址应排在第一位");
        assertEquals("张三", list.get(0).getRcvrName());
    }

    @Test
    @DisplayName("VO区域名称解析和完整地址拼接正确")
    void addrVO_regionNameAndFullAddr() {
        addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "望京SOHO", "Y"));

        List<AddressVO> list = addrService.getAddrList("user-001");
        AddressVO vo = list.get(0);

        assertEquals("北京市", vo.getPrvcName());
        assertEquals("北京市", vo.getCityName());
        assertEquals("朝阳区", vo.getDstrctName());
        assertEquals("北京市北京市朝阳区望京SOHO", vo.getFullAddr());
    }

    // ========== 默认地址查询 ==========

    @Test
    @DisplayName("获取默认地址 - 有默认时返回默认")
    void getDftAddr_returnsDefault() {
        addrService.addAddr("user-001",
                buildDTO("张三", "13800000001", "110000", "110100", "110105", "朝阳路1号", "Y"));
        addrService.addAddr("user-001",
                buildDTO("李四", "13800000002", "310000", "310100", "310101", "南京路1号", "N"));

        AddressVO dft = addrService.getDftAddr("user-001");

        assertNotNull(dft);
        assertEquals("张三", dft.getRcvrName());
        assertEquals("Y", dft.getDftIndc());
    }

    @Test
    @DisplayName("获取默认地址 - 无默认时返回最近地址")
    void getDftAddr_fallbackToLatest() {
        // 手动插入，设置 entrTime 确保排序确定
        UserAddr a1 = new UserAddr();
        a1.setId("addr-001"); a1.setUserId("user-001"); a1.setRcvrName("张三");
        a1.setRcvrTel("13800000001"); a1.setPrvcCde("110000"); a1.setCityCde("110100");
        a1.setDstrctCde("110105"); a1.setDtlAddr("朝阳路1号"); a1.setDftIndc("N");
        a1.setEntrTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        addrMapper.insert(a1);

        UserAddr a2 = new UserAddr();
        a2.setId("addr-002"); a2.setUserId("user-001"); a2.setRcvrName("李四");
        a2.setRcvrTel("13800000002"); a2.setPrvcCde("310000"); a2.setCityCde("310100");
        a2.setDstrctCde("310101"); a2.setDtlAddr("南京路1号"); a2.setDftIndc("N");
        a2.setEntrTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        addrMapper.insert(a2);

        AddressVO dft = addrService.getDftAddr("user-001");

        assertNotNull(dft);
        assertEquals("李四", dft.getRcvrName(), "无默认时应返回最近添加的地址");
    }

    @Test
    @DisplayName("获取默认地址 - 无地址时返回null")
    void getDftAddr_noAddr_returnsNull() {
        AddressVO dft = addrService.getDftAddr("user-empty");
        assertNull(dft);
    }
}
