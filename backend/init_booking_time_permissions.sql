-- 简化版权限初始化脚本
-- 可以通过数据库客户端手动执行

USE bunny_bs_v4.3.0_cumt6;

-- 1. 添加预约时间控制权限到sys_permission表
INSERT IGNORE INTO sys_permission (
    id, code, name, type, parent_id, url, sort, 
    customer_id, deleted, create_time, update_time
) VALUES 
('perm_booking_time_config_view', 'booking:time-config:view', '查看预约时间配置', 'BUTTON', 'perm031', '/api/room-booking/booking-time-config', 100, 'demo', 0, NOW(), NOW()),
('perm_booking_time_validate', 'booking:time-config:validate', '验证预约时间', 'BUTTON', 'perm031', '/api/room-booking/validate-booking-time', 101, 'demo', 0, NOW(), NOW()),
('perm_booking_time_info', 'booking:time-info:view', '查看当前时间信息', 'BUTTON', 'perm031', '/api/room-booking/current-time-info', 102, 'demo', 0, NOW(), NOW());

-- 2. 查询现有角色
SELECT id, name FROM sys_role WHERE deleted = 0;

-- 3. 为管理员角色分配权限（请根据实际的角色ID调整）
-- 假设有一个管理员角色，ID需要根据上面的查询结果调整
-- INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
-- VALUES 
-- ('rp_admin_time_config_view', '实际的管理员角色ID', 'perm_booking_time_config_view', NOW(), 'demo'),
-- ('rp_admin_time_validate', '实际的管理员角色ID', 'perm_booking_time_validate', NOW(), 'demo'),
-- ('rp_admin_time_info', '实际的管理员角色ID', 'perm_booking_time_info', NOW(), 'demo');

-- 4. 验证权限是否添加成功
SELECT 
    id, code, name, type, url, create_time
FROM sys_permission 
WHERE code IN ('booking:time-config:view', 'booking:time-config:validate', 'booking:time-info:view') 
AND deleted = 0
ORDER BY sort;