-- ============================================
-- 企业微信巡检系统 — 数据库初始化脚本
-- 版本：2.0.0（新增用户体系）
-- 数据库：MySQL 8.0+
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS inspection_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE inspection_db;

-- ============================================
-- 系统角色表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    role_name       VARCHAR(50)     NOT NULL                COMMENT '角色名称',
    role_code       VARCHAR(30)     NOT NULL                COMMENT '角色编码（EMPLOYEE / DEPT_ADMIN / ADMIN）',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 预置角色数据（幂等：INSERT IGNORE 基于 UNIQUE role_code 去重）
INSERT IGNORE INTO sys_role (role_name, role_code) VALUES
    ('普通员工',   'EMPLOYEE'),
    ('部门管理员', 'DEPT_ADMIN'),
    ('系统管理员', 'ADMIN');

-- ============================================
-- 部门表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_department (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    dept_name       VARCHAR(100)    NOT NULL                COMMENT '部门名称',
    parent_id       BIGINT          NULL        DEFAULT 0   COMMENT '上级部门 ID（0=顶级部门）',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '软删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ============================================
-- 系统用户表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    openid          VARCHAR(64)     NOT NULL                COMMENT '微信 openid',
    username        VARCHAR(50)     NOT NULL                COMMENT '用户名',
    phone           VARCHAR(20)     NULL                    COMMENT '手机号',
    department_id   BIGINT          NULL                    COMMENT '部门 ID，关联 sys_department.id',
    role_id         BIGINT          NOT NULL                COMMENT '角色 ID，关联 sys_role.id',
    status          TINYINT         NOT NULL DEFAULT 1      COMMENT '状态（0=禁用, 1=正常）',
    nickname        VARCHAR(100)    NULL                    COMMENT '微信昵称',
    avatar_url      VARCHAR(500)    NULL                    COMMENT '微信头像',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '软删除标记',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_user_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ============================================
-- 认证令牌表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_token (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    user_id         BIGINT          NOT NULL                COMMENT '用户 ID，关联 sys_user.id',
    token           VARCHAR(64)     NOT NULL                COMMENT '令牌值（UUID）',
    expire_time     DATETIME        NOT NULL                COMMENT '过期时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_token_value (token),
    INDEX idx_token_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证令牌表';

-- ============================================
-- 巡检记录表
-- ============================================
CREATE TABLE IF NOT EXISTS inspection_record (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    user_id         BIGINT          NOT NULL DEFAULT 0      COMMENT '提交用户 ID，关联 sys_user.id（0=历史数据）',
    department      VARCHAR(100)    NOT NULL                COMMENT '部门（历史快照）',
    employee_name   VARCHAR(50)     NOT NULL                COMMENT '姓名（历史快照）',
    phone_number    VARCHAR(20)     NOT NULL                COMMENT '手机号（历史快照）',
    area            VARCHAR(200)    NOT NULL                COMMENT '巡检区域',
    inspection_time DATETIME        NOT NULL                COMMENT '巡检时间（业务时间）',
    device_name     VARCHAR(200)    NOT NULL                COMMENT '设备名称',
    result          TINYINT         NOT NULL                COMMENT '巡检结果（0=正常, 1=故障, 2=隐患）',
    description     TEXT            NULL                    COMMENT '详细描述',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '软删除标记（0=正常, 1=已删除）',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检记录表';

-- ============================================
-- 巡检图片表
-- ============================================
CREATE TABLE IF NOT EXISTS inspection_image (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    record_id   BIGINT          NOT NULL                COMMENT '关联的巡检记录 ID',
    image_url   VARCHAR(500)    NOT NULL                COMMENT '图片存储路径',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检图片表';

-- ============================================
-- 索引
-- ============================================

-- 巡检记录：按用户筛选
CREATE INDEX idx_record_user_id
    ON inspection_record (user_id);

-- 巡检记录：按部门筛选
CREATE INDEX idx_record_department
    ON inspection_record (department);

-- 巡检记录：按区域筛选
CREATE INDEX idx_record_area
    ON inspection_record (area);

-- 巡检记录：按日期筛选
CREATE INDEX idx_record_inspection_time
    ON inspection_record (inspection_time);

-- 巡检记录：按设备筛选
CREATE INDEX idx_record_device
    ON inspection_record (device_name);

-- 巡检图片：按记录 ID 关联查询
CREATE INDEX idx_image_record_id
    ON inspection_image (record_id);

-- ============================================
-- 预置管理员（Docker 首次启动自动创建）
-- ============================================
INSERT IGNORE INTO sys_department (id, dept_name, parent_id) VALUES (1, '管理部', 0);
INSERT IGNORE INTO sys_user (id, openid, username, phone, department_id, role_id, status) VALUES (1, 'admin_default', '系统管理员', NULL, 1, 3, 1);
INSERT IGNORE INTO sys_token (user_id, token, expire_time) VALUES (1, 'admin-setup-token', '2099-12-31 23:59:59');