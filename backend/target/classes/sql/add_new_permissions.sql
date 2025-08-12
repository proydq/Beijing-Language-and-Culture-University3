-- 只插入新的权限（从perm022开始）
-- 教室管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm022', 'ROOM_MANAGE', '教室管理', 'MENU', NULL, '/room', 4, 'demo', 0, NOW()),
('perm023', 'ROOM_SEARCH', '教室查询', 'BUTTON', 'perm022', NULL, 1, 'demo', 0, NOW()),
('perm024', 'ROOM_VIEW', '教室查看', 'BUTTON', 'perm022', NULL, 2, 'demo', 0, NOW()),
('perm025', 'ROOM_ADD', '教室新增', 'BUTTON', 'perm022', NULL, 3, 'demo', 0, NOW()),
('perm026', 'ROOM_EDIT', '教室编辑', 'BUTTON', 'perm022', NULL, 4, 'demo', 0, NOW()),
('perm027', 'ROOM_DELETE', '教室删除', 'BUTTON', 'perm022', NULL, 5, 'demo', 0, NOW()),
('perm028', 'ROOM_BOOK', '教室预订', 'BUTTON', 'perm022', NULL, 6, 'demo', 0, NOW()),
('perm029', 'ROOM_CANCEL_BOOK', '取消预订', 'BUTTON', 'perm022', NULL, 7, 'demo', 0, NOW()),
('perm030', 'ROOM_APPROVE', '预订审批', 'BUTTON', 'perm022', NULL, 8, 'demo', 0, NOW());

-- 预订管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm031', 'BOOKING_MANAGE', '预订管理', 'MENU', NULL, '/booking', 5, 'demo', 0, NOW()),
('perm032', 'BOOKING_SEARCH', '预订查询', 'BUTTON', 'perm031', NULL, 1, 'demo', 0, NOW()),
('perm033', 'BOOKING_VIEW', '预订查看', 'BUTTON', 'perm031', NULL, 2, 'demo', 0, NOW()),
('perm034', 'BOOKING_CREATE', '创建预订', 'BUTTON', 'perm031', NULL, 3, 'demo', 0, NOW()),
('perm035', 'BOOKING_EDIT', '编辑预订', 'BUTTON', 'perm031', NULL, 4, 'demo', 0, NOW()),
('perm036', 'BOOKING_CANCEL', '取消预订', 'BUTTON', 'perm031', NULL, 5, 'demo', 0, NOW()),
('perm037', 'BOOKING_APPROVE', '审批预订', 'BUTTON', 'perm031', NULL, 6, 'demo', 0, NOW()),
('perm038', 'BOOKING_REJECT', '拒绝预订', 'BUTTON', 'perm031', NULL, 7, 'demo', 0, NOW());

-- 报表统计模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm043', 'REPORT_VIEW', '报表查看', 'MENU', NULL, '/report', 7, 'demo', 0, NOW()),
('perm044', 'REPORT_EXPORT', '报表导出', 'BUTTON', 'perm043', NULL, 1, 'demo', 0, NOW());

-- 文件管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm045', 'FILE_MANAGE', '文件管理', 'MENU', NULL, '/file', 8, 'demo', 0, NOW()),
('perm046', 'FILE_UPLOAD', '文件上传', 'BUTTON', 'perm045', NULL, 1, 'demo', 0, NOW());

-- 黑名单管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm047', 'BLACKLIST_MANAGE', '黑名单管理', 'MENU', NULL, '/blacklist', 9, 'demo', 0, NOW()),
('perm048', 'BLACKLIST_VIEW', '黑名单查看', 'BUTTON', 'perm047', NULL, 1, 'demo', 0, NOW()),
('perm049', 'BLACKLIST_ADD', '黑名单新增', 'BUTTON', 'perm047', NULL, 2, 'demo', 0, NOW()),
('perm050', 'BLACKLIST_DELETE', '黑名单删除', 'BUTTON', 'perm047', NULL, 3, 'demo', 0, NOW());

-- 违规管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm051', 'VIOLATION_MANAGE', '违规管理', 'MENU', NULL, '/violation', 10, 'demo', 0, NOW());

