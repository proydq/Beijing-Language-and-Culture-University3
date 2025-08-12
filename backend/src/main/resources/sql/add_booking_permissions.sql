-- 添加房屋借用相关的权限

-- 1. 检查并添加缺失的权限
-- 数据记录查看权限
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time)
VALUES ('perm_booking_records', 'BOOKING_RECORDS_VIEW', '数据记录查看', 'BUTTON', 'perm031', NULL, 10, 'demo', 0, NOW());

-- 2. 为管理员角色添加所有房屋借用相关权限
-- 假设管理员角色ID是 'role001'
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id)
VALUES 
('rp_admin_booking_001', 'role001', 'perm031', NOW(), 'demo'),  -- BOOKING_MANAGE
('rp_admin_booking_002', 'role001', 'perm032', NOW(), 'demo'),  -- BOOKING_SEARCH
('rp_admin_booking_003', 'role001', 'perm033', NOW(), 'demo'),  -- BOOKING_VIEW
('rp_admin_booking_004', 'role001', 'perm034', NOW(), 'demo'),  -- BOOKING_CREATE
('rp_admin_booking_005', 'role001', 'perm035', NOW(), 'demo'),  -- BOOKING_EDIT
('rp_admin_booking_006', 'role001', 'perm036', NOW(), 'demo'),  -- BOOKING_CANCEL
('rp_admin_booking_007', 'role001', 'perm037', NOW(), 'demo'),  -- BOOKING_APPROVE
('rp_admin_booking_008', 'role001', 'perm038', NOW(), 'demo'),  -- BOOKING_REJECT
('rp_admin_booking_009', 'role001', 'perm063', NOW(), 'demo'),  -- BOOKING_CONFIG
('rp_admin_booking_010', 'role001', 'perm_booking_records', NOW(), 'demo');  -- BOOKING_RECORDS_VIEW

-- 3. 更新普通用户角色的权限（确保只有必要的权限）
-- 先清除普通用户角色的所有权限
DELETE FROM sys_role_permission WHERE role_id = 'role_normal';

-- 重新分配普通用户权限
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
-- 教室中心（父菜单）
('rp_normal_001', 'role_normal', 'perm_room_center', NOW(), 'demo'),
-- 教室管理菜单及基础权限
('rp_normal_002', 'role_normal', 'perm022', NOW(), 'demo'),  -- ROOM_MANAGE 教室管理菜单
('rp_normal_003', 'role_normal', 'perm023', NOW(), 'demo'),  -- ROOM_SEARCH 教室查询
('rp_normal_004', 'role_normal', 'perm024', NOW(), 'demo'),  -- ROOM_VIEW 教室查看
('rp_normal_005', 'role_normal', 'perm028', NOW(), 'demo'),  -- ROOM_BOOK 教室预订
-- 预订管理菜单及基础权限
('rp_normal_006', 'role_normal', 'perm031', NOW(), 'demo'),  -- BOOKING_MANAGE 预订管理菜单
('rp_normal_007', 'role_normal', 'perm032', NOW(), 'demo'),  -- BOOKING_SEARCH 预订查询
('rp_normal_008', 'role_normal', 'perm033', NOW(), 'demo'),  -- BOOKING_VIEW 预订查看（数据看板需要）
('rp_normal_009', 'role_normal', 'perm034', NOW(), 'demo'),  -- BOOKING_CREATE 创建预订
('rp_normal_010', 'role_normal', 'perm036', NOW(), 'demo'),  -- BOOKING_CANCEL 取消预订
-- 区域查看权限（房间管理需要）
('rp_normal_011', 'role_normal', 'perm058', NOW(), 'demo');  -- AREA_VIEW 区域查看

-- 注意：普通用户不分配以下权限：
-- BOOKING_APPROVE (审批权限)
-- BOOKING_RECORDS_VIEW (数据记录查看)
-- BOOKING_CONFIG (配置权限)
-- BOOKING_EDIT (编辑权限)
-- BOOKING_REJECT (拒绝权限)

-- 4. 验证权限分配
SELECT '=== 管理员权限 ===' as title;
SELECT p.code, p.name 
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role001' 
AND p.code LIKE 'BOOKING%'
ORDER BY p.code;

SELECT '=== 普通用户权限 ===' as title;
SELECT p.code, p.name 
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
AND (p.code LIKE 'BOOKING%' OR p.code LIKE 'ROOM%' OR p.code LIKE 'AREA%')
ORDER BY p.code;