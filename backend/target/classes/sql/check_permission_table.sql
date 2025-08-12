-- 检查 sys_permission 表结构
DESCRIBE sys_permission;

-- 检查表是否存在新添加的字段
SHOW COLUMNS FROM sys_permission LIKE 'path';
SHOW COLUMNS FROM sys_permission LIKE 'component';
SHOW COLUMNS FROM sys_permission LIKE 'icon';
SHOW COLUMNS FROM sys_permission LIKE 'visible';

-- 检查主要权限数据
SELECT 
    id,
    code, 
    name, 
    type,
    path,
    component,
    icon,
    visible,
    parent_id,
    sort
FROM sys_permission 
WHERE code IN (
    'USER_MANAGE', 
    'ROLE_MANAGE', 
    'ROOM_MANAGE', 
    'BOOKING_MANAGE',
    'ORG_MANAGE',
    'POSITION_MANAGE',
    'TITLE_MANAGE'
)
ORDER BY sort;

-- 检查所有MENU类型的权限
SELECT 
    id,
    code,
    name,
    type,
    path,
    component,
    icon,
    visible,
    parent_id
FROM sys_permission 
WHERE type = 'MENU' 
ORDER BY sort;

-- 检查权限总数
SELECT 
    type,
    COUNT(*) as count
FROM sys_permission 
GROUP BY type;

-- 检查是否有路径数据
SELECT COUNT(*) as has_path FROM sys_permission WHERE path IS NOT NULL;
SELECT COUNT(*) as has_component FROM sys_permission WHERE component IS NOT NULL;