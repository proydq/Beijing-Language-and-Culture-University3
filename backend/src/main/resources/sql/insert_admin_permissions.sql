-- =====================================================
-- 管理员设置模块权限插入SQL
-- 基于sys_permission表结构
-- =====================================================

-- 1. 查看管理员列表权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
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
);

-- 2. 添加管理员权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
    REPLACE(UUID(), '-', ''),
    '添加管理员',
    'system:admin:create',
    'BUTTON',
    NULL,
    '/admin-management/admins/add',
    2,
    NULL,
    NULL,
    'plus',
    1,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
);

-- 3. 更新管理员权限（角色/状态）
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
    REPLACE(UUID(), '-', ''),
    '更新管理员',
    'system:admin:update',
    'BUTTON',
    NULL,
    '/admin-management/admins/*/roles',
    3,
    NULL,
    NULL,
    'edit',
    1,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
);

-- 4. 删除管理员权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
    REPLACE(UUID(), '-', ''),
    '删除管理员',
    'system:admin:delete',
    'BUTTON',
    NULL,
    '/admin-management/admins/*',
    4,
    NULL,
    NULL,
    'delete',
    1,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
);

-- 5. 管理员全部管理权限（菜单权限）
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
    REPLACE(UUID(), '-', ''),
    '管理员管理',
    'system:admin:manage',
    'MENU',
    NULL,
    '/admin-management',
    50,
    '/admin-management',
    'AdminManagement',
    'user-setting',
    1,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
);

-- 6. 获取可分配角色权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
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
);

-- 7. 更新管理员状态权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
    REPLACE(UUID(), '-', ''),
    '更新管理员状态',
    'system:admin:status:update',
    'BUTTON',
    NULL,
    '/admin-management/admins/*/status',
    7,
    NULL,
    NULL,
    'switch',
    1,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
);

-- 8. 批量删除管理员权限
INSERT INTO sys_permission (
    id, name, code, type, parent_id, url, sort, 
    path, component, icon, visible, keep_alive, redirect, 
    customer_id, create_time, update_time, deleted
) VALUES (
    REPLACE(UUID(), '-', ''),
    '批量删除管理员',
    'system:admin:batch:delete',
    'BUTTON',
    NULL,
    '/admin-management/admins/batch',
    8,
    NULL,
    NULL,
    'delete',
    0,
    0,
    NULL,
    'demo',
    NOW(),
    NOW(),
    0
);

-- 查询验证插入结果
SELECT code, name, type, url, sort 
FROM sys_permission 
WHERE code LIKE 'system:admin:%' 
ORDER BY sort;