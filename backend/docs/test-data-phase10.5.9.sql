-- ============================================
-- Phase 10.5.9 验收测试数据 v2
-- 数据库：inspection_db
-- 特性：完全幂等（INSERT IGNORE），不依赖 LAST_INSERT_ID
--       同一 session / 分步执行 / GUI 工具均可
-- 使用方式：mysql -u root -p inspection_db < test-data-phase10.5.9.sql
-- ============================================

USE inspection_db;

-- ============================================
-- 一、确保角色存在（幂等）
-- ============================================
INSERT IGNORE INTO sys_role (id, role_name, role_code) VALUES
    (1, '普通员工', 'EMPLOYEE'),
    (2, '部门管理员', 'DEPT_ADMIN'),
    (3, '系统管理员', 'ADMIN');

-- ============================================
-- 二、确保部门存在（幂等）
-- ============================================
INSERT IGNORE INTO sys_department (id, dept_name, parent_id) VALUES
    (1, '系统管理部', 0),
    (2, '技术部', 0),
    (3, '市场部', 0);

-- ============================================
-- 三、创建测试用户（幂等）
-- ============================================
INSERT IGNORE INTO sys_user (id, openid, username, phone, department_id, role_id, status) VALUES
    (101, 'test_openid_admin_101', 'admin_test',      '13800000001', 1, 3, 1),
    (102, 'test_openid_dept_102',  'dept_admin_test', '13800000002', 2, 2, 1),
    (103, 'test_openid_emp_a_103', 'employee_a_test', '13800000003', 2, 1, 1),
    (104, 'test_openid_emp_b_104', 'employee_b_test', '13800000004', 3, 1, 1);

-- ============================================
-- 四、创建固定 Token（幂等）
-- ============================================
INSERT INTO sys_token (user_id, token, expire_time) VALUES
    (101, 'admin-test-token-101',      '2099-12-31 23:59:59'),
    (102, 'dept-admin-test-token-102', '2099-12-31 23:59:59'),
    (103, 'employee-a-test-token-103', '2099-12-31 23:59:59'),
    (104, 'employee-b-test-token-104', '2099-12-31 23:59:59')
ON DUPLICATE KEY UPDATE expire_time = '2099-12-31 23:59:59';

-- ============================================
-- 五、创建巡检记录（幂等，固定 ID 2001-2010）
-- ============================================
INSERT IGNORE INTO inspection_record (id, user_id, department, employee_name, phone_number, area, inspection_time, device_name, result, description) VALUES
-- 技术部 - employee_a_test (user_id=103)
(2001, 103, '技术部', 'employee_a_test', '13800000003', 'A区', '2026-07-28 10:00:00', '空调设备',   0, '正常运行，温度正常'),
(2002, 103, '技术部', 'employee_a_test', '13800000003', 'B区', '2026-07-28 11:00:00', '服务器设备', 2, '风扇噪音偏大，存在隐患'),
(2003, 103, '技术部', 'employee_a_test', '13800000003', 'C区', '2026-07-27 09:00:00', '监控设备',   0, '画面清晰，存储正常'),
(2004, 103, '技术部', 'employee_a_test', '13800000003', 'A区', '2026-07-27 14:00:00', '服务器设备', 1, '硬盘指示灯红色，需更换'),
(2005, 103, '技术部', 'employee_a_test', '13800000003', 'B区', '2026-07-21 08:00:00', '空调设备',   0, '制冷效果一般，建议清洗滤网'),

-- 市场部 - employee_b_test (user_id=104)
(2006, 104, '市场部', 'employee_b_test', '13800000004', 'C区', '2026-07-28 09:30:00', '监控设备',   0, '一切正常'),
(2007, 104, '市场部', 'employee_b_test', '13800000004', 'A区', '2026-07-27 10:30:00', '空调设备',   2, '漏水，需维修'),
(2008, 104, '市场部', 'employee_b_test', '13800000004', 'B区', '2026-07-26 15:00:00', '服务器设备', 0, '运行正常'),
(2009, 104, '市场部', 'employee_b_test', '13800000004', 'C区', '2026-07-25 11:00:00', '监控设备',   1, '摄像头离线'),
(2010, 104, '市场部', 'employee_b_test', '13800000004', 'A区', '2026-07-22 13:00:00', '空调设备',   0, '正常');

-- ============================================
-- 六、创建图片关联（幂等，引用固定 record_id）
-- ============================================
INSERT IGNORE INTO inspection_image (record_id, image_url) VALUES
    (2001, '/uploads/2026/07/28/test-sample-001.jpg'),
    (2001, '/uploads/2026/07/28/test-sample-002.jpg'),
    (2002, '/uploads/2026/07/28/test-sample-003.jpg'),
    (2004, '/uploads/2026/07/27/test-sample-004.jpg');

-- ============================================
-- 执行后验证
-- ============================================
SELECT '=== 用户数据 ===' AS '';
SELECT u.id, u.username, r.role_code AS role, d.dept_name AS department
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
LEFT JOIN sys_department d ON u.department_id = d.id
WHERE u.id IN (101, 102, 103, 104);

SELECT '=== Token 数据 ===' AS '';
SELECT t.token, u.username, t.expire_time
FROM sys_token t
JOIN sys_user u ON t.user_id = u.id
WHERE u.id IN (101, 102, 103, 104);

SELECT '=== 巡检记录（测试数据）===' AS '';
SELECT id, department, employee_name, area, device_name, result, DATE(inspection_time) AS 日期
FROM inspection_record
WHERE id BETWEEN 2001 AND 2010;

SELECT '=== 图片关联数据 ===' AS '';
SELECT i.id, i.record_id, i.image_url
FROM inspection_image i
WHERE i.record_id BETWEEN 2001 AND 2010;