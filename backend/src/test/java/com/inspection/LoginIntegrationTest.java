package com.inspection;

import com.inspection.entity.User;
import com.inspection.mapper.UserMapper;
import com.inspection.service.TokenService;
import com.inspection.service.WechatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 登录集成测试（Phase 9）
 * Mock WechatService 避免真实微信 API 调用，
 * 验证完整登录链路：code → openid → 查/创用户 → 生成 token
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/sql/init-login-test.sql")
class LoginIntegrationTest {

    private static final String TEST_OPENID_NEW = "test_openid_new_user_001";
    private static final String TEST_OPENID_EXISTING = "test_openid_existing_001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    /** Mock 微信服务，返回模拟 openid */
    @MockBean
    private WechatService wechatService;

    @BeforeEach
    void setUp() {
        // 每个测试前重置 mock 行为（默认）
    }

    // =====================================================
    // 测试1：新用户首次登录
    // =====================================================
    @Test
    @Order(1)
    @DisplayName("测试1：新用户首次登录 → 自动创建用户 + 生成 token + 默认角色 EMPLOYEE")
    void testNewUserLogin() throws Exception {
        // Mock WechatService 返回新的 openid
        when(wechatService.getOpenid(anyString())).thenReturn(TEST_OPENID_NEW);

        String response = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"test_code_001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").isNumber())
                .andExpect(jsonPath("$.data.role").value("EMPLOYEE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("测试1 - 新用户登录响应: " + response);

        // 验证 token 有效性
        String token = extractTokenFromJson(response);
        Long userId = tokenService.getUserIdByToken(token);
        assertNotNull(userId, "生成的 token 应能查到 userId");

        // 验证用户已创建
        User user = userMapper.selectByOpenid(TEST_OPENID_NEW);
        assertNotNull(user, "新用户应已创建");
        assertEquals(TEST_OPENID_NEW, user.getOpenid());
        assertEquals(Long.valueOf(1L), user.getRoleId(), "默认角色应为 EMPLOYEE (id=1)");
        assertEquals(Integer.valueOf(1), user.getStatus(), "状态应为 1（正常）");
    }

    // =====================================================
    // 测试2：已存在用户登录
    // =====================================================
    @Test
    @Order(2)
    @DisplayName("测试2：已存在用户登录 → 不重复创建，直接返回 token")
    void testExistingUserLogin() throws Exception {
        // 先手动创建用户（模拟已存在的用户）
        User existingUser = new User();
        existingUser.setOpenid(TEST_OPENID_EXISTING);
        existingUser.setUsername("测试用户");
        existingUser.setRoleId(2L); // DEPT_ADMIN
        existingUser.setStatus(1);
        userMapper.insert(existingUser);
        assertNotNull(existingUser.getId(), "手动创建的用户应有 ID");

        // Mock WechatService 返回重复的 openid
        when(wechatService.getOpenid(anyString())).thenReturn(TEST_OPENID_EXISTING);

        String response = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"test_code_002\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(existingUser.getId()))
                .andExpect(jsonPath("$.data.role").value("DEPT_ADMIN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("测试2 - 已存在用户登录响应: " + response);

        // 验证 token 有效性
        String token = extractTokenFromJson(response);
        Long userId = tokenService.getUserIdByToken(token);
        assertEquals(existingUser.getId(), userId, "token 应对应已存在用户");

        // 验证没有重复创建用户
        User user = userMapper.selectByOpenid(TEST_OPENID_EXISTING);
        assertNotNull(user);
        assertEquals(existingUser.getId(), user.getId(), "应为同一用户");
        assertEquals(Long.valueOf(2L), user.getRoleId(), "角色应保持 DEPT_ADMIN");
    }

    // =====================================================
    // 测试3：Token 生成验证（工具方法测试）
    // =====================================================
    @Test
    @Order(3)
    @DisplayName("测试3：Token 生成 → 可查询 + 7天有效期")
    void testTokenCreation() {
        // 先手动创建用户
        User testUser = new User();
        testUser.setOpenid("test_openid_token_test_001");
        testUser.setUsername("Token测试用户");
        testUser.setRoleId(1L);
        testUser.setStatus(1);
        userMapper.insert(testUser);
        assertNotNull(testUser.getId(), "手动创建的用户应有 ID");

        String token = tokenService.createToken(testUser.getId());
        assertNotNull(token, "token 不应为 null");
        assertFalse(token.isBlank(), "token 不应为空");
        assertFalse(token.contains("-"), "UUID token 不应包含连字符");

        // 验证可通过 token 查询 userId
        Long userId = tokenService.getUserIdByToken(token);
        assertEquals(testUser.getId(), userId, "token 应能查到正确的 userId");
    }

    // =====================================================
    // 测试4：缺少 code 参数 → 400
    // =====================================================
    @Test
    @Order(4)
    @DisplayName("测试4：缺少 code 参数 → 400 参数错误")
    void testMissingCode() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("登录凭证 code 不能为空"));
    }

    // =====================================================
    // 清理
    // =====================================================
    @AfterAll
    static void cleanup(@Autowired UserMapper userMapper, @Autowired TokenService tokenService) {
        // 清理测试数据（通过 AfterAll + SQL 方式无法注入，故手动清理）
        // 测试结束后数据保留在数据库中（测试库可接受），正式环境执行 SQL 清理即可
    }

    /**
     * 从 JSON 响应中提取 token 字段
     */
    private String extractTokenFromJson(String json) {
        // 简单的手动解析，避免引入额外依赖
        int tokenStart = json.indexOf("\"token\":\"") + 9;
        if (tokenStart < 9) return null;
        int tokenEnd = json.indexOf("\"", tokenStart);
        return json.substring(tokenStart, tokenEnd);
    }
}