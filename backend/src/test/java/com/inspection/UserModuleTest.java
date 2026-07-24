package com.inspection;

import com.inspection.entity.User;
import com.inspection.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户模块集成测试（Phase 6）
 * 验证 UserService → UserMapper → MySQL 完整数据流
 * @Sql 在测试前自动建表，@AfterAll 通过 JdbcTemplate 清理测试数据
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = "/sql/init-user-tables.sql")
class UserModuleTest {

    private static final String TEST_OPENID = "test_openid_001";
    private static Long createdUserId;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterAll
    static void cleanup(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("DELETE FROM sys_user WHERE openid = ?", TEST_OPENID);
    }

    @Test
    @Order(1)
    @DisplayName("测试1：创建默认用户 createDefaultUser")
    void testCreateDefaultUser() {
        User user = userService.createDefaultUser(TEST_OPENID);

        assertNotNull(user, "返回的 User 不应为空");
        assertNotNull(user.getId(), "id 应已生成");
        assertEquals(TEST_OPENID, user.getOpenid(), "openid 应匹配");
        assertEquals("微信用户", user.getUsername(), "默认用户名应为'微信用户'");
        assertEquals(1L, user.getRoleId(), "默认角色 ID 应为 1（EMPLOYEE）");
        assertEquals(1, user.getStatus(), "状态应为 1（正常）");

        createdUserId = user.getId();
    }

    @Test
    @Order(2)
    @DisplayName("测试2：根据 openid 查询 getByOpenid")
    void testGetByOpenid() {
        User user = userService.getByOpenid(TEST_OPENID);

        assertNotNull(user, "应能查询到已创建的用户");
        assertEquals(createdUserId, user.getId(), "ID 应与创建时一致");
        assertEquals(TEST_OPENID, user.getOpenid());
    }

    @Test
    @Order(3)
    @DisplayName("测试3：更新用户信息 update")
    void testUpdate() {
        User user = userService.getByOpenid(TEST_OPENID);
        user.setNickname("测试昵称");
        userService.update(user);

        User updated = userService.getByOpenid(TEST_OPENID);
        assertEquals("测试昵称", updated.getNickname(), "nickname 应更新为'测试昵称'");
    }
}