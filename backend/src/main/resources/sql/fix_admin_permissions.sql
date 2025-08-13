-- =====================================================
-- 修复管理员管理权限问题
-- =====================================================

-- 1. 先检查权限是否存在
SELECT 'Checking existing permissions...' as status;
SELECT code, name, type, url FROM sys_permission WHERE code LIKE 'system:admin:%';

-- 2. 插入管理员管理相关权限（如果不存在）
-- 查看管理员列表权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) 
SELECT 
    REPLACE(UUID(), '-', ''),
    '查看管理员列表',
    'system:admin:view',
    'BUTTON',
    NULL,
    '/admin-management/admins/search',
    1,
    NULL,
    NULL,
    'view',
    1,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE code = 'system:admin:view' AND customer_id = 'demo'
);

-- 获取可分配角色权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) 
SELECT 
    REPLACE(UUID(), '-', ''),
    '获取可分配角色',
    'system:admin:role:list',
    'BUTTON',
    NULL,
    '/admin-management/roles/available',
    6,
    NULL,
    NULL,
    'role',
    0,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE code = 'system:admin:role:list' AND customer_id = 'demo'
);

-- 3. 为超级管理员角色分配所有管理员管理权限
-- 先获取超级管理员角色ID
SET @super_admin_role_id = (SELECT id FROM sys_role WHERE code = 'SUPER_ADMIN' AND customer_id = 'demo' LIMIT 1);

-- 分配权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id)
SELECT 
    REPLACE(UUID(), '-', ''),
    @super_admin_role_id,
    p.id,
    'demo'
FROM sys_permission p
WHERE p.code IN (
    'system:admin:view',
    'system:admin:create',
    'system:admin:update',
    'system:admin:delete',
    'system:admin:manage',
    'system:admin:role:list',
    'system:admin:status:update',
    'system:admin:batch:delete'
)
AND p.customer_id = 'demo'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp 
    WHERE rp.role_id = @super_admin_role_id 
    AND rp.permission_id = p.id
);

-- 4. 为系统管理员角色分配管理员管理权限
SET @system_admin_role_id = (SELECT id FROM sys_role WHERE code = 'SYSTEM_ADMIN' AND customer_id = 'demo' LIMIT 1);

INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id)
SELECT 
    REPLACE(UUID(), '-', ''),
    @system_admin_role_id,
    p.id,
    'demo'
FROM sys_permission p
WHERE p.code IN (
    'system:admin:view',
    'system:admin:create',
    'system:admin:update',
    'system:admin:delete',
    'system:admin:manage',
    'system:admin:role:list',
    'system:admin:status:update',
    'system:admin:batch:delete'
)
AND p.customer_id = 'demo'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp 
    WHERE rp.role_id = @system_admin_role_id 
    AND rp.permission_id = p.id
);

-- 5. 验证权限分配结果
SELECT 'Super Admin permissions:' as role_check;
SELECT 
    r.name as role_name,
    COUNT(DISTINCT p.id) as permission_count,
    GROUP_CONCAT(p.code ORDER BY p.code SEPARATOR ', ') as permissions
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.code = 'SUPER_ADMIN'
  AND r.customer_id = 'demo'
  AND p.code LIKE 'system:admin:%'
GROUP BY r.id, r.name;

SELECT 'System Admin permissions:' as role_check;
SELECT 
    r.name as role_name,
    COUNT(DISTINCT p.id) as permission_count,
    GROUP_CONCAT(p.code ORDER BY p.code SEPARATOR ', ') as permissions
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.code = 'SYSTEM_ADMIN'
  AND r.customer_id = 'demo'
  AND p.code LIKE 'system:admin:%'
GROUP BY r.id, r.name;

-- 6. 检查当前登录用户的角色和权限
-- 请将 'admin' 替换为您的实际登录用户名
SELECT 'Current user permissions:' as user_check;
SELECT 
    u.username,
    u.real_name,
    r.name as role_name,
    r.code as role_code,
    COUNT(DISTINCT p.id) as admin_permission_count
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id AND p.code LIKE 'system:admin:%'
WHERE u.username = 'admin'  -- 请修改为您的实际用户名
  AND u.customer_id = 'demo'
GROUP BY u.id, u.username, u.real_name, r.id, r.name, r.code;

-- 7. 创建权限别名映射（如果使用了权限别名系统）
-- 检查是否有权限别名表
SELECT 'Checking permission alias...' as alias_check;
SELECT COUNT(*) as alias_table_exists 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name = 'sys_permission_alias';