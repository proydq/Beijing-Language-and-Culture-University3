-- 根据实际表结构重新配置角色权限
-- 清理现有数据（已手动清空）

-- 查看当前角色和权限情况
SELECT '=== 当前角色列表 ===' as info;
SELECT id, name, code, remark FROM sys_role WHERE deleted = 0;

SELECT '=== 当前权限总数 ===' as info;  
SELECT COUNT(*) as total_permissions FROM sys_permission WHERE deleted = 0;

-- 1. 管理员角色 - 分配所有权限
-- 假设管理员角色的code是'role001'或者name包含'管理员'
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 
    (SELECT id FROM sys_role WHERE code = 'role001' AND deleted = 0 LIMIT 1),
    id as permission_id
FROM sys_permission 
WHERE deleted = 0;

-- 如果上面没有匹配到，尝试通过name查找管理员角色
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 
    (SELECT id FROM sys_role WHERE name LIKE '%管理员%' AND deleted = 0 LIMIT 1),
    id as permission_id
FROM sys_permission 
WHERE deleted = 0
AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission 
    WHERE role_id = (SELECT id FROM sys_role WHERE name LIKE '%管理员%' AND deleted = 0 LIMIT 1)
);

-- 2. 创建区域查看员角色（如果不存在）
INSERT IGNORE INTO sys_role (id, name, code, remark, customer_id, create_time, update_time, deleted) 
VALUES (REPLACE(UUID(),'-',''), '区域查看员', 'AREA_VIEWER', '只能查看区域信息', 'customer001', NOW(), NOW(), 0);

-- 给区域查看员分配区域查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'AREA_VIEWER' AND deleted = 0 LIMIT 1),
    (SELECT id FROM sys_permission WHERE permission_code = 'building:area:view' AND deleted = 0 LIMIT 1)
WHERE EXISTS (SELECT 1 FROM sys_role WHERE code = 'AREA_VIEWER' AND deleted = 0)
AND EXISTS (SELECT 1 FROM sys_permission WHERE permission_code = 'building:area:view' AND deleted = 0);

-- 3. 创建测试用户（密码：123456）
INSERT IGNORE INTO sys_user (id, username, real_name, password, customer_id, create_time, update_time, deleted)
VALUES (REPLACE(UUID(),'-',''), 'testuser', '测试用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdYMvFOk2rqIIHJpeSaggE1L8Ri', 'customer001', NOW(), NOW(), 0);

-- 给测试用户分配区域查看员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT 
    (SELECT id FROM sys_user WHERE username = 'testuser' AND deleted = 0 LIMIT 1),
    (SELECT id FROM sys_role WHERE code = 'AREA_VIEWER' AND deleted = 0 LIMIT 1)
WHERE EXISTS (SELECT 1 FROM sys_user WHERE username = 'testuser' AND deleted = 0)
AND EXISTS (SELECT 1 FROM sys_role WHERE code = 'AREA_VIEWER' AND deleted = 0);

-- 查看配置结果
SELECT '=== 配置完成后的角色权限统计 ===' as info;
SELECT 
    r.name as role_name,
    r.code as role_code,
    COUNT(rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
WHERE r.deleted = 0
GROUP BY r.id, r.name, r.code
ORDER BY permission_count DESC;