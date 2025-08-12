-- 新增权限数据 - 为新添加权限注解的控制器
-- 执行前请确保已执行 complete_permissions_init.sql

-- ==================== 新增业务模块权限 ====================

-- 预订人员权限管理模块
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm082', 'BOOKING_PERSONNEL_MANAGE', '预订人员权限管理', 'MENU', NULL, '/booking-personnel', 18, 'demo', 0, NOW()),
('perm083', 'BOOKING_PERSONNEL_VIEW', '预订人员权限查看', 'BUTTON', 'perm082', NULL, 1, 'demo', 0, NOW()),
('perm084', 'BOOKING_PERSONNEL_ADD', '预订人员权限新增', 'BUTTON', 'perm082', NULL, 2, 'demo', 0, NOW()),
('perm085', 'BOOKING_PERSONNEL_EDIT', '预订人员权限编辑', 'BUTTON', 'perm082', NULL, 3, 'demo', 0, NOW()),
('perm086', 'BOOKING_PERSONNEL_DELETE', '预订人员权限删除', 'BUTTON', 'perm082', NULL, 4, 'demo', 0, NOW());

-- 连续预订设置模块
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm087', 'CONTINUOUS_BOOKING_MANAGE', '连续预订管理', 'MENU', NULL, '/continuous-booking', 19, 'demo', 0, NOW()),
('perm088', 'CONTINUOUS_BOOKING_VIEW', '连续预订查看', 'BUTTON', 'perm087', NULL, 1, 'demo', 0, NOW()),
('perm089', 'CONTINUOUS_BOOKING_EDIT', '连续预订编辑', 'BUTTON', 'perm087', NULL, 2, 'demo', 0, NOW()),
('perm090', 'CONTINUOUS_BOOKING_DELETE', '连续预订删除', 'BUTTON', 'perm087', NULL, 3, 'demo', 0, NOW());

-- 用户预订限制模块
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm091', 'USER_BOOKING_LIMIT_MANAGE', '用户预订限制管理', 'MENU', NULL, '/user-booking-limit', 20, 'demo', 0, NOW()),
('perm092', 'USER_BOOKING_LIMIT_VIEW', '用户预订限制查看', 'BUTTON', 'perm091', NULL, 1, 'demo', 0, NOW()),
('perm093', 'USER_BOOKING_LIMIT_EDIT', '用户预订限制编辑', 'BUTTON', 'perm091', NULL, 2, 'demo', 0, NOW()),
('perm094', 'USER_BOOKING_LIMIT_DELETE', '用户预订限制删除', 'BUTTON', 'perm091', NULL, 3, 'demo', 0, NOW());

-- 预订时间规则模块
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm095', 'BOOKING_TIME_RULE_MANAGE', '预订时间规则管理', 'MENU', NULL, '/booking-time-rule', 21, 'demo', 0, NOW()),
('perm096', 'BOOKING_TIME_RULE_VIEW', '预订时间规则查看', 'BUTTON', 'perm095', NULL, 1, 'demo', 0, NOW()),
('perm097', 'BOOKING_TIME_RULE_ADD', '预订时间规则新增', 'BUTTON', 'perm095', NULL, 2, 'demo', 0, NOW()),
('perm098', 'BOOKING_TIME_RULE_EDIT', '预订时间规则编辑', 'BUTTON', 'perm095', NULL, 3, 'demo', 0, NOW()),
('perm099', 'BOOKING_TIME_RULE_DELETE', '预订时间规则删除', 'BUTTON', 'perm095', NULL, 4, 'demo', 0, NOW());

-- 回收站管理模块（更新）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm100', 'RECYCLE_BIN_MANAGE', '回收站管理', 'MENU', NULL, '/recycle-bin', 22, 'demo', 0, NOW()),
('perm101', 'RECYCLE_BIN_VIEW', '回收站查看', 'BUTTON', 'perm100', NULL, 1, 'demo', 0, NOW()),
('perm102', 'RECYCLE_BIN_RESTORE', '回收站恢复', 'BUTTON', 'perm100', NULL, 2, 'demo', 0, NOW()),
('perm103', 'RECYCLE_BIN_DELETE', '回收站删除', 'BUTTON', 'perm100', NULL, 3, 'demo', 0, NOW());

-- 文件管理模块（扩展）
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm104', 'FILE_VIEW', '文件查看', 'BUTTON', 'perm045', NULL, 2, 'demo', 0, NOW());

-- 教室日程管理模块
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm105', 'ROOM_SCHEDULE_MANAGE', '教室日程管理', 'MENU', NULL, '/room-schedule', 23, 'demo', 0, NOW()),
('perm106', 'ROOM_SCHEDULE_VIEW', '教室日程查看', 'BUTTON', 'perm105', NULL, 1, 'demo', 0, NOW());

