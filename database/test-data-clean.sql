-- ============================================
-- Phase 13 测试数据清理脚本
-- ============================================
-- 使用方式：
--   mysql -u inspection -p inspection_db < database/test-data-clean.sql
--
-- 说明：
--   - 仅删除 test-data.sql 插入的数据
--   - 不影响 init.sql 预置的管理员数据（id=1）
--   - 不影响正式数据（id < 10 或 id 不在测试范围内）
-- ============================================

USE inspection_db;

-- 删除测试巡检记录（user_id >= 10 的都是测试用户的数据）
DELETE FROM inspection_record WHERE user_id >= 10;

-- 删除测试用户（id >= 10）
DELETE FROM sys_user WHERE id >= 10;

-- 删除测试部门（id >= 10）
DELETE FROM sys_department WHERE id >= 10;