-- 用户管理测试用（预置 ADMIN 用户 + token + sys_department）
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

CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO sys_department (dept_name) VALUES ('工程部'), ('运维部');

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

-- 预置 ADMIN 用户
INSERT INTO sys_user (openid, username, role_id, department_id) VALUES ('admin_test_openid', 'AdminTest', 3, 1)
    ON DUPLICATE KEY UPDATE username = username;

-- 预置 ADMIN token
INSERT IGNORE INTO sys_token (user_id, token, expire_time)
SELECT id, 'test-token-admin', DATE_ADD(NOW(), INTERVAL 7 DAY)
FROM sys_user WHERE openid = 'admin_test_openid';

-- 预置 EMPLOYEE 用户
INSERT INTO sys_user (openid, username, role_id, department_id) VALUES ('emp_test_openid', 'EmpTest', 1, 2)
    ON DUPLICATE KEY UPDATE username = username;

-- 预置 EMPLOYEE token
INSERT IGNORE INTO sys_token (user_id, token, expire_time)
SELECT id, 'test-token-employee', DATE_ADD(NOW(), INTERVAL 7 DAY)
FROM sys_user WHERE openid = 'emp_test_openid';