-- =====================================================
-- 管理员设置模块权限资源添加脚本
-- 基于管理员设置API接口设计文档创建
-- =====================================================

-- 1. 添加管理员管理权限资源
-- =====================================================

-- 查看管理员权限
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:view',
    '查看管理员',
    'API',
    '/admin-management/admins/search',
    'POST',
    NULL,
    0,
    1,
    'view',
    'demo',
    NOW(),
    NOW()
);

-- 创建管理员权限
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:create',
    '创建管理员',
    'API',
    '/admin-management/admins/add',
    'POST',
    NULL,
    0,
    2,
    'plus',
    'demo',
    NOW(),
    NOW()
);

-- 更新管理员权限（更新角色）
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:update',
    '更新管理员',
    'API',
    '/admin-management/admins/{userId}/roles',
    'PUT',
    NULL,
    0,
    3,
    'edit',
    'demo',
    NOW(),
    NOW()
);

-- 更新管理员权限（更新状态）
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:update',
    '更新管理员状态',
    'API',
    '/admin-management/admins/{userId}/status',
    'PUT',
    NULL,
    0,
    4,
    'switch',
    'demo',
    NOW(),
    NOW()
);

-- 删除管理员权限（单个删除）
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:delete',
    '删除管理员',
    'API',
    '/admin-management/admins/{userId}',
    'DELETE',
    NULL,
    0,
    5,
    'delete',
    'demo',
    NOW(),
    NOW()
);

-- 删除管理员权限（批量删除）
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:delete',
    '批量删除管理员',
    'API',
    '/admin-management/admins/batch',
    'DELETE',
    NULL,
    0,
    6,
    'delete',
    'demo',
    NOW(),
    NOW()
);

-- 管理员管理权限（包含所有操作）
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:manage',
    '管理员管理',
    'MENU',
    '/admin-management',
    NULL,
    NULL,
    0,
    7,
    'user-setting',
    'demo',
    NOW(),
    NOW()
);

-- 2. 角色相关权限（复用或新增）
-- =====================================================

-- 获取可分配角色列表（使用查看权限即可）
INSERT INTO sys_permission (id, permission_code, permission_name, resource_type, resource_url, http_method, parent_id, is_public, sort_order, icon, customer_id, create_time, update_time)
VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:view',
    '获取可分配角色',
    'API',
    '/admin-management/roles/available',
    'GET',
    NULL,
    0,
    8,
    'role',
    'demo',
    NOW(),
    NOW()
);

-- 3. 为超级管理员角色分配管理员管理权限
-- =====================================================

-- 获取超级管理员角色ID
SET @super_admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN' AND customer_id = 'demo' LIMIT 1);

-- 如果超级管理员角色存在，分配所有管理员管理权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    REPLACE(UUID(), '-', ''),
    @super_admin_role_id,
    p.id,
    'demo',
    NOW()
FROM sys_permission p
WHERE p.permission_code IN ('system:admin:view', 'system:admin:create', 'system:admin:update', 'system:admin:delete', 'system:admin:manage')
  AND p.customer_id = 'demo'
  AND @super_admin_role_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp 
      WHERE rp.role_id = @super_admin_role_id 
        AND rp.permission_id = p.id
  );

-- 4. 为系统管理员角色分配管理员管理权限
-- =====================================================

-- 获取系统管理员角色ID
SET @system_admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'SYSTEM_ADMIN' AND customer_id = 'demo' LIMIT 1);

-- 如果系统管理员角色存在，分配管理员管理权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    REPLACE(UUID(), '-', ''),
    @system_admin_role_id,
    p.id,
    'demo',
    NOW()
FROM sys_permission p
WHERE p.permission_code IN ('system:admin:view', 'system:admin:create', 'system:admin:update', 'system:admin:delete', 'system:admin:manage')
  AND p.customer_id = 'demo'
  AND @system_admin_role_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp 
      WHERE rp.role_id = @system_admin_role_id 
        AND rp.permission_id = p.id
  );

-- 5. 为管理员角色分配部分管理员管理权限（只能查看）
-- =====================================================

-- 获取管理员角色ID
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'ADMIN' AND customer_id = 'demo' LIMIT 1);

-- 如果管理员角色存在，只分配查看权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    REPLACE(UUID(), '-', ''),
    @admin_role_id,
    p.id,
    'demo',
    NOW()
FROM sys_permission p
WHERE p.permission_code = 'system:admin:view'
  AND p.customer_id = 'demo'
  AND @admin_role_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp 
      WHERE rp.role_id = @admin_role_id 
        AND rp.permission_id = p.id
  );

-- 6. 创建权限别名映射（用于权限系统升级兼容）
-- =====================================================

-- 插入管理员管理相关的权限别名
INSERT INTO sys_permission_alias (id, old_code, new_code, description, customer_id, create_time)
VALUES 
    (REPLACE(UUID(), '-', ''), 'admin:list', 'system:admin:view', '管理员列表查看权限映射', 'demo', NOW()),
    (REPLACE(UUID(), '-', ''), 'admin:add', 'system:admin:create', '添加管理员权限映射', 'demo', NOW()),
    (REPLACE(UUID(), '-', ''), 'admin:edit', 'system:admin:update', '编辑管理员权限映射', 'demo', NOW()),
    (REPLACE(UUID(), '-', ''), 'admin:delete', 'system:admin:delete', '删除管理员权限映射', 'demo', NOW()),
    (REPLACE(UUID(), '-', ''), 'admin:*', 'system:admin:manage', '管理员管理所有权限映射', 'demo', NOW())
ON DUPLICATE KEY UPDATE 
    new_code = VALUES(new_code),
    description = VALUES(description),
    update_time = NOW();

-- 7. 验证权限添加结果
-- =====================================================

-- 查询新添加的管理员管理权限
SELECT 
    p.permission_code,
    p.permission_name,
    p.resource_type,
    p.resource_url,
    p.http_method,
    COUNT(rp.id) as assigned_roles_count
FROM sys_permission p
LEFT JOIN sys_role_permission rp ON p.id = rp.permission_id
WHERE p.permission_code LIKE 'system:admin:%'
  AND p.customer_id = 'demo'
GROUP BY p.id, p.permission_code, p.permission_name, p.resource_type, p.resource_url, p.http_method
ORDER BY p.sort_order;

-- 查询各角色的管理员管理权限分配情况
SELECT 
    r.role_name,
    r.role_code,
    GROUP_CONCAT(p.permission_code) as admin_permissions
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id AND p.permission_code LIKE 'system:admin:%'
WHERE r.customer_id = 'demo'
  AND r.role_code IN ('SUPER_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')
GROUP BY r.id, r.role_name, r.role_code;

-- 提示信息
SELECT '管理员设置模块权限资源添加完成！' as message;
SELECT '请刷新用户权限缓存或重新登录以使权限生效。' as reminder;