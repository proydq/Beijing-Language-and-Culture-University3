-- 为普通用户添加必要的API权限
-- 这些权限是普通用户访问房屋借用功能所必需的

-- 1. 添加教室日程查看权限（用于 /api/room-schedule/weeks）
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
('rp_normal_012', 'role_normal', 'perm106', NOW(), 'demo');  -- ROOM_SCHEDULE_VIEW 教室日程查看

-- 2. 添加方案查看权限（用于 /scheme-management/personnel/list）
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
('rp_normal_013', 'role_normal', 'perm053', NOW(), 'demo');  -- SCHEME_VIEW 方案查看

-- 3. 验证权限分配
SELECT '=== 普通用户新增权限 ===' as title;
SELECT p.code, p.name 
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
AND p.code IN ('ROOM_SCHEDULE_VIEW', 'SCHEME_VIEW', 'AREA_VIEW')
ORDER BY p.code;

-- 4. 查看普通用户所有权限
SELECT '=== 普通用户所有权限 ===' as title;
SELECT p.code, p.name, p.type
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
ORDER BY p.sort, p.code;