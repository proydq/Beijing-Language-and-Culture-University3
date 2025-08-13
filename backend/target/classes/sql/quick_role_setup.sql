-- 快速角色权限配置脚本
-- 使用前请确保已清空 sys_role_permission 表

-- 查看当前角色和权限情况
SELECT '=== 当前角色列表 ===' as info;
SELECT role_id, role_name, role_code, role_description FROM sys_role WHERE deleted = 0;

SELECT '=== 当前权限总数 ===' as info;  
SELECT COUNT(*) as total_permissions FROM sys_permission WHERE deleted = 0;

-- 1. 管理员角色 - 分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 
    (SELECT role_id FROM sys_role WHERE role_code = 'role001' AND deleted = 0 LIMIT 1),
    permission_id
FROM sys_permission 
WHERE deleted = 0;

-- 2. 创建区域查看员角色（如果不存在）
INSERT IGNORE INTO sys_role (role_id, role_name, role_code, role_description, created_by, created_time, updated_by, updated_time, deleted) 
VALUES (UUID(), '区域查看员', 'AREA_VIEWER', '只能查看区域信息', 'system', NOW(), 'system', NOW(), 0);

-- 给区域查看员分配区域查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT role_id FROM sys_role WHERE role_code = 'AREA_VIEWER' AND deleted = 0 LIMIT 1),
    (SELECT permission_id FROM sys_permission WHERE permission_code = 'building:area:view' AND deleted = 0 LIMIT 1);

-- 3. 创建测试用户（密码：123456）
INSERT IGNORE INTO sys_user (user_id, username, real_name, password, customer_id, created_by, created_time, updated_by, updated_time, deleted)
VALUES (UUID(), 'testuser', '测试用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdYMvFOk2rqIIHJpeSaggE1L8Ri', 'customer001', 'system', NOW(), 'system', NOW(), 0);

-- 给测试用户分配区域查看员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT 
    (SELECT user_id FROM sys_user WHERE username = 'testuser' AND deleted = 0 LIMIT 1),
    (SELECT role_id FROM sys_role WHERE role_code = 'AREA_VIEWER' AND deleted = 0 LIMIT 1);

-- 查看配置结果
SELECT '=== 配置完成后的角色权限统计 ===' as info;
SELECT 
    r.role_name,
    r.role_code,
    COUNT(rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.role_id = rp.role_id
WHERE r.deleted = 0
GROUP BY r.role_id, r.role_name, r.role_code
ORDER BY permission_count DESC;

SELECT '=== 测试用户信息 ===' as info;
SELECT 
    u.username,
    u.real_name,
    r.role_name as role,
    r.role_code,
    COUNT(rp.permission_id) as permissions
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.role_id  
LEFT JOIN sys_role_permission rp ON r.role_id = rp.role_id
WHERE u.username IN ('admin', 'testuser') AND u.deleted = 0
GROUP BY u.user_id, r.role_id
ORDER BY u.username;