package com.inspection;

import com.inspection.common.UserContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理集成测试（Phase 10.4）
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/sql/init-user-management-test.sql")
class UserManagementTest {

    private static final String ADMIN_TOKEN = "test-token-admin";
    private static final String EMPLOYEE_TOKEN = "test-token-employee";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    @DisplayName("测试1：ADMIN 查询用户列表 → 200")
    void testAdminListUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(2)
    @DisplayName("测试2：EMPLOYEE 查询用户列表 → 403")
    void testEmployeeListUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + EMPLOYEE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @AfterAll
    static void cleanup() {
        UserContext.clear();
    }
}