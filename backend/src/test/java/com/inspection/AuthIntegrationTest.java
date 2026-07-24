package com.inspection;

import com.inspection.common.UserContext;
import com.inspection.service.TokenService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证框架集成测试（Phase 7）
 * 使用 @Sql 预置用户 + token，验证 TokenService → AuthInterceptor → UserContext 完整链路
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/sql/init-auth-test.sql")
class AuthIntegrationTest {

    /** SQL 预置的有效 token */
    private static final String VALID_TOKEN = "test-token-valid";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Test
    @Order(1)
    @DisplayName("测试1：根据预置 token 查询 userId")
    void testCreateTokenAndQuery() {
        Long userId = tokenService.getUserIdByToken(VALID_TOKEN);
        assertNotNull(userId, "有效 token 应能查到 userId");
        assertTrue(userId > 0, "userId 应为正数");
    }

    @Test
    @Order(2)
    @DisplayName("测试2：携带有效 Token → Interceptor 放行")
    void testInterceptorValidToken() throws Exception {
        mockMvc.perform(get("/api/v1/report")
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("测试3：无 Token → 401")
    void testInterceptorNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/report"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("缺少认证令牌"));
    }

    @Test
    @Order(4)
    @DisplayName("测试4：错误 Token → 401")
    void testInterceptorInvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/report")
                        .header("Authorization", "Bearer invalid-token-xxxx"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @AfterAll
    static void cleanup() {
        UserContext.clear();
    }
}