-- Phase 9 登录集成测试 SQL（建表 + 预置角色 + 测试数据清理）
-- 清理测试残留数据（防止与其他测试类同时运行时冲突）
DELETE FROM sys_token WHERE user_id IN (SELECT id FROM sys_user WHERE openid LIKE 'test_openid_%');
DELETE FROM sys_user WHERE openid LIKE 'test_openid_%';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_role (role_name, role_code) VALUES
    ('普通员工', 'EMPLOYEE'),
    ('部门管理员', 'DEPT_ADMIN'),
    ('系统管理员', 'ADMIN');

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    openid VARCHAR(64) NOT NULL,
    username VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NULL,
    department_id BIGINT NULL,
    role_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    nickname VARCHAR(100) NULL,
    avatar_url VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_user_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(64) NOT NULL,
    expire_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_token_value (token),
    INDEX idx_token_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;