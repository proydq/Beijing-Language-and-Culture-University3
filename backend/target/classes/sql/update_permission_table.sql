-- 扩展 sys_permission 表，添加前端所需字段
ALTER TABLE sys_permission ADD COLUMN path VARCHAR(200) COMMENT '前端路由路径' AFTER url;
ALTER TABLE sys_permission ADD COLUMN component VARCHAR(200) COMMENT '前端组件名称' AFTER path;
ALTER TABLE sys_permission ADD COLUMN icon VARCHAR(50) COMMENT '菜单图标' AFTER component;
ALTER TABLE sys_permission ADD COLUMN visible TINYINT DEFAULT 1 COMMENT '是否在菜单中显示' AFTER icon;
ALTER TABLE sys_permission ADD COLUMN keep_alive TINYINT DEFAULT 0 COMMENT '是否缓存页面' AFTER visible;
ALTER TABLE sys_permission ADD COLUMN redirect VARCHAR(200) COMMENT '重定向路径' AFTER keep_alive;

-- 更新现有权限数据，添加前端路由和图标信息
-- 用户管理
UPDATE sys_permission SET 
    path = '/user-management',
    component = 'UserManagement',
    icon = 'user',
    visible = 1
WHERE code = 'USER_MANAGE';

-- 角色管理
UPDATE sys_permission SET 
    path = '/role-management',
    component = 'RoleManagement',
    icon = 'user-filled',
    visible = 1
WHERE code = 'ROLE_MANAGE';

-- 权限管理（组件不存在，暂时使用RoleManagement）
UPDATE sys_permission SET 
    path = '/role-management',
    component = 'RoleManagement',
    icon = 'lock',
    visible = 1
WHERE code = 'PERMISSION_MANAGE';

-- 教室管理
UPDATE sys_permission SET 
    path = '/house-management',
    component = 'HouseManagement',
    icon = 'office-building',
    visible = 1
WHERE code = 'ROOM_MANAGE';

-- 预订管理
UPDATE sys_permission SET 
    path = '/room-booking',
    component = 'RoomBooking',
    icon = 'calendar',
    visible = 1
WHERE code = 'BOOKING_MANAGE';

-- 组织机构管理
UPDATE sys_permission SET 
    path = '/organization-management',
    component = 'OrganizationManagement',
    icon = 'office-building',
    visible = 1
WHERE code = 'ORG_MANAGE';

-- 职位管理
UPDATE sys_permission SET 
    path = '/position-management',
    component = 'PositionManagement',
    icon = 'postcard',
    visible = 1
WHERE code = 'POSITION_MANAGE';

-- 职称管理
UPDATE sys_permission SET 
    path = '/level-management',
    component = 'LevelManagement',
    icon = 'medal',
    visible = 1
WHERE code = 'TITLE_MANAGE';

-- 系统管理（虚拟父菜单）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, path, icon, visible, sort, customer_id, deleted, create_time) VALUES
('perm_system', 'SYSTEM', '系统管理', 'MENU', NULL, NULL, 'setting', 1, 100, 'demo', 0, NOW());

-- 更新系统管理相关菜单的父级
UPDATE sys_permission SET parent_id = 'perm_system' WHERE code IN ('ROLE_MANAGE');

-- 用户中心（虚拟父菜单）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, path, icon, visible, sort, customer_id, deleted, create_time) VALUES
('perm_user_center', 'USER_CENTER', '用户中心', 'MENU', NULL, NULL, 'user', 1, 10, 'demo', 0, NOW());

-- 更新用户相关菜单的父级
UPDATE sys_permission SET parent_id = 'perm_user_center' WHERE code IN ('USER_MANAGE', 'ORG_MANAGE', 'POSITION_MANAGE', 'TITLE_MANAGE');

-- 教室中心（虚拟父菜单）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, path, icon, visible, sort, customer_id, deleted, create_time) VALUES
('perm_room_center', 'ROOM_CENTER', '教室中心', 'MENU', NULL, NULL, 'office-building', 1, 20, 'demo', 0, NOW());

-- 更新教室相关菜单的父级
UPDATE sys_permission SET parent_id = 'perm_room_center' WHERE code IN ('ROOM_MANAGE', 'BOOKING_MANAGE');

-- 首页权限（所有人都有）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, path, component, icon, visible, sort, customer_id, deleted, create_time) VALUES
('perm_dashboard', 'DASHBOARD', '首页', 'MENU', NULL, '/dashboard', 'Dashboard', 'home-filled', 1, 1, 'demo', 0, NOW());

-- 个人中心（所有人都有）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, path, component, icon, visible, sort, customer_id, deleted, create_time) VALUES
('perm_personal', 'PERSONAL_CENTER', '个人中心', 'MENU', NULL, '/personal-center', 'PersonalCenter', 'user', 1, 200, 'demo', 0, NOW());

-- 将按钮权限设置为不在菜单显示
UPDATE sys_permission SET visible = 0 WHERE type = 'BUTTON';

-- 为所有角色分配首页和个人中心权限
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    CONCAT('rp_dashboard_', role_id) as id,
    role_id,
    'perm_dashboard',
    customer_id,
    NOW()
FROM sys_role WHERE deleted = 0;

INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    CONCAT('rp_personal_', role_id) as id,
    role_id,
    'perm_personal',
    customer_id,
    NOW()
FROM sys_role WHERE deleted = 0;