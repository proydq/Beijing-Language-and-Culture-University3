-- 最终版本：根据实际表结构配置角色权限

-- 1. 给超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 
    'role001',
    id
FROM sys_permission 
WHERE deleted = 0;

-- 2. 创建区域查看员角色
INSERT IGNORE INTO sys_role (id, name, code, remark, customer_id, create_time, update_time, deleted) 
VALUES ('area_viewer_role', '区域查看员', 'AREA_VIEWER', '只能查看区域信息', 'customer001', NOW(), NOW(), 0);

-- 3. 给区域查看员分配区域查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    'area_viewer_role',
    id
FROM sys_permission 
WHERE code = 'building:area:view' AND deleted = 0;

-- 4. 创建测试用户
INSERT IGNORE INTO sys_user (id, username, real_name, password, customer_id, create_time, update_time, deleted)
VALUES ('test_user_id', 'testuser', '测试用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdYMvFOk2rqIIHJpeSaggE1L8Ri', 'customer001', NOW(), NOW(), 0);

-- 5. 给测试用户分配区域查看员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
VALUES ('test_user_id', 'area_viewer_role');

-- 6. 查看配置结果
SELECT '=== 权限配置完成 ===' as result;

SELECT 
    r.name as role_name,
    r.code as role_code,
    COUNT(rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
WHERE r.deleted = 0
GROUP BY r.id, r.name, r.code
ORDER BY permission_count DESC;

SELECT '=== 测试用户配置 ===' as result;
SELECT 
    u.username,
    u.real_name,
    r.name as role_name,
    COUNT(rp.permission_id) as permission_count
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
WHERE u.username IN ('admin', 'testuser') AND u.deleted = 0
GROUP BY u.id, u.username, u.real_name, r.id, r.name;