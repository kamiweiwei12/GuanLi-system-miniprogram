-- ============================================
-- Phase 10.5.9 验收测试数据 v3
-- 数据库：inspection_db
-- 特性：完全幂等，部门名称与 sys_user.department_id 严格一致
--        ADMIN 可看到 10 条，DEPT_ADMIN 可看到自己部门 5 条
--        EMPLOYEE 只能看到自己的 5 条
-- 使用方式：
--   mysql --default-character-set=utf8mb4 -u root -p inspection_db < test-data-phase10.5.9.sql
-- ============================================

USE inspection_db;

-- 清理旧的重复部门记录
DELETE FROM sys_department WHERE id > 2;

-- 一、确保角色存在
INSERT IGNORE INTO sys_role (id, role_name, role_code) VALUES
    (1, '普通员工', 'EMPLOYEE'),
    (2, '部门管理员', 'DEPT_ADMIN'),
    (3, '系统管理员', 'ADMIN');

-- 二、确保部门存在（只有两个部门，与 inspection_record.department 一致）
INSERT IGNORE INTO sys_department (id, dept_name, parent_id) VALUES
    (1, '工程部', 0),
    (2, '运维部', 0);

-- 三、创建测试用户
-- admin_test: ADMIN, 工程部
-- dept_admin_test: DEPT_ADMIN, 工程部
-- employee_a_test: EMPLOYEE, 运维部
-- employee_b_test: EMPLOYEE, 工程部
INSERT IGNORE INTO sys_user (id, openid, username, phone, department_id, role_id, status) VALUES
    (101, 'test_openid_admin_101', 'admin_test',      '13800000001', 1, 3, 3),
    (102, 'test_openid_dept_102',  'dept_admin_test', '13800000002', 1, 2, 1),
    (103, 'test_openid_emp_a_103', 'employee_a_test', '13800000003', 2, 1, 1),
    (104, 'test_openid_emp_b_104', 'employee_b_test', '13800000004', 1, 1, 1);

-- 四、创建固定 Token
INSERT INTO sys_token (user_id, token, expire_time) VALUES
    (101, 'admin-test-token-101',      '2099-12-31 23:59:59'),
    (102, 'dept-admin-test-token-102', '2099-12-31 23:59:59'),
    (103, 'employee-a-test-token-103', '2099-12-31 23:59:59'),
    (104, 'employee-b-test-token-104', '2099-12-31 23:59:59')
ON DUPLICATE KEY UPDATE expire_time = '2099-12-31 23:59:59';

-- 先清理旧的测试巡检记录
DELETE FROM inspection_image WHERE record_id BETWEEN 2001 AND 2010;
DELETE FROM inspection_record WHERE id BETWEEN 2001 AND 2010;

-- 五、创建巡检记录
-- employee_a_test: user_id=103, department='运维部' (5条)
-- employee_b_test: user_id=104, department='工程部' (5条)

INSERT IGNORE INTO inspection_record (id, user_id, department, employee_name, phone_number, area, inspection_time, device_name, result, description) VALUES
(2001, 103, '运维部', 'employee_a_test', '13800000003', 'A区', '2026-07-28 10:00:00', '空调设备',   0, '正常运行，温度正常'),
(2002, 103, '运维部', 'employee_a_test', '13800000003', 'B区', '2026-07-28 11:00:00', '服务器设备', 2, '风扇噪音偏大，存在隐患'),
(2003, 103, '运维部', 'employee_a_test', '13800000003', 'C区', '2026-07-27 09:00:00', '监控设备',   0, '画面清晰，存储正常'),
(2004, 103, '运维部', 'employee_a_test', '13800000003', 'A区', '2026-07-27 14:00:00', '服务器设备', 1, '硬盘指示灯红色，需更换'),
(2005, 103, '运维部', 'employee_a_test', '13800000003', 'B区', '2026-07-21 08:00:00', '空调设备',   0, '制冷效果一般，建议清洗滤网'),

(2006, 104, '工程部', 'employee_b_test', '13800000004', 'C区', '2026-07-28 09:30:00', '监控设备',   0, '一切正常'),
(2007, 104, '工程部', 'employee_b_test', '13800000004', 'A区', '2026-07-27 10:30:00', '空调设备',   2, '漏水，需维修'),
(2008, 104, '工程部', 'employee_b_test', '13800000004', 'B区', '2026-07-26 15:00:00', '服务器设备', 0, '运行正常'),
(2009, 104, '工程部', 'employee_b_test', '13800000004', 'C区', '2026-07-25 11:00:00', '监控设备',   1, '摄像头离线'),
(2010, 104, '工程部', 'employee_b_test', '13800000004', 'A区', '2026-07-22 13:00:00', '空调设备',   0, '正常');

-- 六、图片关联
INSERT IGNORE INTO inspection_image (record_id, image_url) VALUES
    (2001, '/uploads/2026/07/28/test-sample-001.jpg'),
    (2001, '/uploads/2026/07/28/test-sample-002.jpg'),
    (2002, '/uploads/2026/07/28/test-sample-003.jpg'),
    (2004, '/uploads/2026/07/27/test-sample-004.jpg');