-- 方案管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm052', 'SCHEME_MANAGE', '方案管理', 'MENU', NULL, '/scheme', 11, 'demo', 0, NOW()),
('perm053', 'SCHEME_VIEW', '方案查看', 'BUTTON', 'perm052', NULL, 1, 'demo', 0, NOW()),
('perm054', 'SCHEME_ADD', '方案新增', 'BUTTON', 'perm052', NULL, 2, 'demo', 0, NOW()),
('perm055', 'SCHEME_EDIT', '方案编辑', 'BUTTON', 'perm052', NULL, 3, 'demo', 0, NOW()),
('perm056', 'SCHEME_DELETE', '方案删除', 'BUTTON', 'perm052', NULL, 4, 'demo', 0, NOW());

-- 回收站模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm057', 'RECYCLE_VIEW', '回收站查看', 'MENU', NULL, '/recycle', 12, 'demo', 0, NOW()),
('perm058', 'RECYCLE_RESTORE', '回收站恢复', 'BUTTON', 'perm057', NULL, 1, 'demo', 0, NOW()),
('perm059', 'RECYCLE_DELETE', '回收站删除', 'BUTTON', 'perm057', NULL, 2, 'demo', 0, NOW());

-- 区域管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm060', 'AREA_MANAGE', '区域管理', 'MENU', NULL, '/area', 13, 'demo', 0, NOW()),
('perm061', 'AREA_VIEW', '区域查看', 'BUTTON', 'perm060', NULL, 1, 'demo', 0, NOW()),
('perm062', 'AREA_ADD', '区域新增', 'BUTTON', 'perm060', NULL, 2, 'demo', 0, NOW()),
('perm063', 'AREA_EDIT', '区域编辑', 'BUTTON', 'perm060', NULL, 3, 'demo', 0, NOW()),
('perm064', 'AREA_DELETE', '区域删除', 'BUTTON', 'perm060', NULL, 4, 'demo', 0, NOW());

-- 预订配置模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm065', 'BOOKING_CONFIG', '预订配置', 'MENU', NULL, '/booking-config', 14, 'demo', 0, NOW()),
('perm066', 'BOOKING_PERMISSION_MANAGE', '预订权限管理', 'BUTTON', 'perm065', NULL, 1, 'demo', 0, NOW());

-- 职称管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm067', 'TITLE_MANAGE', '职称管理', 'MENU', NULL, '/title', 15, 'demo', 0, NOW()),
('perm068', 'TITLE_VIEW', '职称查看', 'BUTTON', 'perm067', NULL, 1, 'demo', 0, NOW()),
('perm069', 'TITLE_ADD', '职称新增', 'BUTTON', 'perm067', NULL, 2, 'demo', 0, NOW()),
('perm070', 'TITLE_EDIT', '职称编辑', 'BUTTON', 'perm067', NULL, 3, 'demo', 0, NOW()),
('perm071', 'TITLE_DELETE', '职称删除', 'BUTTON', 'perm067', NULL, 4, 'demo', 0, NOW());

-- 职位管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm072', 'POSITION_MANAGE', '职位管理', 'MENU', NULL, '/position', 16, 'demo', 0, NOW()),
('perm073', 'POSITION_VIEW', '职位查看', 'BUTTON', 'perm072', NULL, 1, 'demo', 0, NOW()),
('perm074', 'POSITION_ADD', '职位新增', 'BUTTON', 'perm072', NULL, 2, 'demo', 0, NOW()),
('perm075', 'POSITION_EDIT', '职位编辑', 'BUTTON', 'perm072', NULL, 3, 'demo', 0, NOW()),
('perm076', 'POSITION_DELETE', '职位删除', 'BUTTON', 'perm072', NULL, 4, 'demo', 0, NOW());

-- 组织机构模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm077', 'ORG_MANAGE', '组织机构管理', 'MENU', NULL, '/organization', 17, 'demo', 0, NOW()),
('perm078', 'ORG_VIEW', '组织机构查看', 'BUTTON', 'perm077', NULL, 1, 'demo', 0, NOW()),
('perm079', 'ORG_ADD', '组织机构新增', 'BUTTON', 'perm077', NULL, 2, 'demo', 0, NOW()),
('perm080', 'ORG_EDIT', '组织机构编辑', 'BUTTON', 'perm077', NULL, 3, 'demo', 0, NOW()),
('perm081', 'ORG_DELETE', '组织机构删除', 'BUTTON', 'perm077', NULL, 4, 'demo', 0, NOW());