-- =====================================================
-- 更新角色权限为新版权限码
-- =====================================================

-- 1. 首先插入所有权限映射（如果还没有）
INSERT IGNORE INTO sys_permission_alias (id, old_code, new_code, description) VALUES
-- 系统管理模块
(UUID(), 'SYSTEM_MANAGE', 'system:config:manage', '系统管理'),
(UUID(), 'USER_MANAGE', 'system:user:manage', '用户管理'),
(UUID(), 'USER_VIEW', 'system:user:view', '用户查看'),
(UUID(), 'USER_ADD', 'system:user:create', '用户新增'),
(UUID(), 'USER_EDIT', 'system:user:update', '用户编辑'),
(UUID(), 'USER_DELETE', 'system:user:delete', '用户删除'),
(UUID(), 'USER_EXPORT', 'system:user:export', '用户导出'),
(UUID(), 'USER_IMPORT', 'system:user:import', '用户导入'),
(UUID(), 'USER_SEARCH', 'system:user:view', '用户搜索'),
(UUID(), 'USER_RESET_PWD', 'system:user:resetPassword', '重置密码'),
(UUID(), 'USER_ASSIGN_ROLE', 'system:user:assignRoles', '分配角色'),

-- 角色权限模块
(UUID(), 'ROLE_MANAGE', 'system:role:manage', '角色管理'),
(UUID(), 'ROLE_VIEW', 'system:role:view', '角色查看'),
(UUID(), 'ROLE_ADD', 'system:role:create', '角色新增'),
(UUID(), 'ROLE_EDIT', 'system:role:update', '角色编辑'),
(UUID(), 'ROLE_DELETE', 'system:role:delete', '角色删除'),
(UUID(), 'ROLE_ASSIGN', 'system:role:assign', '角色分配权限'),
(UUID(), 'ROLE_SEARCH', 'system:role:view', '角色搜索'),
(UUID(), 'ROLE_ASSIGN_PERM', 'system:role:assign', '分配权限'),

-- 权限管理模块
(UUID(), 'PERMISSION_MANAGE', 'system:permission:manage', '权限管理'),
(UUID(), 'PERMISSION_VIEW', 'system:permission:view', '权限查看'),
(UUID(), 'PERMISSION_ASSIGN', 'system:permission:assign', '权限分配'),
(UUID(), 'PERMISSION_SEARCH', 'system:permission:view', '权限搜索'),

-- 房间管理模块
(UUID(), 'ROOM_MANAGE', 'building:room:manage', '房间管理'),
(UUID(), 'ROOM_VIEW', 'building:room:view', '房间查看'),
(UUID(), 'ROOM_ADD', 'building:room:create', '房间新增'),
(UUID(), 'ROOM_EDIT', 'building:room:update', '房间编辑'),
(UUID(), 'ROOM_DELETE', 'building:room:delete', '房间删除'),
(UUID(), 'ROOM_EXPORT', 'building:room:export', '房间导出'),
(UUID(), 'ROOM_IMPORT', 'building:room:import', '房间导入'),
(UUID(), 'ROOM_SEARCH', 'building:room:view', '房间搜索'),
(UUID(), 'ROOM_BOOK', 'booking:reservation:create', '预约房间'),
(UUID(), 'ROOM_CANCEL_BOOK', 'booking:reservation:cancel', '取消预约'),

-- 预约管理模块
(UUID(), 'BOOKING_MANAGE', 'booking:reservation:manage', '预约管理'),
(UUID(), 'BOOKING_VIEW', 'booking:reservation:view', '预约查看'),
(UUID(), 'BOOKING_ADD', 'booking:reservation:create', '预约新增'),
(UUID(), 'BOOKING_CREATE', 'booking:reservation:create', '创建预约'),
(UUID(), 'BOOKING_EDIT', 'booking:reservation:update', '预约编辑'),
(UUID(), 'BOOKING_DELETE', 'booking:reservation:delete', '预约删除'),
(UUID(), 'BOOKING_CANCEL', 'booking:reservation:cancel', '预约取消'),
(UUID(), 'BOOKING_AUDIT', 'booking:reservation:audit', '预约审核'),
(UUID(), 'BOOKING_EXPORT', 'booking:reservation:export', '预约导出'),
(UUID(), 'BOOKING_SEARCH', 'booking:reservation:view', '预约搜索'),

-- 报表模块
(UUID(), 'REPORT_VIEW', 'booking:report:view', '报表查看'),
(UUID(), 'REPORT_EXPORT', 'booking:report:export', '报表导出');

-- 2. 创建临时表存储新旧权限码对应关系
CREATE TEMPORARY TABLE temp_permission_mapping AS
SELECT old_code, new_code FROM sys_permission_alias;

-- 3. 更新sys_permission表中的权限码
UPDATE sys_permission p
INNER JOIN temp_permission_mapping m ON p.code = m.old_code
SET p.code = m.new_code
WHERE p.code IN (SELECT old_code FROM temp_permission_mapping);

-- 4. 更新角色权限关联表（如果权限码存储在关联表中）
-- 注意：这取决于你的具体表结构

-- 5. 为普通用户角色添加必要的新权限（如果还没有）
-- 获取普通用户角色ID
SET @normal_user_role_id = (SELECT id FROM sys_role WHERE role_code = 'NORMAL_USER' LIMIT 1);

-- 添加基础权限
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time)
SELECT 
    UUID(),
    @normal_user_role_id,
    p.id,
    NOW()
FROM sys_permission p
WHERE p.code IN (
    'booking:reservation:view',
    'booking:reservation:create',
    'booking:reservation:cancel',
    'building:room:view',
    'system:user:view'
);

-- 6. 为管理员角色更新权限
SET @admin_role_id = (SELECT id FROM sys_role WHERE role_code = 'ADMIN' LIMIT 1);

-- 确保管理员拥有所有manage权限
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time)
SELECT 
    UUID(),
    @admin_role_id,
    p.id,
    NOW()
FROM sys_permission p
WHERE p.code LIKE '%:manage';

-- 7. 清理重复的权限关联
DELETE rp1 FROM sys_role_permission rp1
INNER JOIN sys_role_permission rp2 
WHERE rp1.id > rp2.id 
AND rp1.role_id = rp2.role_id 
AND rp1.permission_id = rp2.permission_id;

-- 8. 输出更新结果统计
SELECT 
    'Permission Update Summary' as 'Report',
    COUNT(DISTINCT p.id) as 'Total Permissions',
    COUNT(DISTINCT CASE WHEN p.code LIKE '%:%:%' THEN p.id END) as 'New Format Permissions',
    COUNT(DISTINCT r.id) as 'Total Roles',
    COUNT(DISTINCT rp.id) as 'Total Role Permissions'
FROM sys_permission p
LEFT JOIN sys_role_permission rp ON p.id = rp.permission_id
LEFT JOIN sys_role r ON rp.role_id = r.id;