-- 预订审批管理模块
INSERT IGNORE INTO sys_permission (id, code, name, type, parent_id, url, sort, customer_id, deleted, create_time) VALUES
('perm107', 'BOOKING_APPROVAL_MANAGE', '预订审批管理', 'MENU', NULL, '/booking-approval', 24, 'demo', 0, NOW()),
('perm108', 'BOOKING_APPROVAL_VIEW', '预订审批查看', 'BUTTON', 'perm107', NULL, 1, 'demo', 0, NOW()),
('perm109', 'BOOKING_APPROVAL_APPROVE', '预订审批通过', 'BUTTON', 'perm107', NULL, 2, 'demo', 0, NOW());

-- ==================== 更新角色权限分配 ====================

-- 为超级管理员添加所有新权限（自动通过之前的脚本完成）

-- 为普通用户添加基础新权限
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) VALUES
-- 文件查看权限
('rp_user_104', 'role002', 'perm104', NOW(), 'demo'),
-- 教室日程查看权限
('rp_user_106', 'role002', 'perm106', NOW(), 'demo');

-- 为部门管理员添加扩展新权限
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) VALUES
-- 预订人员权限管理
('rp_dept_082', 'role003', 'perm082', NOW(), 'demo'),
('rp_dept_083', 'role003', 'perm083', NOW(), 'demo'),
('rp_dept_084', 'role003', 'perm084', NOW(), 'demo'),
('rp_dept_085', 'role003', 'perm085', NOW(), 'demo'),
('rp_dept_086', 'role003', 'perm086', NOW(), 'demo'),
-- 连续预订设置
('rp_dept_087', 'role003', 'perm087', NOW(), 'demo'),
('rp_dept_088', 'role003', 'perm088', NOW(), 'demo'),
('rp_dept_089', 'role003', 'perm089', NOW(), 'demo'),
('rp_dept_090', 'role003', 'perm090', NOW(), 'demo'),
-- 用户预订限制
('rp_dept_091', 'role003', 'perm091', NOW(), 'demo'),
('rp_dept_092', 'role003', 'perm092', NOW(), 'demo'),
('rp_dept_093', 'role003', 'perm093', NOW(), 'demo'),
('rp_dept_094', 'role003', 'perm094', NOW(), 'demo'),
-- 预订时间规则
('rp_dept_095', 'role003', 'perm095', NOW(), 'demo'),
('rp_dept_096', 'role003', 'perm096', NOW(), 'demo'),
('rp_dept_097', 'role003', 'perm097', NOW(), 'demo'),
('rp_dept_098', 'role003', 'perm098', NOW(), 'demo'),
('rp_dept_099', 'role003', 'perm099', NOW(), 'demo'),
-- 回收站管理
('rp_dept_100', 'role003', 'perm100', NOW(), 'demo'),
('rp_dept_101', 'role003', 'perm101', NOW(), 'demo'),
('rp_dept_102', 'role003', 'perm102', NOW(), 'demo'),
('rp_dept_103', 'role003', 'perm103', NOW(), 'demo'),
-- 文件查看权限
('rp_dept_104', 'role003', 'perm104', NOW(), 'demo'),
-- 教室日程管理
('rp_dept_105', 'role003', 'perm105', NOW(), 'demo'),
('rp_dept_106', 'role003', 'perm106', NOW(), 'demo'),
-- 预订审批管理
('rp_dept_107', 'role003', 'perm107', NOW(), 'demo'),
('rp_dept_108', 'role003', 'perm108', NOW(), 'demo'),
('rp_dept_109', 'role003', 'perm109', NOW(), 'demo');

-- ==================== 验证新增权限 ====================

-- 统计新增权限数量
SELECT COUNT(*) as new_permissions_count 
FROM sys_permission 
WHERE id >= 'perm082' AND deleted = 0 AND customer_id = 'demo';

-- 查看新增权限列表
SELECT code, name, type 
FROM sys_permission 
WHERE id >= 'perm082' AND deleted = 0 AND customer_id = 'demo'
ORDER BY id;

-- 验证超级管理员是否拥有所有权限（包括新增的）
SELECT COUNT(DISTINCT p.id) as total_admin_permissions
FROM sys_permission p
WHERE p.deleted = 0 AND p.customer_id = 'demo'
AND EXISTS (
    SELECT 1 FROM sys_role_permission rp 
    WHERE rp.permission_id = p.id AND rp.role_id = 'role001'
);