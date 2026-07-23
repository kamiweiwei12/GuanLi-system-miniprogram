-- ============================================
-- 企业微信巡检系统 — 数据库初始化脚本
-- 版本：1.0.0
-- 数据库：MySQL 8.0+
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS inspection_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE inspection_db;

-- ============================================
-- 巡检记录表
-- ============================================
CREATE TABLE IF NOT EXISTS inspection_record (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    department      VARCHAR(100)    NOT NULL                COMMENT '部门',
    employee_name   VARCHAR(50)     NOT NULL                COMMENT '姓名',
    phone_number    VARCHAR(20)     NOT NULL                COMMENT '手机号',
    area            VARCHAR(200)    NOT NULL                COMMENT '巡检区域',
    inspection_time DATETIME        NOT NULL                COMMENT '巡检时间（业务时间）',
    device_name     VARCHAR(200)    NOT NULL                COMMENT '设备名称',
    result          TINYINT         NOT NULL                COMMENT '巡检结果（0=正常, 1=异常）',
    description     TEXT            NULL                    COMMENT '详细描述',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP           COMMENT '记录创建时间',
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