package com.online.mall.service.impl;

import com.online.mall.common.BusinessException;
import com.online.mall.dto.AddressDTO;
import com.online.mall.vo.AddressVO;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fake 测试 — 用内存实现替代数据库，验证业务逻辑
 *
 * Fake vs Mock：
 * - Mock：假数据，只验证"方法有没有被调用"
 * - Fake：真实逻辑的简化实现，真正存数据、真正查询
 */
class UserAddrServiceFakeTest {

    private FakeUserAddrService addrService;

    @BeforeEach
    void setUp() {
        addrService = new FakeUserAddrService();
    }

    // ========== 测试用例 ==========

    @Test
    @DisplayName("第一个地址自动成为默认")
    void firstAddress_autoDefault() {
        addrService.addAddr("user-001", buildDTO("张三", "13800000001", "N"));

        List<AddressVO> list = addrService.getAddrList("user-001");
        assertEquals(1, list.size());
        assertEquals("Y", list.get(0).getDftIndc());
    }

    @Test
    @DisplayName("添加非默认地址不影响已有默认")
    void addNonDefault_preservesExistingDefault() {
        addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        addrService.addAddr("user-001", buildDTO("李四", "13800000002", "N"));

        List<AddressVO> list = addrService.getAddrList("user-001");
        assertEquals(2, list.size());

        long defaultCount = list.stream().filter(a -> "Y".equals(a.getDftIndc())).count();
        assertEquals(1, defaultCount);

        // 默认地址是张三
        assertEquals("张三", list.get(0).getRcvrName());
        assertEquals("Y", list.get(0).getDftIndc());
    }

    @Test
    @DisplayName("设置默认时清除其他默认")
    void setDftAddr_clearsOtherDefaults() {
        AddressVO addr1 = addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        AddressVO addr2 = addrService.addAddr("user-001", buildDTO("李四", "13800000002", "N"));

        addrService.setDftAddr("user-001", addr2.getId());

        List<AddressVO> list = addrService.getAddrList("user-001");
        long defaultCount = list.stream().filter(a -> "Y".equals(a.getDftIndc())).count();
        assertEquals(1, defaultCount);

        // 李四变成默认
        AddressVO updated = addrService.getAddrById("user-001", addr2.getId());
        assertEquals("Y", updated.getDftIndc());

        // 张三不再是默认
        AddressVO old = addrService.getAddrById("user-001", addr1.getId());
        assertEquals("N", old.getDftIndc());
    }

    @Test
    @DisplayName("删除默认地址后提升最近的")
    void deleteDefault_promotesLatest() {
        AddressVO addr1 = addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        AddressVO addr2 = addrService.addAddr("user-001", buildDTO("李四", "13800000002", "N"));
        AddressVO addr3 = addrService.addAddr("user-001", buildDTO("王五", "13800000003", "N"));

        addrService.deleteAddr("user-001", addr1.getId());

        // 王五（最新）成为默认
        AddressVO promoted = addrService.getAddrById("user-001", addr3.getId());
        assertEquals("Y", promoted.getDftIndc());

        // 李四不是默认
        AddressVO notPromoted = addrService.getAddrById("user-001", addr2.getId());
        assertEquals("N", notPromoted.getDftIndc());
    }

    @Test
    @DisplayName("删除非默认不影响默认")
    void deleteNonDefault_preservesDefault() {
        AddressVO addr1 = addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        AddressVO addr2 = addrService.addAddr("user-001", buildDTO("李四", "13800000002", "N"));

        addrService.deleteAddr("user-001", addr2.getId());

        AddressVO defaultAddr = addrService.getAddrById("user-001", addr1.getId());
        assertEquals("Y", defaultAddr.getDftIndc());
    }

    @Test
    @DisplayName("删除唯一地址后列表为空")
    void deleteOnlyAddress_listEmpty() {
        AddressVO addr = addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        addrService.deleteAddr("user-001", addr.getId());

        assertTrue(addrService.getAddrList("user-001").isEmpty());
    }

