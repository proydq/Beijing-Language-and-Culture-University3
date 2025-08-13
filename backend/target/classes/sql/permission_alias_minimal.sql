-- Minimal permission alias mapping for safe upgrade
-- This approach only updates permission codes without changing system structure

-- Step 1: Create permission alias table if not exists
CREATE TABLE IF NOT EXISTS sys_permission_alias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    old_code VARCHAR(100) NOT NULL COMMENT '旧权限代码',
    new_code VARCHAR(100) NOT NULL COMMENT '新权限代码',
    description VARCHAR(255) COMMENT '权限描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_old_code (old_code),
    INDEX idx_new_code (new_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限代码别名映射表';

-- Step 2: Insert all permission mappings
INSERT INTO sys_permission_alias (old_code, new_code, description) VALUES
-- 用户管理
('USER_MANAGE', 'system:user:manage', '用户管理'),
('USER_VIEW', 'system:user:view', '查看用户'),
('USER_CREATE', 'system:user:create', '创建用户'),
('USER_UPDATE', 'system:user:update', '更新用户'),
('USER_DELETE', 'system:user:delete', '删除用户'),
('USER_EXPORT', 'system:user:export', '导出用户'),
('USER_IMPORT', 'system:user:import', '导入用户'),
('USER_RESET_PASSWORD', 'system:user:reset-password', '重置密码'),

-- 角色管理
('ROLE_MANAGE', 'system:role:manage', '角色管理'),
('ROLE_VIEW', 'system:role:view', '查看角色'),
('ROLE_CREATE', 'system:role:create', '创建角色'),
('ROLE_UPDATE', 'system:role:update', '更新角色'),
('ROLE_DELETE', 'system:role:delete', '删除角色'),
('ROLE_ASSIGN_PERMISSION', 'system:role:assign-permission', '分配权限'),

-- 权限管理
('PERMISSION_MANAGE', 'system:permission:manage', '权限管理'),
('PERMISSION_VIEW', 'system:permission:view', '查看权限'),
('PERMISSION_CREATE', 'system:permission:create', '创建权限'),
('PERMISSION_UPDATE', 'system:permission:update', '更新权限'),
('PERMISSION_DELETE', 'system:permission:delete', '删除权限'),

-- 房间管理
('ROOM_MANAGE', 'building:room:manage', '房间管理'),
('ROOM_SEARCH', 'building:room:search', '搜索房间'),
('ROOM_VIEW', 'building:room:view', '查看房间'),
('ROOM_CREATE', 'building:room:create', '创建房间'),
('ROOM_UPDATE', 'building:room:update', '更新房间'),
('ROOM_DELETE', 'building:room:delete', '删除房间'),
('ROOM_BOOK', 'building:room:book', '预订房间'),
('ROOM_SCHEDULE_VIEW', 'building:room:schedule-view', '查看房间日程'),

-- 预订管理
('BOOKING_MANAGE', 'booking:booking:manage', '预订管理'),
('BOOKING_SEARCH', 'booking:booking:search', '搜索预订'),
('BOOKING_VIEW', 'booking:booking:view', '查看预订'),
('BOOKING_CREATE', 'booking:booking:create', '创建预订'),
('BOOKING_UPDATE', 'booking:booking:update', '更新预订'),
('BOOKING_CANCEL', 'booking:booking:cancel', '取消预订'),
('BOOKING_APPROVE', 'booking:booking:approve', '审批预订'),
('BOOKING_REJECT', 'booking:booking:reject', '拒绝预订'),
('BOOKING_RELEASE', 'booking:booking:release', '释放预订'),

-- 审批管理
('APPROVAL_MANAGE', 'approval:approval:manage', '审批管理'),
('APPROVAL_SEARCH', 'approval:approval:search', '搜索审批'),
('APPROVAL_VIEW', 'approval:approval:view', '查看审批'),
('APPROVAL_PROCESS', 'approval:approval:process', '处理审批'),
('APPROVAL_CONFIG_MANAGE', 'approval:config:manage', '审批配置管理'),

-- 区域管理
('AREA_MANAGE', 'building:area:manage', '区域管理'),
('AREA_VIEW', 'building:area:view', '查看区域'),
('AREA_CREATE', 'building:area:create', '创建区域'),
('AREA_UPDATE', 'building:area:update', '更新区域'),
('AREA_DELETE', 'building:area:delete', '删除区域'),

-- 方案管理
('SCHEME_MANAGE', 'scheme:scheme:manage', '方案管理'),
('SCHEME_VIEW', 'scheme:scheme:view', '查看方案'),
('SCHEME_CREATE', 'scheme:scheme:create', '创建方案'),
('SCHEME_UPDATE', 'scheme:scheme:update', '更新方案'),
('SCHEME_DELETE', 'scheme:scheme:delete', '删除方案'),
('SCHEME_ACTIVATE', 'scheme:scheme:activate', '激活方案'),

-- 黑名单管理
('BLACKLIST_MANAGE', 'system:blacklist:manage', '黑名单管理'),
('BLACKLIST_VIEW', 'system:blacklist:view', '查看黑名单'),
('BLACKLIST_CREATE', 'system:blacklist:create', '添加黑名单'),
('BLACKLIST_UPDATE', 'system:blacklist:update', '更新黑名单'),
('BLACKLIST_DELETE', 'system:blacklist:delete', '删除黑名单'),

-- 违约设置
('VIOLATION_MANAGE', 'system:violation:manage', '违约管理'),
('VIOLATION_VIEW', 'system:violation:view', '查看违约设置'),
('VIOLATION_UPDATE', 'system:violation:update', '更新违约设置'),

-- 预订规则
('BOOKING_RULE_MANAGE', 'booking:rule:manage', '预订规则管理'),
('BOOKING_RULE_VIEW', 'booking:rule:view', '查看预订规则'),
('BOOKING_RULE_UPDATE', 'booking:rule:update', '更新预订规则'),

-- 用户预订限制
('USER_BOOKING_LIMIT_MANAGE', 'booking:user-limit:manage', '用户预订限制管理'),
('USER_BOOKING_LIMIT_VIEW', 'booking:user-limit:view', '查看用户预订限制'),
('USER_BOOKING_LIMIT_CREATE', 'booking:user-limit:create', '创建用户预订限制'),
('USER_BOOKING_LIMIT_UPDATE', 'booking:user-limit:update', '更新用户预订限制'),
('USER_BOOKING_LIMIT_DELETE', 'booking:user-limit:delete', '删除用户预订限制'),

-- 预订人员权限
('BOOKING_PERSONNEL_MANAGE', 'booking:personnel:manage', '预订人员权限管理'),
('BOOKING_PERSONNEL_VIEW', 'booking:personnel:view', '查看预订人员权限'),
('BOOKING_PERSONNEL_CREATE', 'booking:personnel:create', '创建预订人员权限'),
('BOOKING_PERSONNEL_UPDATE', 'booking:personnel:update', '更新预订人员权限'),
('BOOKING_PERSONNEL_DELETE', 'booking:personnel:delete', '删除预订人员权限'),

-- 回收站
('RECYCLE_BIN_MANAGE', 'system:recycle-bin:manage', '回收站管理'),
('RECYCLE_BIN_VIEW', 'system:recycle-bin:view', '查看回收站'),
('RECYCLE_BIN_RESTORE', 'system:recycle-bin:restore', '恢复数据'),
('RECYCLE_BIN_DELETE', 'system:recycle-bin:delete', '永久删除'),

-- 时间规则
('TIME_RULE_MANAGE', 'booking:time-rule:manage', '时间规则管理'),
('TIME_RULE_VIEW', 'booking:time-rule:view', '查看时间规则'),
('TIME_RULE_CREATE', 'booking:time-rule:create', '创建时间规则'),
('TIME_RULE_UPDATE', 'booking:time-rule:update', '更新时间规则'),
('TIME_RULE_DELETE', 'booking:time-rule:delete', '删除时间规则'),

-- 连续预订设置
('CONTINUOUS_BOOKING_MANAGE', 'booking:continuous:manage', '连续预订管理'),
('CONTINUOUS_BOOKING_VIEW', 'booking:continuous:view', '查看连续预订设置'),
('CONTINUOUS_BOOKING_UPDATE', 'booking:continuous:update', '更新连续预订设置')
ON DUPLICATE KEY UPDATE 
    new_code = VALUES(new_code),
    description = VALUES(description);

-- Step 3: Update sys_permission table with new codes
UPDATE sys_permission p
JOIN sys_permission_alias a ON p.code = a.old_code
SET p.code = a.new_code
WHERE p.deleted = 0;

-- Step 4: Verify the update
SELECT COUNT(*) as updated_count FROM sys_permission WHERE code LIKE '%:%';
SELECT '权限代码升级完成' as message;