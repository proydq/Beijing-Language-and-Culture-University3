-- 重新建立角色权限绑定关系
-- 清理现有数据（已经手动清空了）
-- DELETE FROM sys_role_permission;

-- 1. 超级管理员角色（role001/admin）- 拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 
    r.role_id,
    p.permission_id
FROM sys_role r, sys_permission p
WHERE r.role_code IN ('role001', 'admin', 'SUPER_ADMIN') 
AND p.deleted = 0 AND r.deleted = 0;

-- 2. 普通用户角色（role002/user）- 基础预订权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    r.role_id,
    p.permission_id
FROM sys_role r, sys_permission p
WHERE r.role_code IN ('role002', 'user', 'USER') 
AND r.deleted = 0 AND p.deleted = 0
AND p.permission_code IN (
    -- 房屋预订相关
    'booking:booking:view',
    'booking:booking:create',
    'building:room:book',
    'booking:booking:cancel',
    
    -- 查看房间信息
    'building:room:view',
    'building:area:view',
    
    -- 查看自己的预订记录
    'booking:records:view'
);

-- 3. 审批员角色（approver）- 审批和管理权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    r.role_id,
    p.permission_id
FROM sys_role r, sys_permission p
WHERE r.role_code IN ('approver', 'APPROVER') 
AND r.deleted = 0 AND p.deleted = 0
AND p.permission_code IN (
    -- 包含普通用户的所有权限
    'booking:booking:view',
    'booking:booking:create',
    'building:room:book',
    'booking:booking:cancel',
    'building:room:view',
    'building:area:view',
    'booking:records:view',
    
    -- 审批权限
    'booking:booking:approve',
    'booking:booking:reject',
    'booking:booking:manage',
    'booking:booking:search',
    'booking:booking:update',
    
    -- 房间管理权限（查看和基础管理）
    'building:room:search',
    'building:room:manage',
    
    -- 报表权限
    'booking:report:view'
);

-- 4. 创建"仅区域查看"测试角色
INSERT INTO sys_role (role_id, role_name, role_code, role_description, created_by, created_time, updated_by, updated_time, deleted) 
VALUES (UUID(), '区域查看员', 'AREA_VIEWER', '只能查看区域信息的测试角色', 'system', NOW(), 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- 给区域查看员角色分配权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    r.role_id,
    p.permission_id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'AREA_VIEWER' 
AND r.deleted = 0 AND p.deleted = 0
AND p.permission_code = 'building:area:view';

-- 5. 创建测试用户（如果不存在）
INSERT INTO sys_user (user_id, username, real_name, password, customer_id, created_by, created_time, updated_by, updated_time, deleted)
VALUES (UUID(), 'testuser', '测试用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdYMvFOk2rqIIHJpeSaggE1L8Ri', 'default_customer', 'system', NOW(), 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name);

-- 6. 给测试用户分配区域查看员角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM sys_user u, sys_role r
WHERE u.username = 'testuser' 
AND r.role_code = 'AREA_VIEWER'
AND u.deleted = 0 AND r.deleted = 0
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 查看结果
SELECT '=== 角色权限统计 ===' as info;
SELECT 
    r.role_name,
    r.role_code,
    COUNT(rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.role_id = rp.role_id
WHERE r.deleted = 0
GROUP BY r.role_id, r.role_name, r.role_code
ORDER BY permission_count DESC;

SELECT '=== 用户角色信息 ===' as info;
SELECT 
    u.username,
    u.real_name,
    r.role_name,
    COUNT(rp.permission_id) as permission_count
FROM sys_user u
JOIN sys_user_role ur ON u.user_id = ur.user_id
JOIN sys_role r ON ur.role_id = r.role_id
LEFT JOIN sys_role_permission rp ON r.role_id = rp.role_id
WHERE u.deleted = 0 AND r.deleted = 0
GROUP BY u.user_id, u.username, u.real_name, r.role_id, r.role_name
ORDER BY u.username, permission_count DESC;