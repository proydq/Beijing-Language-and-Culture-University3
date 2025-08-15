-- 添加预约时间控制相关权限
-- 这些权限用于新的预约时间配置和验证接口

-- 1. 首先删除可能存在的重复权限
DELETE FROM sys_permission WHERE code IN (
    'booking:time-config:view',
    'booking:time-config:validate',
    'booking:time-info:view'
) AND deleted = 0;

-- 2. 添加预约时间控制权限
INSERT INTO sys_permission (
    id, code, name, type, parent_id, url, sort, 
    customer_id, deleted, create_time, update_time
) VALUES 
-- 预约时间配置查看权限
(
    'perm_booking_time_config_view', 
    'booking:time-config:view', 
    '查看预约时间配置', 
    'BUTTON', 
    'perm031', -- BOOKING_MANAGE 作为父权限
    '/api/room-booking/booking-time-config', 
    100, 
    'demo', 
    0, 
    NOW(), 
    NOW()
),

-- 预约时间验证权限
(
    'perm_booking_time_validate', 
    'booking:time-config:validate', 
    '验证预约时间', 
    'BUTTON', 
    'perm031', -- BOOKING_MANAGE 作为父权限
    '/api/room-booking/validate-booking-time', 
    101, 
    'demo', 
    0, 
    NOW(), 
    NOW()
),

-- 当前时间信息查看权限
(
    'perm_booking_time_info', 
    'booking:time-info:view', 
    '查看当前时间信息', 
    'BUTTON', 
    'perm031', -- BOOKING_MANAGE 作为父权限
    '/api/room-booking/current-time-info', 
    102, 
    'demo', 
    0, 
    NOW(), 
    NOW()
);

-- 3. 为管理员角色分配这些权限
-- 假设管理员角色ID为 'role001'，如果不同请修改
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
('rp_admin_time_config_view', 'role001', 'perm_booking_time_config_view', NOW(), 'demo'),
('rp_admin_time_validate', 'role001', 'perm_booking_time_validate', NOW(), 'demo'),
('rp_admin_time_info', 'role001', 'perm_booking_time_info', NOW(), 'demo');

-- 4. 为普通用户角色也分配这些权限（因为这些是查看和验证权限，用户预约时需要）
-- 假设普通用户角色ID为 'role002'，如果不同请修改
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
('rp_user_time_config_view', 'role002', 'perm_booking_time_config_view', NOW(), 'demo'),
('rp_user_time_validate', 'role002', 'perm_booking_time_validate', NOW(), 'demo'),
('rp_user_time_info', 'role002', 'perm_booking_time_info', NOW(), 'demo');

-- 5. 验证权限是否添加成功
SELECT 
    p.id,
    p.code,
    p.name,
    p.type,
    p.url,
    p.create_time
FROM sys_permission p 
WHERE p.code IN (
    'booking:time-config:view',
    'booking:time-config:validate', 
    'booking:time-info:view'
) AND p.deleted = 0
ORDER BY p.sort;

-- 6. 验证角色权限关联
SELECT 
    r.name as role_name,
    p.code as permission_code,
    p.name as permission_name
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE p.code IN (
    'booking:time-config:view',
    'booking:time-config:validate',
    'booking:time-info:view'
) AND r.deleted = 0 AND p.deleted = 0
ORDER BY r.name, p.code;