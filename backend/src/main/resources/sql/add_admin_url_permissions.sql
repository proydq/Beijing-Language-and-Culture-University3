-- =====================================================
-- 添加管理员管理URL权限映射
-- 针对新的权限系统，确保URL能正确映射到权限
-- =====================================================

-- 1. 检查当前权限系统使用的字段
SHOW COLUMNS FROM sys_permission;

-- 2. 根据您的权限系统结构，添加URL权限映射
-- 如果使用的是 permission_code 和 resource_url 字段
UPDATE sys_permission 
SET resource_url = '/admin-management/admins/search',
    http_method = 'POST'
WHERE permission_code = 'system:admin:view' 
AND customer_id = 'demo';

UPDATE sys_permission 
SET resource_url = '/admin-management/roles/available',
    http_method = 'GET'
WHERE permission_code = 'system:admin:role:list' 
AND customer_id = 'demo';

-- 如果使用的是 code 和 url 字段（基于您之前的SQL）
UPDATE sys_permission 
SET url = '/admin-management/admins/search'
WHERE code = 'system:admin:view' 
AND customer_id = 'demo';

UPDATE sys_permission 
SET url = '/admin-management/roles/available'
WHERE code = 'system:admin:role:list' 
AND customer_id = 'demo';

-- 3. 添加通配符权限（如果需要）
-- 添加管理员管理的所有接口权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    customer_id, create_time, update_time, deleted
) 
SELECT 
    REPLACE(UUID(), '-', ''),
    '管理员管理所有接口',
    'system:admin:*',
    'BUTTON',
    NULL,
    '/admin-management/**',
    99,
    'demo',
    NOW(),
    NOW(),
    0
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE code = 'system:admin:*' AND customer_id = 'demo'
);

-- 4. 为超级管理员添加通配符权限
SET @super_admin_role_id = (SELECT id FROM sys_role WHERE code = 'SUPER_ADMIN' AND customer_id = 'demo' LIMIT 1);
SET @admin_all_permission_id = (SELECT id FROM sys_permission WHERE code = 'system:admin:*' AND customer_id = 'demo' LIMIT 1);

INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id)
SELECT 
    REPLACE(UUID(), '-', ''),
    @super_admin_role_id,
    @admin_all_permission_id,
    'demo'
WHERE @super_admin_role_id IS NOT NULL 
AND @admin_all_permission_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission 
    WHERE role_id = @super_admin_role_id 
    AND permission_id = @admin_all_permission_id
);

-- 5. 检查权限URL映射
SELECT 'Permission URL Mapping:' as check_type;
SELECT 
    code as permission_code,
    name as permission_name,
    url as mapped_url,
    type
FROM sys_permission
WHERE code LIKE 'system:admin:%'
AND customer_id = 'demo'
ORDER BY sort;

-- 6. 检查用户的具体权限URL
SELECT 'User Permission URLs:' as check_type;
SELECT DISTINCT
    u.username,
    p.code as permission_code,
    p.url as permission_url
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE u.username = 'admin'  -- 修改为您的用户名
AND (p.code LIKE 'system:admin:%' OR p.url LIKE '/admin-management%')
AND u.customer_id = 'demo'
ORDER BY p.code;