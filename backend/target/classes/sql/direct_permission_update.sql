-- 直接更新权限代码为新格式

-- 系统管理
UPDATE sys_permission SET code = 'system:system:manage' WHERE code = 'SYSTEM_MANAGE' AND deleted = 0;

-- 用户管理
UPDATE sys_permission SET code = 'system:user:manage' WHERE code = 'USER_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:view' WHERE code IN ('USER_SEARCH', 'USER_VIEW') AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:create' WHERE code IN ('USER_ADD', 'USER_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:update' WHERE code IN ('USER_EDIT', 'USER_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:delete' WHERE code = 'USER_DELETE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:reset-password' WHERE code IN ('USER_RESET_PWD', 'USER_RESET_PASSWORD') AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:assign-roles' WHERE code = 'USER_ASSIGN_ROLE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:export' WHERE code = 'USER_EXPORT' AND deleted = 0;
UPDATE sys_permission SET code = 'system:user:import' WHERE code = 'USER_IMPORT' AND deleted = 0;

-- 角色管理
UPDATE sys_permission SET code = 'system:role:manage' WHERE code = 'ROLE_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:role:view' WHERE code IN ('ROLE_SEARCH', 'ROLE_VIEW') AND deleted = 0;
UPDATE sys_permission SET code = 'system:role:create' WHERE code IN ('ROLE_ADD', 'ROLE_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:role:update' WHERE code IN ('ROLE_EDIT', 'ROLE_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:role:delete' WHERE code = 'ROLE_DELETE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:role:assign-permission' WHERE code IN ('ROLE_ASSIGN_PERM', 'ROLE_ASSIGN_PERMISSION') AND deleted = 0;

-- 权限管理
UPDATE sys_permission SET code = 'system:permission:manage' WHERE code = 'PERMISSION_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:permission:view' WHERE code IN ('PERMISSION_SEARCH', 'PERMISSION_VIEW') AND deleted = 0;
UPDATE sys_permission SET code = 'system:permission:create' WHERE code IN ('PERMISSION_ADD', 'PERMISSION_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:permission:update' WHERE code IN ('PERMISSION_EDIT', 'PERMISSION_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:permission:delete' WHERE code = 'PERMISSION_DELETE' AND deleted = 0;

-- 组织管理
UPDATE sys_permission SET code = 'system:org:manage' WHERE code = 'ORG_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:org:view' WHERE code = 'ORG_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:org:create' WHERE code = 'ORG_ADD' AND deleted = 0;
UPDATE sys_permission SET code = 'system:org:update' WHERE code = 'ORG_EDIT' AND deleted = 0;
UPDATE sys_permission SET code = 'system:org:delete' WHERE code = 'ORG_DELETE' AND deleted = 0;

-- 职位管理
UPDATE sys_permission SET code = 'system:position:manage' WHERE code = 'POSITION_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:position:view' WHERE code = 'POSITION_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:position:create' WHERE code = 'POSITION_ADD' AND deleted = 0;
UPDATE sys_permission SET code = 'system:position:update' WHERE code = 'POSITION_EDIT' AND deleted = 0;
UPDATE sys_permission SET code = 'system:position:delete' WHERE code = 'POSITION_DELETE' AND deleted = 0;

-- 职称管理
UPDATE sys_permission SET code = 'system:title:manage' WHERE code = 'TITLE_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:title:view' WHERE code = 'TITLE_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:title:create' WHERE code = 'TITLE_ADD' AND deleted = 0;
UPDATE sys_permission SET code = 'system:title:update' WHERE code = 'TITLE_EDIT' AND deleted = 0;
UPDATE sys_permission SET code = 'system:title:delete' WHERE code = 'TITLE_DELETE' AND deleted = 0;

-- 房间管理
UPDATE sys_permission SET code = 'building:room:manage' WHERE code = 'ROOM_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:search' WHERE code = 'ROOM_SEARCH' AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:view' WHERE code = 'ROOM_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:create' WHERE code IN ('ROOM_ADD', 'ROOM_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:update' WHERE code IN ('ROOM_EDIT', 'ROOM_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:delete' WHERE code = 'ROOM_DELETE' AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:book' WHERE code = 'ROOM_BOOK' AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:schedule-view' WHERE code = 'ROOM_SCHEDULE_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'building:room:center' WHERE code = 'ROOM_CENTER' AND deleted = 0;

-- 区域管理
UPDATE sys_permission SET code = 'building:area:manage' WHERE code = 'AREA_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'building:area:view' WHERE code = 'AREA_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'building:area:create' WHERE code IN ('AREA_ADD', 'AREA_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'building:area:update' WHERE code IN ('AREA_EDIT', 'AREA_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'building:area:delete' WHERE code = 'AREA_DELETE' AND deleted = 0;

-- 预订管理
UPDATE sys_permission SET code = 'booking:booking:manage' WHERE code = 'BOOKING_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:search' WHERE code = 'BOOKING_SEARCH' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:view' WHERE code = 'BOOKING_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:create' WHERE code IN ('BOOKING_ADD', 'BOOKING_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:update' WHERE code IN ('BOOKING_EDIT', 'BOOKING_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:cancel' WHERE code = 'BOOKING_CANCEL' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:approve' WHERE code = 'BOOKING_APPROVE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:reject' WHERE code = 'BOOKING_REJECT' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:booking:release' WHERE code = 'BOOKING_RELEASE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:config:manage' WHERE code = 'BOOKING_CONFIG' AND deleted = 0;

-- 预订审批
UPDATE sys_permission SET code = 'approval:approval:view' WHERE code IN ('BOOKING_APPROVAL_VIEW', 'APPROVAL_VIEW') AND deleted = 0;
UPDATE sys_permission SET code = 'approval:approval:manage' WHERE code IN ('BOOKING_APPROVAL_MANAGE', 'APPROVAL_MANAGE') AND deleted = 0;
UPDATE sys_permission SET code = 'approval:approval:process' WHERE code IN ('BOOKING_APPROVAL_APPROVE', 'APPROVAL_PROCESS') AND deleted = 0;
UPDATE sys_permission SET code = 'approval:approval:search' WHERE code = 'APPROVAL_SEARCH' AND deleted = 0;
UPDATE sys_permission SET code = 'approval:config:manage' WHERE code = 'APPROVAL_CONFIG_MANAGE' AND deleted = 0;

-- 方案管理
UPDATE sys_permission SET code = 'scheme:scheme:manage' WHERE code = 'SCHEME_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'scheme:scheme:view' WHERE code = 'SCHEME_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'scheme:scheme:create' WHERE code = 'SCHEME_CREATE' AND deleted = 0;
UPDATE sys_permission SET code = 'scheme:scheme:update' WHERE code = 'SCHEME_UPDATE' AND deleted = 0;
UPDATE sys_permission SET code = 'scheme:scheme:delete' WHERE code = 'SCHEME_DELETE' AND deleted = 0;
UPDATE sys_permission SET code = 'scheme:scheme:activate' WHERE code = 'SCHEME_ACTIVATE' AND deleted = 0;

-- 黑名单管理
UPDATE sys_permission SET code = 'system:blacklist:manage' WHERE code = 'BLACKLIST_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:blacklist:view' WHERE code = 'BLACKLIST_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:blacklist:create' WHERE code IN ('BLACKLIST_ADD', 'BLACKLIST_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'system:blacklist:update' WHERE code = 'BLACKLIST_UPDATE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:blacklist:delete' WHERE code = 'BLACKLIST_DELETE' AND deleted = 0;

-- 违约设置
UPDATE sys_permission SET code = 'system:violation:manage' WHERE code = 'VIOLATION_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:violation:view' WHERE code = 'VIOLATION_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:violation:update' WHERE code = 'VIOLATION_UPDATE' AND deleted = 0;

-- 预订规则
UPDATE sys_permission SET code = 'booking:rule:manage' WHERE code = 'BOOKING_RULE_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:rule:view' WHERE code = 'BOOKING_RULE_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:rule:update' WHERE code = 'BOOKING_RULE_UPDATE' AND deleted = 0;

-- 时间规则
UPDATE sys_permission SET code = 'booking:time-rule:view' WHERE code IN ('BOOKING_TIME_RULE_VIEW', 'TIME_RULE_VIEW') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:time-rule:create' WHERE code IN ('BOOKING_TIME_RULE_ADD', 'TIME_RULE_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:time-rule:update' WHERE code IN ('BOOKING_TIME_RULE_EDIT', 'TIME_RULE_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:time-rule:delete' WHERE code IN ('BOOKING_TIME_RULE_DELETE', 'TIME_RULE_DELETE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:time-rule:manage' WHERE code = 'TIME_RULE_MANAGE' AND deleted = 0;

-- 用户预订限制
UPDATE sys_permission SET code = 'booking:user-limit:manage' WHERE code = 'USER_BOOKING_LIMIT_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:user-limit:view' WHERE code = 'USER_BOOKING_LIMIT_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:user-limit:create' WHERE code = 'USER_BOOKING_LIMIT_CREATE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:user-limit:update' WHERE code = 'USER_BOOKING_LIMIT_UPDATE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:user-limit:delete' WHERE code = 'USER_BOOKING_LIMIT_DELETE' AND deleted = 0;

-- 预订人员权限
UPDATE sys_permission SET code = 'booking:personnel:view' WHERE code = 'BOOKING_PERSONNEL_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:personnel:create' WHERE code IN ('BOOKING_PERSONNEL_ADD', 'BOOKING_PERSONNEL_CREATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:personnel:update' WHERE code IN ('BOOKING_PERSONNEL_EDIT', 'BOOKING_PERSONNEL_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:personnel:delete' WHERE code = 'BOOKING_PERSONNEL_DELETE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:personnel:manage' WHERE code IN ('BOOKING_PERSONNEL_MANAGE', 'BOOKING_PERMISSION_MANAGE') AND deleted = 0;

-- 回收站
UPDATE sys_permission SET code = 'system:recycle-bin:manage' WHERE code = 'RECYCLE_BIN_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:recycle-bin:view' WHERE code = 'RECYCLE_BIN_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:recycle-bin:restore' WHERE code = 'RECYCLE_BIN_RESTORE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:recycle-bin:delete' WHERE code = 'RECYCLE_BIN_DELETE' AND deleted = 0;

-- 连续预订设置
UPDATE sys_permission SET code = 'booking:continuous:manage' WHERE code = 'CONTINUOUS_BOOKING_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:continuous:view' WHERE code = 'CONTINUOUS_BOOKING_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'booking:continuous:update' WHERE code IN ('CONTINUOUS_BOOKING_EDIT', 'CONTINUOUS_BOOKING_UPDATE') AND deleted = 0;
UPDATE sys_permission SET code = 'booking:continuous:delete' WHERE code = 'CONTINUOUS_BOOKING_DELETE' AND deleted = 0;

-- 文件管理
UPDATE sys_permission SET code = 'system:file:manage' WHERE code = 'FILE_MANAGE' AND deleted = 0;
UPDATE sys_permission SET code = 'system:file:upload' WHERE code = 'FILE_UPLOAD' AND deleted = 0;
UPDATE sys_permission SET code = 'system:file:view' WHERE code = 'FILE_VIEW' AND deleted = 0;
UPDATE sys_permission SET code = 'system:file:delete' WHERE code = 'FILE_DELETE' AND deleted = 0;

-- 验证更新结果
SELECT COUNT(*) as updated_count FROM sys_permission WHERE code LIKE '%:%' AND deleted = 0;
SELECT COUNT(*) as remaining_old FROM sys_permission WHERE code NOT LIKE '%:%' AND deleted = 0;

-- 查看更新后的权限
SELECT code, name FROM sys_permission WHERE deleted = 0 ORDER BY code LIMIT 20;