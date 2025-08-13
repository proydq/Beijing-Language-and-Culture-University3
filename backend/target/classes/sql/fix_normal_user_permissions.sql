-- 修复普通用户权限，移除不应该有的权限

-- 1. 首先清除普通用户的所有权限
DELETE FROM sys_role_permission WHERE role_id = 'role_normal';

-- 2. 重新分配普通用户应该有的权限
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
-- 教室中心（父菜单）
('rp_normal_001', 'role_normal', 'perm_room_center', NOW(), 'demo'),
-- 教室管理菜单及基础权限
('rp_normal_002', 'role_normal', 'perm022', NOW(), 'demo'),  -- ROOM_MANAGE 教室管理菜单
('rp_normal_003', 'role_normal', 'perm023', NOW(), 'demo'),  -- ROOM_SEARCH 教室查询
('rp_normal_004', 'role_normal', 'perm024', NOW(), 'demo'),  -- ROOM_VIEW 教室查看
('rp_normal_005', 'role_normal', 'perm028', NOW(), 'demo'),  -- ROOM_BOOK 教室预订
-- 预订管理菜单及基础权限（不包括审批和数据记录）
('rp_normal_006', 'role_normal', 'perm031', NOW(), 'demo'),  -- BOOKING_MANAGE 预订管理菜单
('rp_normal_007', 'role_normal', 'perm032', NOW(), 'demo'),  -- BOOKING_SEARCH 预订查询
('rp_normal_008', 'role_normal', 'perm033', NOW(), 'demo'),  -- BOOKING_VIEW 预订查看（数据看板需要）
('rp_normal_009', 'role_normal', 'perm034', NOW(), 'demo'),  -- BOOKING_CREATE 创建预订
('rp_normal_010', 'role_normal', 'perm036', NOW(), 'demo'),  -- BOOKING_CANCEL 取消预订
-- 必要的API权限
('rp_normal_011', 'role_normal', 'perm058', NOW(), 'demo'),  -- AREA_VIEW 区域查看
('rp_normal_012', 'role_normal', 'perm106', NOW(), 'demo'),  -- ROOM_SCHEDULE_VIEW 教室日程查看
('rp_normal_013', 'role_normal', 'perm053', NOW(), 'demo');  -- SCHEME_VIEW 方案查看

-- 3. 验证权限分配
SELECT '=== 普通用户权限列表 ===' as title;
SELECT p.code, p.name, p.type
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
ORDER BY p.type, p.code;

-- 4. 确认没有审批和数据记录权限
SELECT '=== 确认没有以下权限 ===' as title;
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✓ 成功：普通用户没有审批和数据记录权限'
        ELSE CONCAT('✗ 错误：普通用户仍有 ', COUNT(*), ' 个不应该有的权限')
    END as result
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
AND p.code IN ('BOOKING_APPROVE', 'BOOKING_RECORDS_VIEW', 'BOOKING_REJECT', 'BOOKING_EDIT', 
               'ROOM_ADD', 'ROOM_DELETE', 'ROOM_EDIT', 'ROOM_APPROVE', 'ROOM_CANCEL_BOOK');