-- Step 3: URL权限映射初始化数据
-- 用于配置哪些URL需要哪些权限才能访问

-- 用户管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/sys-user/**', 'GET', 'system:user:view', 'USER', '查看用户信息', 10, 1),
('/api/sys-user', 'POST', 'system:user:create', 'USER', '创建用户', 11, 1),
('/api/sys-user/**', 'PUT', 'system:user:update', 'USER', '更新用户', 12, 1),
('/api/sys-user/**', 'DELETE', 'system:user:delete', 'USER', '删除用户', 13, 1),
('/api/sys-user/reset-password', 'POST', 'system:user:reset-password', 'USER', '重置密码', 14, 1),
('/api/sys-user/batch-import', 'POST', 'system:user:import', 'USER', '批量导入用户', 15, 1),
('/api/sys-user/export', 'GET', 'system:user:export', 'USER', '导出用户', 16, 1);

-- 角色管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/role/**', 'GET', 'system:role:view', 'ROLE', '查看角色信息', 20, 1),
('/api/role', 'POST', 'system:role:create', 'ROLE', '创建角色', 21, 1),
('/api/role/**', 'PUT', 'system:role:update', 'ROLE', '更新角色', 22, 1),
('/api/role/**', 'DELETE', 'system:role:delete', 'ROLE', '删除角色', 23, 1),
('/api/role/*/permissions', 'PUT', 'system:role:assign-permission', 'ROLE', '分配权限', 24, 1);

-- 房间管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/room/**', 'GET', 'building:room:view', 'ROOM', '查看房间信息', 30, 1),
('/api/room', 'POST', 'building:room:create', 'ROOM', '创建房间', 31, 1),
('/api/room/**', 'PUT', 'building:room:update', 'ROOM', '更新房间', 32, 1),
('/api/room/**', 'DELETE', 'building:room:delete', 'ROOM', '删除房间', 33, 1),
('/api/room/batch-import', 'POST', 'building:room:import', 'ROOM', '批量导入房间', 34, 1),
('/api/room/export', 'GET', 'building:room:export', 'ROOM', '导出房间', 35, 1);

-- 预约管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/room-booking/**', 'GET', 'booking:booking:view', 'BOOKING', '查看预约信息', 40, 1),
('/api/room-booking', 'POST', 'booking:booking:create', 'BOOKING', '创建预约', 41, 1),
('/api/room-booking/**', 'PUT', 'booking:booking:update', 'BOOKING', '更新预约', 42, 1),
('/api/room-booking/**', 'DELETE', 'booking:booking:delete', 'BOOKING', '删除预约', 43, 1),
('/api/room-booking/*/cancel', 'PUT', 'booking:booking:cancel', 'BOOKING', '取消预约', 44, 1),
('/api/room-booking/*/checkin', 'POST', 'booking:booking:checkin', 'BOOKING', '签到', 45, 1);

-- 预约审批相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/room-booking-approval/**', 'GET', 'booking:approval:view', 'BOOKING_APPROVAL', '查看审批信息', 50, 1),
('/api/room-booking-approval/*/approve', 'PUT', 'booking:approval:approve', 'BOOKING_APPROVAL', '审批通过', 51, 1),
('/api/room-booking-approval/*/reject', 'PUT', 'booking:approval:reject', 'BOOKING_APPROVAL', '审批拒绝', 52, 1),
('/api/room-booking-approval/batch-approve', 'POST', 'booking:approval:batch', 'BOOKING_APPROVAL', '批量审批', 53, 1);

-- 区域管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/area/**', 'GET', 'building:area:view', 'AREA', '查看区域信息', 60, 1),
('/api/area', 'POST', 'building:area:create', 'AREA', '创建区域', 61, 1),
('/api/area/**', 'PUT', 'building:area:update', 'AREA', '更新区域', 62, 1),
('/api/area/**', 'DELETE', 'building:area:delete', 'AREA', '删除区域', 63, 1);

-- 组织机构相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/organization/**', 'GET', 'system:organization:view', 'ORGANIZATION', '查看组织信息', 70, 1),
('/api/organization', 'POST', 'system:organization:create', 'ORGANIZATION', '创建组织', 71, 1),
('/api/organization/**', 'PUT', 'system:organization:update', 'ORGANIZATION', '更新组织', 72, 1),
('/api/organization/**', 'DELETE', 'system:organization:delete', 'ORGANIZATION', '删除组织', 73, 1);

-- 黑名单管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/blacklist/**', 'GET', 'system:blacklist:view', 'BLACKLIST', '查看黑名单', 80, 1),
('/api/blacklist', 'POST', 'system:blacklist:create', 'BLACKLIST', '添加黑名单', 81, 1),
('/api/blacklist/**', 'PUT', 'system:blacklist:update', 'BLACKLIST', '更新黑名单', 82, 1),
('/api/blacklist/**', 'DELETE', 'system:blacklist:delete', 'BLACKLIST', '删除黑名单', 83, 1);

-- 权限管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/permission/**', 'GET', 'system:permission:view', 'PERMISSION', '查看权限信息', 90, 1),
('/api/permission/refresh/**', 'POST', 'system:permission:refresh', 'PERMISSION', '刷新权限缓存', 91, 1),
('/api/user-permission/**', 'GET', 'system:permission:view', 'PERMISSION', '查看用户权限', 92, 1);

-- 系统设置相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/booking-time-rule/**', '*', 'system:setting:manage', 'SYSTEM', '预约时间规则管理', 100, 1),
('/api/continuous-booking-setting/**', '*', 'system:setting:manage', 'SYSTEM', '连续预约设置', 101, 1),
('/api/violation-setting/**', '*', 'system:setting:manage', 'SYSTEM', '违约设置', 102, 1),
('/api/user-booking-limit/**', '*', 'system:setting:manage', 'SYSTEM', '用户预约限制', 103, 1);

-- 文件管理相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/file/upload', 'POST', 'system:file:upload', 'FILE', '文件上传', 110, 1),
('/api/file/download/**', 'GET', 'system:file:download', 'FILE', '文件下载', 111, 1),
('/api/file/delete/**', 'DELETE', 'system:file:delete', 'FILE', '文件删除', 112, 1);

-- 报表相关URL
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/api/report/**', 'GET', 'system:report:view', 'REPORT', '查看报表', 120, 1),
('/api/report/export', 'POST', 'system:report:export', 'REPORT', '导出报表', 121, 1);

-- 公开接口（不需要权限）
INSERT INTO sys_permission_url (url_pattern, http_method, perm_code, module, description, sort_order, status) VALUES
('/authentication/login', '*', 'PUBLIC', 'AUTH', '登录接口', 1, 1),
('/authentication/logout', '*', 'PUBLIC', 'AUTH', '登出接口', 2, 1),
('/authentication/forgot-password', '*', 'PUBLIC', 'AUTH', '忘记密码', 3, 1),
('/api/public/**', '*', 'PUBLIC', 'PUBLIC', '公开接口', 4, 1);