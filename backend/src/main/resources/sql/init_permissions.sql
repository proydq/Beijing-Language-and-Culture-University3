-- 权限数据初始化脚本
-- 清理已存在的权限数据（可选，根据需要使用）
-- DELETE FROM sys_permission WHERE customer_id = 'demo';

-- 用户管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm001', 'USER_MANAGE', '用户管理', 'MENU', NULL, '/user', 1, 'demo', 0, NOW()),
('perm002', 'USER_SEARCH', '用户查询', 'BUTTON', 'perm001', NULL, 1, 'demo', 0, NOW()),
('perm003', 'USER_VIEW', '用户查看', 'BUTTON', 'perm001', NULL, 2, 'demo', 0, NOW()),
('perm004', 'USER_ADD', '用户新增', 'BUTTON', 'perm001', NULL, 3, 'demo', 0, NOW()),
('perm005', 'USER_EDIT', '用户编辑', 'BUTTON', 'perm001', NULL, 4, 'demo', 0, NOW()),
('perm006', 'USER_DELETE', '用户删除', 'BUTTON', 'perm001', NULL, 5, 'demo', 0, NOW()),
('perm007', 'USER_RESET_PWD', '重置密码', 'BUTTON', 'perm001', NULL, 6, 'demo', 0, NOW()),
('perm008', 'USER_ASSIGN_ROLE', '分配角色', 'BUTTON', 'perm001', NULL, 7, 'demo', 0, NOW()),
('perm009', 'USER_EXPORT', '用户导出', 'BUTTON', 'perm001', NULL, 8, 'demo', 0, NOW()),
('perm010', 'USER_IMPORT', '用户导入', 'BUTTON', 'perm001', NULL, 9, 'demo', 0, NOW());

-- 角色管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm011', 'ROLE_MANAGE', '角色管理', 'MENU', NULL, '/role', 2, 'demo', 0, NOW()),
('perm012', 'ROLE_SEARCH', '角色查询', 'BUTTON', 'perm011', NULL, 1, 'demo', 0, NOW()),
('perm013', 'ROLE_ADD', '角色新增', 'BUTTON', 'perm011', NULL, 2, 'demo', 0, NOW()),
('perm014', 'ROLE_EDIT', '角色编辑', 'BUTTON', 'perm011', NULL, 3, 'demo', 0, NOW()),
('perm015', 'ROLE_DELETE', '角色删除', 'BUTTON', 'perm011', NULL, 4, 'demo', 0, NOW()),
('perm016', 'ROLE_ASSIGN_PERM', '分配权限', 'BUTTON', 'perm011', NULL, 5, 'demo', 0, NOW());

-- 权限管理模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm017', 'PERMISSION_MANAGE', '权限管理', 'MENU', NULL, '/permission', 3, 'demo', 0, NOW()),
('perm018', 'PERMISSION_SEARCH', '权限查询', 'BUTTON', 'perm017', NULL, 1, 'demo', 0, NOW()),
('perm019', 'PERMISSION_ADD', '权限新增', 'BUTTON', 'perm017', NULL, 2, 'demo', 0, NOW()),
('perm020', 'PERMISSION_EDIT', '权限编辑', 'BUTTON', 'perm017', NULL, 3, 'demo', 0, NOW()),
('perm021', 'PERMISSION_DELETE', '权限删除', 'BUTTON', 'perm017', NULL, 4, 'demo', 0, NOW());

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

-- 系统设置模块权限
INSERT INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm039', 'SYSTEM_MANAGE', '系统管理', 'MENU', NULL, '/system', 6, 'demo', 0, NOW()),
('perm040', 'SYSTEM_CONFIG', '系统配置', 'BUTTON', 'perm039', NULL, 1, 'demo', 0, NOW()),
('perm041', 'SYSTEM_LOG_VIEW', '日志查看', 'BUTTON', 'perm039', NULL, 2, 'demo', 0, NOW()),
('perm042', 'SYSTEM_BACKUP', '系统备份', 'BUTTON', 'perm039', NULL, 3, 'demo', 0, NOW());

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

-- 查询现有角色
-- SELECT * FROM sys_role WHERE deleted = 0;

-- 为超级管理员角色分配所有权限
-- 首先清理旧的权限分配
DELETE FROM sys_role_permission WHERE role_id = 'role001';

-- 为超级管理员分配所有权限
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id)
SELECT 
    CONCAT('rp_admin_', SUBSTRING(id, 5)) as id,
    'role001' as role_id,
    id as permission_id,
    NOW() as create_time,
    'demo' as customer_id
FROM sys_permission 
WHERE deleted = 0 AND customer_id = 'demo';

-- 为普通用户角色分配基础权限
DELETE FROM sys_role_permission WHERE role_id = 'role002';

INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) VALUES
-- 用户管理基础权限
('rp_user_001', 'role002', 'perm001', NOW(), 'demo'),  -- 用户管理菜单
('rp_user_002', 'role002', 'perm002', NOW(), 'demo'),  -- 用户查询
('rp_user_003', 'role002', 'perm003', NOW(), 'demo'),  -- 用户查看
-- 教室管理基础权限
('rp_user_022', 'role002', 'perm022', NOW(), 'demo'),  -- 教室管理菜单
('rp_user_023', 'role002', 'perm023', NOW(), 'demo'),  -- 教室查询
('rp_user_024', 'role002', 'perm024', NOW(), 'demo'),  -- 教室查看
('rp_user_028', 'role002', 'perm028', NOW(), 'demo'),  -- 教室预订
('rp_user_029', 'role002', 'perm029', NOW(), 'demo'),  -- 取消预订
-- 预订管理基础权限
('rp_user_031', 'role002', 'perm031', NOW(), 'demo'),  -- 预订管理菜单
('rp_user_032', 'role002', 'perm032', NOW(), 'demo'),  -- 预订查询
('rp_user_033', 'role002', 'perm033', NOW(), 'demo'),  -- 预订查看
('rp_user_034', 'role002', 'perm034', NOW(), 'demo'),  -- 创建预订
('rp_user_036', 'role002', 'perm036', NOW(), 'demo'),  -- 取消预订
-- 报表查看权限
('rp_user_043', 'role002', 'perm043', NOW(), 'demo'),  -- 报表查看
-- 文件上传权限
('rp_user_046', 'role002', 'perm046', NOW(), 'demo');  -- 文件上传

-- 为部门管理员角色分配权限
DELETE FROM sys_role_permission WHERE role_id = 'role003';

INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) VALUES
-- 用户管理权限（除删除外）
('rp_dept_001', 'role003', 'perm001', NOW(), 'demo'),  -- 用户管理菜单
('rp_dept_002', 'role003', 'perm002', NOW(), 'demo'),  -- 用户查询
('rp_dept_003', 'role003', 'perm003', NOW(), 'demo'),  -- 用户查看
('rp_dept_004', 'role003', 'perm004', NOW(), 'demo'),  -- 用户新增
('rp_dept_005', 'role003', 'perm005', NOW(), 'demo'),  -- 用户编辑
('rp_dept_007', 'role003', 'perm007', NOW(), 'demo'),  -- 重置密码
('rp_dept_008', 'role003', 'perm008', NOW(), 'demo'),  -- 分配角色
-- 教室管理全部权限
('rp_dept_022', 'role003', 'perm022', NOW(), 'demo'),  -- 教室管理菜单
('rp_dept_023', 'role003', 'perm023', NOW(), 'demo'),  -- 教室查询
('rp_dept_024', 'role003', 'perm024', NOW(), 'demo'),  -- 教室查看
('rp_dept_025', 'role003', 'perm025', NOW(), 'demo'),  -- 教室新增
('rp_dept_026', 'role003', 'perm026', NOW(), 'demo'),  -- 教室编辑
('rp_dept_027', 'role003', 'perm027', NOW(), 'demo'),  -- 教室删除
('rp_dept_028', 'role003', 'perm028', NOW(), 'demo'),  -- 教室预订
('rp_dept_029', 'role003', 'perm029', NOW(), 'demo'),  -- 取消预订
('rp_dept_030', 'role003', 'perm030', NOW(), 'demo'),  -- 预订审批
-- 预订管理全部权限
('rp_dept_031', 'role003', 'perm031', NOW(), 'demo'),  -- 预订管理菜单
('rp_dept_032', 'role003', 'perm032', NOW(), 'demo'),  -- 预订查询
('rp_dept_033', 'role003', 'perm033', NOW(), 'demo'),  -- 预订查看
('rp_dept_034', 'role003', 'perm034', NOW(), 'demo'),  -- 创建预订
('rp_dept_035', 'role003', 'perm035', NOW(), 'demo'),  -- 编辑预订
('rp_dept_036', 'role003', 'perm036', NOW(), 'demo'),  -- 取消预订
('rp_dept_037', 'role003', 'perm037', NOW(), 'demo'),  -- 审批预订
('rp_dept_038', 'role003', 'perm038', NOW(), 'demo'),  -- 拒绝预订
-- 报表权限
('rp_dept_043', 'role003', 'perm043', NOW(), 'demo'),  -- 报表查看
('rp_dept_044', 'role003', 'perm044', NOW(), 'demo'),  -- 报表导出
-- 文件管理权限
('rp_dept_045', 'role003', 'perm045', NOW(), 'demo'),  -- 文件管理
('rp_dept_046', 'role003', 'perm046', NOW(), 'demo'),  -- 文件上传
-- 区域管理权限
('rp_dept_060', 'role003', 'perm060', NOW(), 'demo'),  -- 区域管理
('rp_dept_061', 'role003', 'perm061', NOW(), 'demo'),  -- 区域查看
('rp_dept_062', 'role003', 'perm062', NOW(), 'demo'),  -- 区域新增
('rp_dept_063', 'role003', 'perm063', NOW(), 'demo'),  -- 区域编辑
('rp_dept_064', 'role003', 'perm064', NOW(), 'demo');  -- 区域删除

-- 验证权限分配
SELECT 
    r.name as role_name,
    r.code as role_code,
    COUNT(rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
WHERE r.deleted = 0
GROUP BY r.id, r.name, r.code;

-- 验证test用户的权限
SELECT DISTINCT
    u.username,
    r.name as role_name,
    p.code as permission_code,
    p.name as permission_name
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE u.username = 'test' 
AND u.deleted = 0 
AND r.deleted = 0 
AND p.deleted = 0
ORDER BY p.code;