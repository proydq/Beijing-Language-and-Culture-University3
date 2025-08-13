-- =====================================================
-- 权限系统升级脚本 - Step 2: 权限码映射数据
-- =====================================================

-- 1. 插入新旧权限码映射关系
INSERT INTO sys_permission_alias (old_code, new_code, description) VALUES
-- 系统管理模块
('SYSTEM_MANAGE', 'system:config:manage', '系统管理'),
('USER_MANAGE', 'system:user:manage', '用户管理'),
('USER_VIEW', 'system:user:view', '用户查看'),
('USER_ADD', 'system:user:create', '用户新增'),
('USER_EDIT', 'system:user:update', '用户编辑'),
('USER_DELETE', 'system:user:delete', '用户删除'),
('USER_EXPORT', 'system:user:export', '用户导出'),
('USER_IMPORT', 'system:user:import', '用户导入'),

-- 角色权限模块
('ROLE_MANAGE', 'system:role:manage', '角色管理'),
('ROLE_VIEW', 'system:role:view', '角色查看'),
('ROLE_ADD', 'system:role:create', '角色新增'),
('ROLE_EDIT', 'system:role:update', '角色编辑'),
('ROLE_DELETE', 'system:role:delete', '角色删除'),
('ROLE_ASSIGN', 'system:role:assign', '角色分配权限'),

-- 权限管理模块
('PERMISSION_MANAGE', 'system:permission:manage', '权限管理'),
('PERMISSION_VIEW', 'system:permission:view', '权限查看'),
('PERMISSION_ASSIGN', 'system:permission:assign', '权限分配'),

-- 组织机构模块
('ORGANIZATION_MANAGE', 'system:org:manage', '组织管理'),
('ORGANIZATION_VIEW', 'system:org:view', '组织查看'),
('ORGANIZATION_ADD', 'system:org:create', '组织新增'),
('ORGANIZATION_EDIT', 'system:org:update', '组织编辑'),
('ORGANIZATION_DELETE', 'system:org:delete', '组织删除'),

-- 职位管理模块
('POSITION_MANAGE', 'system:position:manage', '职位管理'),
('POSITION_VIEW', 'system:position:view', '职位查看'),
('POSITION_ADD', 'system:position:create', '职位新增'),
('POSITION_EDIT', 'system:position:update', '职位编辑'),
('POSITION_DELETE', 'system:position:delete', '职位删除'),

-- 职称管理模块
('TITLE_MANAGE', 'system:title:manage', '职称管理'),
('TITLE_VIEW', 'system:title:view', '职称查看'),
('TITLE_ADD', 'system:title:create', '职称新增'),
('TITLE_EDIT', 'system:title:update', '职称编辑'),
('TITLE_DELETE', 'system:title:delete', '职称删除'),

-- 房间管理模块
('ROOM_MANAGE', 'building:room:manage', '房间管理'),
('ROOM_VIEW', 'building:room:view', '房间查看'),
('ROOM_SEARCH', 'building:room:search', '房间搜索'),
('ROOM_ADD', 'building:room:create', '房间新增'),
('ROOM_EDIT', 'building:room:update', '房间编辑'),
('ROOM_DELETE', 'building:room:delete', '房间删除'),
('ROOM_BOOK', 'building:room:book', '房间预订'),
('ROOM_APPROVE', 'building:room:approve', '房间审批'),
('ROOM_CANCEL_BOOK', 'building:room:cancel', '取消预订'),
('ROOM_EXPORT', 'building:room:export', '房间导出'),
('ROOM_IMPORT', 'building:room:import', '房间导入'),

-- 区域管理模块
('AREA_MANAGE', 'building:area:manage', '区域管理'),
('AREA_VIEW', 'building:area:view', '区域查看'),
('AREA_ADD', 'building:area:create', '区域新增'),
('AREA_EDIT', 'building:area:update', '区域编辑'),
('AREA_DELETE', 'building:area:delete', '区域删除'),

-- 预订管理模块
('BOOKING_MANAGE', 'booking:record:manage', '预订管理'),
('BOOKING_VIEW', 'booking:record:view', '预订查看'),
('BOOKING_SEARCH', 'booking:record:search', '预订搜索'),
('BOOKING_CREATE', 'booking:record:create', '创建预订'),
('BOOKING_EDIT', 'booking:record:update', '编辑预订'),
('BOOKING_CANCEL', 'booking:record:cancel', '取消预订'),
('BOOKING_DELETE', 'booking:record:delete', '删除预订'),
('BOOKING_APPROVE', 'booking:record:approve', '审批预订'),
('BOOKING_REJECT', 'booking:record:reject', '拒绝预订'),
('BOOKING_EXPORT', 'booking:record:export', '预订导出'),

