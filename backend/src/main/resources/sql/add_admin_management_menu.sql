-- =====================================================
-- 管理员设置模块菜单和权限完整配置脚本
-- =====================================================

-- 1. 创建管理员管理菜单权限
-- =====================================================

-- 先查找权限管理的父菜单ID
SET @parent_menu_id = (
    SELECT id FROM sys_permission 
    WHERE permission_code = 'system:permission:menu' 
       OR permission_name = '权限管理'
       OR (resource_type = 'MENU' AND permission_name LIKE '%权限%管理%')
    LIMIT 1
);

-- 如果没有找到权限管理菜单，则查找系统管理菜单
IF @parent_menu_id IS NULL THEN
    SET @parent_menu_id = (
        SELECT id FROM sys_permission 
        WHERE permission_code = 'system:system:menu' 
           OR permission_name = '系统管理'
           OR (resource_type = 'MENU' AND permission_name LIKE '%系统%管理%')
        LIMIT 1
    );
END IF;

-- 创建管理员管理菜单权限
INSERT INTO sys_permission (
    id, 
    permission_code, 
    permission_name, 
    resource_type, 
    resource_url, 
    http_method, 
    parent_id, 
    is_public, 
    sort_order, 
    icon, 
    visible,
    component,
    customer_id, 
    create_time, 
    update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:menu',
    '管理员管理',
    'MENU',
    '/admin-management',
    NULL,
    @parent_menu_id,
    0,
    50,  -- 排序在角色管理之后
    'user-setting',
    1,
    'AdminManagement',
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    resource_url = VALUES(resource_url),
    parent_id = VALUES(parent_id),
    sort_order = VALUES(sort_order),
    icon = VALUES(icon),
    visible = VALUES(visible),
    component = VALUES(component),
    update_time = NOW();

-- 获取刚创建的菜单ID
SET @admin_menu_id = (
    SELECT id FROM sys_permission 
    WHERE permission_code = 'system:admin:menu' 
      AND customer_id = 'demo'
    LIMIT 1
);

-- 2. 创建管理员管理的按钮权限（作为菜单的子权限）
-- =====================================================

-- 查看按钮
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, icon, visible, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:view',
    '查看',
    'BUTTON',
    '/admin-management/admins/search',
    'POST',
    @admin_menu_id,
    0,
    1,
    'view',
    1,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    parent_id = @admin_menu_id,
    update_time = NOW();

-- 新增按钮
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, icon, visible, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:create',
    '新增',
    'BUTTON',
    '/admin-management/admins/add',
    'POST',
    @admin_menu_id,
    0,
    2,
    'plus',
    1,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    parent_id = @admin_menu_id,
    update_time = NOW();

-- 编辑按钮
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, icon, visible, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:update',
    '编辑',
    'BUTTON',
    '/admin-management/admins/*/roles',
    'PUT',
    @admin_menu_id,
    0,
    3,
    'edit',
    1,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    parent_id = @admin_menu_id,
    update_time = NOW();

-- 删除按钮
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, icon, visible, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:delete',
    '删除',
    'BUTTON',
    '/admin-management/admins/*',
    'DELETE',
    @admin_menu_id,
    0,
    4,
    'delete',
    1,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    parent_id = @admin_menu_id,
    update_time = NOW();

-- 3. 创建API级别的细粒度权限
-- =====================================================

-- 获取可分配角色API权限
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:role:list',
    '获取可分配角色列表',
    'API',
    '/admin-management/roles/available',
    'GET',
    NULL,
    0,
    101,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    update_time = NOW();

-- 更新管理员状态API权限
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:status:update',
    '更新管理员状态',
    'API',
    '/admin-management/admins/*/status',
    'PUT',
    NULL,
    0,
    102,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    update_time = NOW();

-- 批量删除API权限
INSERT INTO sys_permission (
    id, permission_code, permission_name, resource_type, 
    resource_url, http_method, parent_id, is_public, 
    sort_order, customer_id, create_time, update_time
) VALUES (
    REPLACE(UUID(), '-', ''),
    'system:admin:batch:delete',
    '批量删除管理员',
    'API',
    '/admin-management/admins/batch',
    'DELETE',
    NULL,
    0,
    103,
    'demo',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    update_time = NOW();

-- 4. 为角色分配权限
-- =====================================================

-- 超级管理员获得所有权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    REPLACE(UUID(), '-', ''),
    r.id,
    p.id,
    'demo',
    NOW()
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'SUPER_ADMIN'
  AND r.customer_id = 'demo'
  AND p.permission_code LIKE 'system:admin:%'
  AND p.customer_id = 'demo'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp 
      WHERE rp.role_id = r.id 
        AND rp.permission_id = p.id
  );

-- 系统管理员获得管理员管理权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    REPLACE(UUID(), '-', ''),
    r.id,
    p.id,
    'demo',
    NOW()
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'SYSTEM_ADMIN'
  AND r.customer_id = 'demo'
  AND p.permission_code LIKE 'system:admin:%'
  AND p.customer_id = 'demo'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp 
      WHERE rp.role_id = r.id 
        AND rp.permission_id = p.id
  );

-- 普通管理员只能查看
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    REPLACE(UUID(), '-', ''),
    r.id,
    p.id,
    'demo',
    NOW()
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ADMIN'
  AND r.customer_id = 'demo'
  AND p.permission_code IN ('system:admin:menu', 'system:admin:view', 'system:admin:role:list')
  AND p.customer_id = 'demo'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp 
      WHERE rp.role_id = r.id 
        AND rp.permission_id = p.id
  );

-- 5. 验证结果
-- =====================================================

-- 显示管理员管理菜单结构
SELECT 
    p.permission_code,
    p.permission_name,
    p.resource_type,
    p.resource_url,
    p.sort_order,
    pp.permission_name as parent_name
FROM sys_permission p
LEFT JOIN sys_permission pp ON p.parent_id = pp.id
WHERE p.permission_code LIKE 'system:admin:%'
  AND p.customer_id = 'demo'
ORDER BY p.parent_id, p.sort_order;

-- 显示各角色的管理员管理权限
SELECT 
    r.role_name,
    r.role_code,
    COUNT(rp.id) as admin_permission_count,
    GROUP_CONCAT(p.permission_name ORDER BY p.sort_order) as permissions
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id AND p.permission_code LIKE 'system:admin:%'
WHERE r.customer_id = 'demo'
  AND r.status = 'ACTIVE'
GROUP BY r.id, r.role_name, r.role_code
HAVING admin_permission_count > 0
ORDER BY admin_permission_count DESC;

-- 完成提示
SELECT '✅ 管理员管理模块菜单和权限配置完成！' as status;
SELECT '📌 请刷新页面或重新登录以查看新菜单。' as action;