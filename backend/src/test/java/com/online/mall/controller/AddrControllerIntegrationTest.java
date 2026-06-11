package com.online.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.mall.dto.AddressDTO;
import com.online.mall.entity.UserAddr;
import com.online.mall.mapper.UserAddrMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AddrController MockMvc 集成测试
 *
 * 使用 H2 内存数据库（test profile）验证完整请求链路：
 * HTTP 请求 → MockMvc → Security Filter → Controller → Service → Mapper → H2
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@DisplayName("地址控制器集成测试")
class AddrControllerIntegrationTest {

    // ==================== 常量 ====================

    private static final String TEST_USER  = "test-user-001";
    private static final String OTHER_USER = "other-user-002";
    private static final String BASE_URL   = "/address";

    // ==================== 自动注入 ====================

    @Autowired private WebApplicationContext wac;
    @Autowired private MockMvc mockMvc;
    @Autowired private UserAddrMapper addrMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 设置 MockMvc with auth filter ====================

    /**
     * 每个测试前重新构建 MockMvc，注入认证过滤器
     */
    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    FilterChain chain) throws ServletException, IOException {
                        request.setAttribute("userId", TEST_USER);
                        request.setAttribute("username", "testuser");
                        List<SimpleGrantedAuthority> authorities =
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken("testuser", null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        chain.doFilter(request, response);
                    }
                }, "/*")
                .build();
    }

    // ==================== Helper 方法 ====================

    /**
     * 构建合法的 AddressDTO
     */
    private AddressDTO buildValidDTO() {
        AddressDTO dto = new AddressDTO();
        dto.setRcvrName("张三");
        dto.setRcvrTel("13800000001");
        dto.setPrvcCde("110000");
        dto.setCityCde("110100");
        dto.setDstrctCde("110105");
        dto.setDtlAddr("望京SOHO T1 1001室");
        dto.setDftIndc("N");
        return dto;
    }

    /**
     * 直接插入 UserAddr 实体到数据库（用于精确控制测试数据）
     */
    private UserAddr insertAddr(String userId, String name, String tel,
                                String prvcCde, String cityCde, String dstrctCde,
                                String dtlAddr, String dftIndc, LocalDateTime entrTime) {
        UserAddr addr = new UserAddr();
        addr.setUserId(userId);
        addr.setRcvrName(name);
        addr.setRcvrTel(tel);
        addr.setPrvcCde(prvcCde);
        addr.setCityCde(cityCde);
        addr.setDstrctCde(dstrctCde);
        addr.setDtlAddr(dtlAddr);
        addr.setDftIndc(dftIndc);
        addr.setEntrTime(entrTime);
        addrMapper.insert(addr);
        return addr;
    }

    // ================================================================
    //  地址列表测试
    // ================================================================

    @Test
    @DisplayName("获取地址列表 - 空用户返回空列表")
    void getAddrList_empty_returnsEmptyList() throws Exception {
        mockMvc.perform(get(BASE_URL + "/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("获取地址列表 - 有地址时返回默认地址在前、按时间倒序")
    void getAddrList_withAddresses_returnsSortedList() throws Exception {
        // 手动插入两个地址，确保排序确定
        insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));
        insertAddr(TEST_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "N",
                LocalDateTime.of(2024, 1, 2, 10, 0));

        mockMvc.perform(get(BASE_URL + "/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].rcvrName").value("张三"))
                .andExpect(jsonPath("$.data[0].dftIndc").value("Y"))
                .andExpect(jsonPath("$.data[1].rcvrName").value("李四"))
                .andExpect(jsonPath("$.data[1].dftIndc").value("N"));
    }

    @Test
    @DisplayName("获取地址列表 - 用户隔离：只返回自己的地址")
    void getAddrList_userIsolation_onlyOwnAddresses() throws Exception {
        insertAddr(TEST_USER,  "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));
        insertAddr(OTHER_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        mockMvc.perform(get(BASE_URL + "/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].rcvrName").value("张三"));
    }

    // ================================================================
    //  地址 CRUD HTTP 测试
    // ================================================================

    @Test
    @DisplayName("添加地址 - 合法输入返回 200 并包含地址数据")
    void addAddr_validInput_returns200WithAddress() throws Exception {
        AddressDTO dto = buildValidDTO();

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rcvrName").value("张三"))
                .andExpect(jsonPath("$.data.rcvrTel").value("13800000001"));
    }

    @Test
    @DisplayName("添加地址 - 无效手机号返回 400")
    void addAddr_invalidPhone_returns400() throws Exception {
        AddressDTO dto = buildValidDTO();
        dto.setRcvrTel("12345");  // 不是 11 位

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("添加地址 - 收货人姓名为空返回 400")
    void addAddr_blankName_returns400() throws Exception {
        AddressDTO dto = buildValidDTO();
        dto.setRcvrName("");  // 空字符串

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("获取地址详情 - 合法请求返回 200 并包含地址数据")
    void getAddrById_valid_returnsAddress() throws Exception {
        UserAddr addr = insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        mockMvc.perform(get(BASE_URL + "/" + addr.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rcvrName").value("张三"));
    }

    @Test
    @DisplayName("获取地址详情 - 访问其他用户地址返回 400（BusinessException）")
    void getAddrById_otherUserAddr_returns404() throws Exception {
        UserAddr addr = insertAddr(OTHER_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        // 当前用户 test-user-001 尝试访问 other-user-002 的地址
        mockMvc.perform(get(BASE_URL + "/" + addr.getId()))
                .andExpect(status().isBadRequest());  // BusinessException → GlobalExceptionHandler → 400
    }

    @Test
    @DisplayName("更新地址 - 合法输入返回 200 并包含更新后数据")
    void updateAddr_validInput_returns200() throws Exception {
        UserAddr addr = insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        AddressDTO dto = buildValidDTO();
        dto.setId(addr.getId());
        dto.setRcvrName("张三改名");
        dto.setRcvrTel("13900000001");

        mockMvc.perform(put(BASE_URL + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rcvrName").value("张三改名"));
    }

    @Test
    @DisplayName("删除地址 - 合法请求返回 200")
    void deleteAddr_valid_returns200() throws Exception {
        UserAddr addr = insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        mockMvc.perform(delete(BASE_URL + "/" + addr.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除地址 - 删除其他用户地址返回 400（BusinessException）")
    void deleteAddr_otherUserAddr_returns404() throws Exception {
        UserAddr addr = insertAddr(OTHER_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        // 当前用户 test-user-001 尝试删除 other-user-002 的地址
        mockMvc.perform(delete(BASE_URL + "/" + addr.getId()))
                .andExpect(status().isBadRequest());  // BusinessException → GlobalExceptionHandler → 400
    }

    // ================================================================
    //  默认地址 HTTP 测试
    // ================================================================

    @Test
    @DisplayName("设置默认地址 - 合法请求返回 200")
    void setDftAddr_valid_returns200() throws Exception {
        UserAddr addr = insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "N",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        mockMvc.perform(put(BASE_URL + "/default/" + addr.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取默认地址 - 有默认地址时返回默认地址")
    void getDftAddr_hasDefault_returnsDefault() throws Exception {
        insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));
        insertAddr(TEST_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "N",
                LocalDateTime.of(2024, 1, 2, 10, 0));

        mockMvc.perform(get(BASE_URL + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rcvrName").value("张三"))
                .andExpect(jsonPath("$.data.dftIndc").value("Y"));
    }

    @Test
    @DisplayName("获取默认地址 - 无默认地址时返回最近添加的地址")
    void getDftAddr_noDefault_returnsLatest() throws Exception {
        insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "N",
                LocalDateTime.of(2024, 1, 1, 10, 0));
        insertAddr(TEST_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "N",
                LocalDateTime.of(2024, 1, 2, 10, 0));

        mockMvc.perform(get(BASE_URL + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rcvrName").value("李四"));
    }

    @Test
    @DisplayName("获取默认地址 - 无地址时返回空数据")
    void getDftAddr_noAddress_returnsNull() throws Exception {
        mockMvc.perform(get(BASE_URL + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ================================================================
    //  默认地址逻辑测试
    // ================================================================

    @Test
    @DisplayName("添加第一个地址时自动成为默认地址")
    void addAddr_firstAddress_autoDefault() throws Exception {
        AddressDTO dto = buildValidDTO();

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dftIndc").value("Y"));
    }

    @Test
    @DisplayName("添加新地址设为默认时，清除其他默认地址")
    void addAddr_setDefault_clearsOtherDefaults() throws Exception {
        // 先添加第一个地址（自动成为默认）
        AddressDTO dto1 = buildValidDTO();
        dto1.setRcvrName("张三");
        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dftIndc").value("Y"));

        // 再添加第二个地址并设为默认
        AddressDTO dto2 = buildValidDTO();
        dto2.setRcvrName("李四");
        dto2.setRcvrTel("13800000002");
        dto2.setDftIndc("Y");
        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dftIndc").value("Y"));

        // 验证：只能有一个默认地址，且是李四
        mockMvc.perform(get(BASE_URL + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rcvrName").value("李四"));
    }

    @Test
    @DisplayName("删除默认地址后，最近添加的地址被提升为默认")
    void deleteAddr_defaultDeleted_promotesLatest() throws Exception {
        // 手动插入，确保排序确定
        UserAddr a1 = insertAddr(TEST_USER, "张三", "13800000001",
                "110000", "110100", "110105", "朝阳路1号", "Y",
                LocalDateTime.of(2024, 1, 1, 10, 0));
        UserAddr a2 = insertAddr(TEST_USER, "李四", "13800000002",
                "310000", "310100", "310101", "南京路1号", "N",
                LocalDateTime.of(2024, 1, 2, 10, 0));
        UserAddr a3 = insertAddr(TEST_USER, "王五", "13800000003",
                "440000", "440100", "440106", "天河路1号", "N",
                LocalDateTime.of(2024, 1, 3, 10, 0));

        // 删除默认地址（张三）
        mockMvc.perform(delete(BASE_URL + "/" + a1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证：最近添加的王五（entr_time 最晚）被提升为默认
        mockMvc.perform(get(BASE_URL + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rcvrName").value("王五"))
                .andExpect(jsonPath("$.data.dftIndc").value("Y"));
    }
}
