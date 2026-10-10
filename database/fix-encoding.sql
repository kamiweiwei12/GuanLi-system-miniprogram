-- ============================================
-- Phase 13 编码修复脚本
-- ============================================
-- 使用方式：
--   docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u inspection -pinspection_pwd_2026 inspection_db < database\fix-encoding.sql
--
-- 说明：
--   - 修复之前因 PowerShell 管道编码问题导致的 sys_role 乱码
--   - 使用 UPDATE 语句，不删除已有数据
--   - 不影响表结构和业务逻辑
-- ============================================

USE inspection_db;

-- 修复 sys_role 的 role_name（按 id 精确修复）
UPDATE sys_role SET role_name = '普通员工'   WHERE id = 1;
UPDATE sys_role SET role_name = '部门管理员' WHERE id = 2;
UPDATE sys_role SET role_name = '系统管理员' WHERE id = 3;

-- 验证修复结果
SELECT id, role_code, role_name FROM sys_role;