-- 预订审批模块
('BOOKING_APPROVAL_MANAGE', 'booking:approval:manage', '审批管理'),
('BOOKING_APPROVAL_VIEW', 'booking:approval:view', '审批查看'),
('BOOKING_APPROVAL_APPROVE', 'booking:approval:approve', '执行审批'),

-- 预订配置模块
('BOOKING_CONFIG', 'booking:config:manage', '预订配置'),
('BOOKING_TIME_RULE_MANAGE', 'booking:config:timerule:manage', '时间规则管理'),
('BOOKING_TIME_RULE_VIEW', 'booking:config:timerule:view', '时间规则查看'),
('BOOKING_TIME_RULE_ADD', 'booking:config:timerule:create', '时间规则新增'),
('BOOKING_TIME_RULE_EDIT', 'booking:config:timerule:update', '时间规则编辑'),
('BOOKING_TIME_RULE_DELETE', 'booking:config:timerule:delete', '时间规则删除'),

-- 预订人员权限模块
('BOOKING_PERSONNEL_MANAGE', 'booking:personnel:manage', '预订人员管理'),
('BOOKING_PERSONNEL_VIEW', 'booking:personnel:view', '预订人员查看'),
('BOOKING_PERSONNEL_ADD', 'booking:personnel:create', '预订人员新增'),
('BOOKING_PERSONNEL_EDIT', 'booking:personnel:update', '预订人员编辑'),
('BOOKING_PERSONNEL_DELETE', 'booking:personnel:delete', '预订人员删除'),
('BOOKING_PERMISSION_MANAGE', 'booking:personnel:permission', '预订权限管理'),

-- 连续预订设置
('CONTINUOUS_BOOKING_MANAGE', 'booking:continuous:manage', '连续预订管理'),
('CONTINUOUS_BOOKING_VIEW', 'booking:continuous:view', '连续预订查看'),
('CONTINUOUS_BOOKING_EDIT', 'booking:continuous:update', '连续预订编辑'),
('CONTINUOUS_BOOKING_DELETE', 'booking:continuous:delete', '连续预订删除'),

-- 用户预订限制
('USER_BOOKING_LIMIT_MANAGE', 'booking:limit:manage', '预订限制管理'),
('USER_BOOKING_LIMIT_VIEW', 'booking:limit:view', '预订限制查看'),
('USER_BOOKING_LIMIT_EDIT', 'booking:limit:update', '预订限制编辑'),
('USER_BOOKING_LIMIT_DELETE', 'booking:limit:delete', '预订限制删除'),

-- 数据记录模块
('BOOKING_RECORDS_VIEW', 'booking:records:view', '数据记录查看'),

-- 教室日程模块
('ROOM_SCHEDULE_MANAGE', 'building:schedule:manage', '日程管理'),
('ROOM_SCHEDULE_VIEW', 'building:schedule:view', '日程查看'),

-- 方案管理模块
('SCHEME_MANAGE', 'system:scheme:manage', '方案管理'),
('SCHEME_VIEW', 'system:scheme:view', '方案查看'),
('SCHEME_ADD', 'system:scheme:create', '方案新增'),
('SCHEME_EDIT', 'system:scheme:update', '方案编辑'),
('SCHEME_DELETE', 'system:scheme:delete', '方案删除'),

-- 黑名单管理
('BLACKLIST_MANAGE', 'booking:blacklist:manage', '黑名单管理'),
('BLACKLIST_VIEW', 'booking:blacklist:view', '黑名单查看'),
('BLACKLIST_ADD', 'booking:blacklist:create', '黑名单新增'),
('BLACKLIST_DELETE', 'booking:blacklist:delete', '黑名单删除'),

-- 违规管理
('VIOLATION_MANAGE', 'booking:violation:manage', '违规管理'),

-- 回收站
('RECYCLE_VIEW', 'system:recycle:view', '回收站查看'),
('RECYCLE_RESTORE', 'system:recycle:restore', '回收站恢复'),
('RECYCLE_DELETE', 'system:recycle:delete', '回收站删除'),
('RECYCLE_BIN_MANAGE', 'system:recycle:manage', '回收站管理'),
('RECYCLE_BIN_VIEW', 'system:recycle:view', '回收站查看'),
('RECYCLE_BIN_RESTORE', 'system:recycle:restore', '回收站恢复'),
('RECYCLE_BIN_DELETE', 'system:recycle:delete', '回收站删除'),

-- 报表模块
('REPORT_VIEW', 'report:analysis:view', '报表查看'),
('REPORT_EXPORT', 'report:analysis:export', '报表导出')
ON DUPLICATE KEY UPDATE 
    new_code = VALUES(new_code),
    description = VALUES(description),
    update_time = NOW();

