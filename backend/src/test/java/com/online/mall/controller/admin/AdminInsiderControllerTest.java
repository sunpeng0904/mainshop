package com.online.mall.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.mall.entity.InsiderInfo;
import com.online.mall.service.InsiderInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 内幕信息知情人登记Controller单元测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminInsiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InsiderInfoService insiderInfoService;

    private InsiderInfo testInsider;

    @BeforeEach
    void setUp() {
        testInsider = new InsiderInfo();
        testInsider.setCompanyName("测试企业有限公司");
        testInsider.setAcceptTime(LocalDate.of(2025, 1, 15));
        testInsider.setBoard("主板（沪市）");
        testInsider.setFinancingType("首次公开发行股票");
        testInsider.setIndustry("制造业");
        testInsider.setKnowledgeTime(LocalDate.of(2025, 1, 10));
        testInsider.setReason("测试理由");
        testInsider.setContent("测试知情内容");
        testInsider.setInsiderName("张三");
        testInsider.setRegisterTime(LocalDate.of(2025, 1, 12));
        testInsider.setStatus("draft");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateInsider() throws Exception {
        mockMvc.perform(post("/admin/insider")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInsider)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetInsiderList() throws Exception {
        // 先创建一条数据
        insiderInfoService.createInsider(testInsider);

        mockMvc.perform(get("/admin/insider/list")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetInsiderListWithFilters() throws Exception {
        // 先创建一条数据
        insiderInfoService.createInsider(testInsider);

        mockMvc.perform(get("/admin/insider/list")
                .param("page", "1")
                .param("pageSize", "10")
                .param("companyName", "测试")
                .param("board", "主板（沪市）")
                .param("industry", "制造业"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetInsiderDetail() throws Exception {
        // 先创建一条数据
        insiderInfoService.createInsider(testInsider);

        mockMvc.perform(get("/admin/insider/" + testInsider.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.companyName").value("测试企业有限公司"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetInsiderDetailNotFound() throws Exception {
        mockMvc.perform(get("/admin/insider/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateInsider() throws Exception {
        // 先创建一条数据
        insiderInfoService.createInsider(testInsider);

        testInsider.setCompanyName("更新后的企业名称");
        testInsider.setStatus("pending");

        mockMvc.perform(put("/admin/insider/" + testInsider.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testInsider)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteInsider() throws Exception {
        // 先创建一条数据
        insiderInfoService.createInsider(testInsider);

        mockMvc.perform(delete("/admin/insider/" + testInsider.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateStatus() throws Exception {
        // 先创建一条数据
        insiderInfoService.createInsider(testInsider);

        mockMvc.perform(put("/admin/insider/" + testInsider.getId() + "/status")
                .param("status", "pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetInsiderListWithPagination() throws Exception {
        // 创建多条数据
        for (int i = 0; i < 15; i++) {
            InsiderInfo insider = new InsiderInfo();
            insider.setCompanyName("企业" + i);
            insider.setAcceptTime(LocalDate.of(2025, 1, i + 1));
            insider.setBoard("主板（沪市）");
            insider.setFinancingType("首次公开发行股票");
            insider.setIndustry("制造业");
            insider.setKnowledgeTime(LocalDate.of(2025, 1, i + 1));
            insider.setReason("理由" + i);
            insider.setInsiderName("知情人" + i);
            insider.setRegisterTime(LocalDate.of(2025, 1, i + 1));
            insider.setStatus("draft");
            insiderInfoService.createInsider(insider);
        }

        // 测试第一页
        mockMvc.perform(get("/admin/insider/list")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(10))
                .andExpect(jsonPath("$.data.total").value(15));

        // 测试第二页
        mockMvc.perform(get("/admin/insider/list")
                .param("page", "2")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(5));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetInsiderListWithStatusFilter() throws Exception {
        // 创建不同状态的数据
        testInsider.setStatus("draft");
        insiderInfoService.createInsider(testInsider);

        InsiderInfo insider2 = new InsiderInfo();
        insider2.setCompanyName("企业2");
        insider2.setAcceptTime(LocalDate.of(2025, 1, 15));
        insider2.setBoard("主板（沪市）");
        insider2.setFinancingType("首次公开发行股票");
        insider2.setIndustry("制造业");
        insider2.setKnowledgeTime(LocalDate.of(2025, 1, 10));
        insider2.setReason("理由");
        insider2.setInsiderName("知情人");
        insider2.setRegisterTime(LocalDate.of(2025, 1, 12));
        insider2.setStatus("approved");
        insiderInfoService.createInsider(insider2);

        // 按状态筛选
        mockMvc.perform(get("/admin/insider/list")
                .param("page", "1")
                .param("pageSize", "10")
                .param("status", "draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
