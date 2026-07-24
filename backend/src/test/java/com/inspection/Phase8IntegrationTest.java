package com.inspection;

import com.inspection.entity.User;
import com.inspection.service.TokenService;
import com.inspection.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 8 最终集成验证
 * 验证 4 个场景：user_id绑定 / 数据隔离 / ADMIN可见全部 / 历史数据兼容
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/sql/init-phase8-test.sql")
class Phase8IntegrationTest {

    private static String tokenA;
    private static String tokenB;
    private static String tokenAdmin;
    private static Long userIdA;
    private static Long userIdB;

    @Autowired private MockMvc mockMvc;
    @Autowired private TokenService tokenService;
    @Autowired private UserService userService;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setUp(@Autowired UserService userService,
                      @Autowired TokenService tokenService,
                      @Autowired JdbcTemplate jdbcTemplate) {
        // 创建 EMPLOYEE 用户 A
        userService.createDefaultUser("test_phase8_userA");
        User userA = userService.getByOpenid("test_phase8_userA");
        userIdA = userA.getId();
        tokenA = tokenService.createToken(userIdA);

        // 创建 EMPLOYEE 用户 B
        userService.createDefaultUser("test_phase8_userB");
        User userB = userService.getByOpenid("test_phase8_userB");
        userIdB = userB.getId();
        tokenB = tokenService.createToken(userIdB);

        // 创建 ADMIN 用户（role_id=3，对应预置 ADMIN 角色）
        userService.createDefaultUser("test_phase8_admin");
        User admin = userService.getByOpenid("test_phase8_admin");
        jdbcTemplate.update("UPDATE sys_user SET role_id = 3 WHERE id = ?", admin.getId());
        tokenAdmin = tokenService.createToken(admin.getId());

        // 插入一条 user_id=0 的历史数据
        jdbcTemplate.update("INSERT INTO inspection_record " +
                "(user_id, department, employee_name, phone_number, area, inspection_time, device_name, result) " +
                "VALUES (0, '历史数据', '匿名', '00000000000', '旧区', '2026-01-01T00:00:00', '旧设备', 0)");
    }

    @AfterAll
    static void tearDown(@Autowired TokenService tokenService,
                         @Autowired JdbcTemplate jdbcTemplate) {
        tokenService.invalidate(userIdA);
        tokenService.invalidate(userIdB);
        jdbcTemplate.update("DELETE FROM inspection_record WHERE user_id IN (?, ?, 0)", userIdA, userIdB);
        jdbcTemplate.update("DELETE FROM sys_user WHERE openid LIKE 'test_phase8_%'");
    }

    final String jsonBody = "{"
            + "\"department\":\"Phase8验证\","
            + "\"employeeName\":\"测试员\","
            + "\"phoneNumber\":\"13800000000\","
            + "\"area\":\"Z区\","
            + "\"inspectionTime\":\"2026-07-24T12:00:00\","
            + "\"deviceName\":\"测试设备\","
            + "\"result\":0"
            + "}";

    @Test
    @Order(1)
    @DisplayName("Test1：EMPLOYEE 用户A提交 → user_id 正确写入")
    void testSubmitUserIdBinding() throws Exception {
        mockMvc.perform(post("/api/v1/report")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long latestUserId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM inspection_record ORDER BY id DESC LIMIT 1", Long.class);
        assertEquals(userIdA, latestUserId, "记录 user_id 应为提交用户A的ID");
    }

    @Test
    @Order(2)
    @DisplayName("Test2：EMPLOYEE 用户B查询 → 看不到用户A数据")
    void testEmployeeIsolation() throws Exception {
        String resp = mockMvc.perform(get("/api/v1/report")
                        .header("Authorization", "Bearer " + tokenB)
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertFalse(resp.contains("Phase8验证"), "用户B不应看到用户A的记录");
    }

    @Test
    @Order(3)
    @DisplayName("Test3：ADMIN 查询 → 可以看到用户A和B的记录")
    void testAdminSeesAll() throws Exception {
        String resp = mockMvc.perform(get("/api/v1/report")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(resp.contains("Phase8验证"), "ADMIN 应能看到用户A的记录");
    }

    @Test
    @Order(4)
    @DisplayName("Test4：user_id=0 历史数据 → ADMIN 可见，EMPLOYEE 不可见")
    void testLegacyDataVisibleAdmin() throws Exception {
        // ADMIN 可以看到历史数据
        String adminResp = mockMvc.perform(get("/api/v1/report")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(adminResp.contains("历史数据"), "ADMIN 应能看到 user_id=0 的历史记录");

        // EMPLOYEE 不应看到历史数据
        String empResp = mockMvc.perform(get("/api/v1/report")
                        .header("Authorization", "Bearer " + tokenA)
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertFalse(empResp.contains("历史数据"), "EMPLOYEE 不应看到 user_id=0 的历史记录");
    }
}