-- 2. 更新sys_permission表的perm_code字段
UPDATE sys_permission p
LEFT JOIN sys_permission_alias a ON p.code = a.old_code
SET p.perm_code = COALESCE(a.new_code, LOWER(REPLACE(p.code, '_', ':')))
WHERE p.perm_code IS NULL OR p.perm_code = '';

-- 3. 插入URL权限映射数据（示例）
INSERT INTO sys_permission_url (http_method, url_pattern, perm_code, description, module) VALUES
-- 用户管理接口
('GET', '/api/user/list', 'system:user:view', '查询用户列表', 'USER'),
('GET', '/api/user/**', 'system:user:view', '查询用户详情', 'USER'),
('POST', '/api/user', 'system:user:create', '创建用户', 'USER'),
('PUT', '/api/user/**', 'system:user:update', '更新用户', 'USER'),
('DELETE', '/api/user/**', 'system:user:delete', '删除用户', 'USER'),
('POST', '/api/user/export', 'system:user:export', '导出用户', 'USER'),
('POST', '/api/user/import', 'system:user:import', '导入用户', 'USER'),

-- 角色管理接口
('GET', '/api/role/list', 'system:role:view', '查询角色列表', 'ROLE'),
('GET', '/api/role/**', 'system:role:view', '查询角色详情', 'ROLE'),
('POST', '/api/role', 'system:role:create', '创建角色', 'ROLE'),
('PUT', '/api/role/**', 'system:role:update', '更新角色', 'ROLE'),
('DELETE', '/api/role/**', 'system:role:delete', '删除角色', 'ROLE'),
('POST', '/api/role/*/permissions', 'system:role:assign', '分配权限', 'ROLE'),
('GET', '/api/role/*/permissions', 'system:role:view', '查看角色权限', 'ROLE'),

-- 房间管理接口
('GET', '/api/room/list', 'building:room:view', '查询房间列表', 'ROOM'),
('GET', '/api/room/search', 'building:room:search', '搜索房间', 'ROOM'),
('GET', '/api/room/**', 'building:room:view', '查询房间详情', 'ROOM'),
('POST', '/api/room', 'building:room:create', '创建房间', 'ROOM'),
('PUT', '/api/room/**', 'building:room:update', '更新房间', 'ROOM'),
('DELETE', '/api/room/**', 'building:room:delete', '删除房间', 'ROOM'),
('GET', '/api/room/available', 'building:room:book', '查询可预订房间', 'ROOM'),
('POST', '/api/room/export', 'building:room:export', '导出房间', 'ROOM'),
('POST', '/api/room/import', 'building:room:import', '导入房间', 'ROOM'),

-- 预订管理接口
('GET', '/api/booking/list', 'booking:record:view', '查询预订列表', 'BOOKING'),
('GET', '/api/booking/my', 'booking:record:view', '查询我的预订', 'BOOKING'),
('GET', '/api/booking/**', 'booking:record:view', '查询预订详情', 'BOOKING'),
('POST', '/api/booking', 'booking:record:create', '创建预订', 'BOOKING'),
('PUT', '/api/booking/**', 'booking:record:update', '更新预订', 'BOOKING'),
('DELETE', '/api/booking/**', 'booking:record:delete', '删除预订', 'BOOKING'),
('POST', '/api/booking/*/cancel', 'booking:record:cancel', '取消预订', 'BOOKING'),
('POST', '/api/booking/*/approve', 'booking:record:approve', '审批预订', 'BOOKING'),
('POST', '/api/booking/*/reject', 'booking:record:reject', '拒绝预订', 'BOOKING'),
('POST', '/api/booking/export', 'booking:record:export', '导出预订记录', 'BOOKING'),

-- 区域管理接口
('GET', '/api/area/tree', 'building:area:view', '查询区域树', 'AREA'),
('GET', '/api/area/**', 'building:area:view', '查询区域详情', 'AREA'),
('POST', '/api/area', 'building:area:create', '创建区域', 'AREA'),
('PUT', '/api/area/**', 'building:area:update', '更新区域', 'AREA'),
('DELETE', '/api/area/**', 'building:area:delete', '删除区域', 'AREA')
ON DUPLICATE KEY UPDATE 
    perm_code = VALUES(perm_code),
    description = VALUES(description),
    update_time = NOW();

-- 4. 统计映射结果
SELECT '权限码映射完成！' as message;
SELECT COUNT(*) as total_mappings FROM sys_permission_alias;
SELECT COUNT(*) as total_url_mappings FROM sys_permission_url;
SELECT COUNT(*) as updated_permissions FROM sys_permission WHERE perm_code IS NOT NULL;