    @Test
    @DisplayName("用户隔离")
    void userIsolation() {
        addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        addrService.addAddr("user-002", buildDTO("李四", "13800000002", "Y"));

        assertEquals(1, addrService.getAddrList("user-001").size());
        assertEquals(1, addrService.getAddrList("user-002").size());
        assertEquals("张三", addrService.getAddrList("user-001").get(0).getRcvrName());
        assertEquals("李四", addrService.getAddrList("user-002").get(0).getRcvrName());
    }

    @Test
    @DisplayName("不能访问其他用户的地址")
    void cannotAccessOtherUsersAddr() {
        AddressVO addr = addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));

        assertThrows(BusinessException.class, () ->
                addrService.getAddrById("user-002", addr.getId()));
    }

    @Test
    @DisplayName("手机号非法抛异常")
    void invalidPhone_throwsException() {
        assertThrows(BusinessException.class, () ->
                addrService.addAddr("user-001", buildDTO("张三", "12345", "N")));
    }

    @Test
    @DisplayName("更新后数据正确")
    void updateAddr_persistsChanges() {
        AddressVO addr = addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));

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

        AddressVO updated = addrService.getAddrById("user-001", addr.getId());
        assertEquals("张三改名", updated.getRcvrName());
        assertEquals("13900000001", updated.getRcvrTel());
        assertEquals("陆家嘴1号", updated.getDtlAddr());
    }

    @Test
    @DisplayName("获取默认地址 - 有默认返回默认")
    void getDftAddr_returnsDefault() {
        addrService.addAddr("user-001", buildDTO("张三", "13800000001", "Y"));
        addrService.addAddr("user-001", buildDTO("李四", "13800000002", "N"));

        AddressVO dft = addrService.getDftAddr("user-001");
        assertNotNull(dft);
        assertEquals("张三", dft.getRcvrName());
        assertEquals("Y", dft.getDftIndc());
    }

    @Test
    @DisplayName("获取默认地址 - 无地址返回null")
    void getDftAddr_noAddr_returnsNull() {
        assertNull(addrService.getDftAddr("user-empty"));
    }

    // ========== 辅助方法 ==========

    private AddressDTO buildDTO(String name, String tel, String dft) {
        AddressDTO dto = new AddressDTO();
        dto.setRcvrName(name);
        dto.setRcvrTel(tel);
        dto.setPrvcCde("110000");
        dto.setCityCde("110100");
        dto.setDstrctCde("110105");
        dto.setDtlAddr("测试地址");
        dto.setDftIndc(dft);
        return dto;
    }

    // ========== Fake 实现 ==========

    /**
     * Fake 版 UserAddrService — 用内存 Map 存数据，实现真实业务逻辑
     * 不依赖数据库，不依赖 MyBatis-Plus
     */
    /**
     * Fake 版地址服务 — 不依赖任何框架，用内存 Map 存数据
     * 复刻 UserAddrServiceImpl 的核心业务逻辑
     */
    static class FakeUserAddrService {

        // 内存数据库：userId -> List<AddressVO>
        private final Map<String, List<AddressVO>> store = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);

        public List<AddressVO> getAddrList(String userId) {
            List<AddressVO> list = store.getOrDefault(userId, new ArrayList<>());
            // 模拟 ORDER BY dft_indc DESC, entr_time DESC
            list.sort((a, b) -> {
                int cmp = b.getDftIndc().compareTo(a.getDftIndc());
                return cmp; // Y > N，所以 Y 排前面
            });
            return new ArrayList<>(list);
        }

        public AddressVO getAddrById(String userId, String addrId) {
            List<AddressVO> list = store.getOrDefault(userId, new ArrayList<>());
            return list.stream()
                    .filter(a -> a.getId().equals(addrId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("address.not.found"));
        }

        public AddressVO addAddr(String userId, AddressDTO dto) {
            // 验证逻辑（和真实 Service 一致）
            validateDTO(dto);

            List<AddressVO> list = store.computeIfAbsent(userId, k -> new ArrayList<>());

            AddressVO vo = new AddressVO();
            vo.setId("fake-" + idGenerator.getAndIncrement());
            vo.setRcvrName(dto.getRcvrName());
            vo.setRcvrTel(dto.getRcvrTel().replaceAll("\\s", ""));
            vo.setPrvcCde(dto.getPrvcCde());
            vo.setCityCde(dto.getCityCde());
            vo.setDstrctCde(dto.getDstrctCde());
            vo.setDtlAddr(dto.getDtlAddr());

            // 第一个地址或设为默认 → 自动成为默认
            if (list.isEmpty() || "Y".equals(dto.getDftIndc())) {
                // 清除其他默认
                list.forEach(a -> a.setDftIndc("N"));
                vo.setDftIndc("Y");
            } else {
                vo.setDftIndc("N");
            }

            list.add(vo);
            return vo;
        }

        public AddressVO updateAddr(String userId, AddressDTO dto) {
            validateDTO(dto);

            AddressVO existing = getAddrById(userId, dto.getId());
            existing.setRcvrName(dto.getRcvrName());
            existing.setRcvrTel(dto.getRcvrTel().replaceAll("\\s", ""));
            existing.setPrvcCde(dto.getPrvcCde());
            existing.setCityCde(dto.getCityCde());
            existing.setDstrctCde(dto.getDstrctCde());
            existing.setDtlAddr(dto.getDtlAddr());

            if ("Y".equals(dto.getDftIndc())) {
                List<AddressVO> list = store.getOrDefault(userId, new ArrayList<>());
                list.forEach(a -> a.setDftIndc("N"));
                existing.setDftIndc("Y");
            }

            return existing;
        }

        public void deleteAddr(String userId, String addrId) {
            List<AddressVO> list = store.getOrDefault(userId, new ArrayList<>());
            AddressVO toDelete = list.stream()
                    .filter(a -> a.getId().equals(addrId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("address.not.found"));

            boolean wasDefault = "Y".equals(toDelete.getDftIndc());
            list.remove(toDelete);

            // 删除默认地址后，提升最近的
            if (wasDefault && !list.isEmpty()) {
                list.get(list.size() - 1).setDftIndc("Y");
            }
        }

        public void setDftAddr(String userId, String addrId) {
            List<AddressVO> list = store.getOrDefault(userId, new ArrayList<>());
            AddressVO target = list.stream()
                    .filter(a -> a.getId().equals(addrId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("address.not.found"));

            list.forEach(a -> a.setDftIndc("N"));
            target.setDftIndc("Y");
        }

        public AddressVO getDftAddr(String userId) {
            List<AddressVO> list = store.getOrDefault(userId, new ArrayList<>());

            // 先找默认
            AddressVO dft = list.stream()
                    .filter(a -> "Y".equals(a.getDftIndc()))
                    .findFirst()
                    .orElse(null);

            // 没有默认则返回最近的
            if (dft == null && !list.isEmpty()) {
                dft = list.get(list.size() - 1);
            }

            return dft;
        }

        private void validateDTO(AddressDTO dto) {
            if (dto.getRcvrTel() != null) {
                String tel = dto.getRcvrTel().replaceAll("\\s", "");
                if (!tel.matches("^\\d{11}$")) {
                    throw new BusinessException("address.invalid.phone");
                }
            }
            if (dto.getRcvrName() != null && dto.getRcvrName().length() > 100) {
                throw new BusinessException("address.receiver.name.too.long");
            }
            if (dto.getDtlAddr() != null && dto.getDtlAddr().length() > 500) {
                throw new BusinessException("address.detail.address.too.long");
            }
        }
    }
}
