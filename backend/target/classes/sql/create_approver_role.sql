-- 创建审批角色及权限配置

-- 1. 创建审批员角色（如果不存在）
INSERT IGNORE INTO sys_role (id, name, code, remark, customer_id, create_time, update_time, deleted) 
VALUES 
('role_approver', '审批员', 'APPROVER', '审批员角色，可以进行预约和审批操作', 'demo', NOW(), NOW(), 0);

-- 2. 创建测试审批员用户（如果不存在）
INSERT IGNORE INTO sys_user (id, username, password, real_name, gender, phone, job_number, 
                            department_id, department_name, position_id, position_name, 
                            title_id, title_name, avatar_url, face_image_url, card_number, 
                            attendance_number, status, customer_id, create_time, update_time, deleted) 
VALUES 
('approver_001', 'approver', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZOzx8Qb6Nh8Qw5Qz5Qz5Qz5Qz', 
 '李审批', '男', '13800138003', 'A001', 
 'dept002', '教务处', 'pos002', '主管', 
 'title003', '中级', NULL, NULL, 'CARD004', 
 'ATT004', 'NORMAL', 'demo', NOW(), NOW(), 0);

-- 3. 分配角色给审批员用户
INSERT IGNORE INTO sys_user_role (id, user_id, role_id, create_time) 
VALUES 
('ur_approver_001', 'approver_001', 'role_approver', NOW());

-- 4. 清除审批员角色的所有权限（如果有的话）
DELETE FROM sys_role_permission WHERE role_id = 'role_approver';

-- 5. 分配审批员角色的权限
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
-- 教室中心（父菜单）
('rp_approver_001', 'role_approver', 'perm_room_center', NOW(), 'demo'),

-- 教室管理菜单及查看权限
('rp_approver_002', 'role_approver', 'perm022', NOW(), 'demo'),  -- ROOM_MANAGE 教室管理菜单
('rp_approver_003', 'role_approver', 'perm023', NOW(), 'demo'),  -- ROOM_SEARCH 教室查询
('rp_approver_004', 'role_approver', 'perm024', NOW(), 'demo'),  -- ROOM_VIEW 教室查看
('rp_approver_005', 'role_approver', 'perm028', NOW(), 'demo'),  -- ROOM_BOOK 教室预订

-- 预订管理菜单及所有预订相关权限
('rp_approver_006', 'role_approver', 'perm031', NOW(), 'demo'),  -- BOOKING_MANAGE 预订管理菜单
('rp_approver_007', 'role_approver', 'perm032', NOW(), 'demo'),  -- BOOKING_SEARCH 预订查询
('rp_approver_008', 'role_approver', 'perm033', NOW(), 'demo'),  -- BOOKING_VIEW 预订查看
('rp_approver_009', 'role_approver', 'perm034', NOW(), 'demo'),  -- BOOKING_CREATE 创建预订
('rp_approver_010', 'role_approver', 'perm035', NOW(), 'demo'),  -- BOOKING_EDIT 编辑预订
('rp_approver_011', 'role_approver', 'perm036', NOW(), 'demo'),  -- BOOKING_CANCEL 取消预订
('rp_approver_012', 'role_approver', 'perm037', NOW(), 'demo'),  -- BOOKING_APPROVE 审批预订（核心权限）
('rp_approver_013', 'role_approver', 'perm038', NOW(), 'demo'),  -- BOOKING_REJECT 拒绝预订

-- 必要的API权限
('rp_approver_014', 'role_approver', 'perm058', NOW(), 'demo'),  -- AREA_VIEW 区域查看
('rp_approver_015', 'role_approver', 'perm106', NOW(), 'demo'),  -- ROOM_SCHEDULE_VIEW 教室日程查看
('rp_approver_016', 'role_approver', 'perm053', NOW(), 'demo');  -- SCHEME_VIEW 方案查看

-- 6. 验证权限分配
SELECT '=== 审批员角色权限列表 ===' as title;
SELECT p.code, p.name, p.type
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_approver'
ORDER BY p.type, p.code;

-- 7. 对比三个角色的权限
SELECT '=== 角色权限对比 ===' as title;
SELECT 
    r.name as role_name,
    COUNT(CASE WHEN p.code LIKE 'BOOKING%' THEN 1 END) as booking_permissions,
    COUNT(CASE WHEN p.code = 'BOOKING_APPROVE' THEN 1 END) as has_approve,
    COUNT(CASE WHEN p.code = 'BOOKING_REJECT' THEN 1 END) as has_reject,
    COUNT(CASE WHEN p.code = 'BOOKING_RECORDS_VIEW' THEN 1 END) as has_records,
    COUNT(*) as total_permissions
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.id IN ('role001', 'role_normal', 'role_approver')
GROUP BY r.id, r.name;