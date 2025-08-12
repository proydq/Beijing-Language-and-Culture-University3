-- 创建普通用户只能访问房屋借用模块的权限配置

-- 1. 创建普通用户（如果不存在）
INSERT IGNORE INTO sys_user (id, username, password, real_name, gender, phone, job_number, 
                            department_id, department_name, position_id, position_name, 
                            title_id, title_name, avatar_url, face_image_url, card_number, 
                            attendance_number, status, customer_id, create_time, update_time, deleted) 
VALUES 
('normal_user_001', 'normal_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZOzx8Qb6Nh8Qw5Qz5Qz5Qz5Qz', 
 '普通用户', '男', '13800138002', 'N001', 
 'dept003', '教务部', 'pos003', '普通员工', 
 'title002', '助理', NULL, NULL, 'CARD003', 
 'ATT003', 'NORMAL', 'demo', NOW(), NOW(), 0);

-- 2. 创建普通用户角色（如果不存在）
INSERT IGNORE INTO sys_role (id, name, code, remark, customer_id, create_time, update_time, deleted) 
VALUES 
('role_normal', '普通用户', 'NORMAL_USER', '普通用户角色，只能访问房屋借用功能', 'demo', NOW(), NOW(), 0);

-- 3. 分配角色给普通用户
INSERT IGNORE INTO sys_user_role (id, user_id, role_id, create_time) 
VALUES 
('ur_normal_001', 'normal_user_001', 'role_normal', NOW());

-- 4. 清除普通用户角色的所有权限（如果有的话）
DELETE FROM sys_role_permission WHERE role_id = 'role_normal';

-- 5. 只分配房屋借用相关的权限给普通用户角色
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
('rp_normal_008', 'role_normal', 'perm033', NOW(), 'demo'),  -- BOOKING_VIEW 预订查看
('rp_normal_009', 'role_normal', 'perm034', NOW(), 'demo'),  -- BOOKING_CREATE 创建预订
('rp_normal_010', 'role_normal', 'perm037', NOW(), 'demo'),  -- BOOKING_CANCEL 取消预订
-- 区域查看权限（房间管理需要）
('rp_normal_011', 'role_normal', 'perm058', NOW(), 'demo');  -- AREA_VIEW 区域查看

-- 6. 确保权限树结构正确
-- 添加教室中心虚拟父菜单（如果不存在）
INSERT IGNORE INTO sys_permission 
(id, code, name, type, parent_id, url, path, component, icon, visible, sort, customer_id, deleted, create_time) 
VALUES 
('perm_room_center', 'ROOM_CENTER', '教室中心', 'MENU', NULL, NULL, NULL, NULL, 'office-building', 1, 20, 'demo', 0, NOW());

-- 更新教室管理和预订管理的父级关系
UPDATE sys_permission SET parent_id = 'perm_room_center' WHERE code IN ('ROOM_MANAGE', 'BOOKING_MANAGE');

-- 确保教室管理和预订管理有正确的路径配置
UPDATE sys_permission SET 
    path = '/house-management',
    component = 'HouseManagement',
    icon = 'office-building',
    visible = 1
WHERE code = 'ROOM_MANAGE';

UPDATE sys_permission SET 
    path = '/room-booking',
    component = 'RoomBooking',
    icon = 'calendar',
    visible = 1
WHERE code = 'BOOKING_MANAGE';

-- 查询验证
SELECT '普通用户权限配置完成' as message;
SELECT u.username, r.name as role_name, p.name as permission_name, p.code, p.type
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE u.username = 'normal_user'
ORDER BY p.sort;