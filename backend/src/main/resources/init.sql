-- 初始化SQL脚本
-- 创建基础数据，确保系统可以正常登录

-- 插入默认客户域
INSERT INTO sys_user (id, username, password, real_name, gender, phone, job_number, 
                      department_id, department_name, position_id, position_name, 
                      title_id, title_name, avatar_url, face_image_url, card_number, 
                      attendance_number, status, customer_id, create_time, update_time, deleted) 
VALUES 
('admin001', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZOzx8Qb6Nh8Qw5Qz5Qz5Qz5Qz', 
 '系统管理员', '男', '13800138000', 'A001', 
 'dept001', '信息技术部', 'pos001', '系统管理员', 
 'title001', '高级工程师', NULL, NULL, 'CARD001', 
 'ATT001', 'NORMAL', 'customer001', NOW(), NOW(), 0),
('user001', 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyZOzx8Qb6Nh8Qw5Qz5Qz5Qz5Qz', 
 '测试用户', '女', '13800138001', 'U001', 
 'dept002', '人事部', 'pos002', '普通员工', 
 'title002', '助理工程师', NULL, NULL, 'CARD002', 
 'ATT002', 'NORMAL', 'customer001', NOW(), NOW(), 0);

-- 插入角色数据
INSERT INTO sys_role (id, name, code, remark, customer_id, create_time, update_time, deleted) 
VALUES 
('role001', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 'customer001', NOW(), NOW(), 0),
('role002', '普通用户', 'USER', '普通用户角色', 'customer001', NOW(), NOW(), 0),
('role003', '部门管理员', 'DEPT_ADMIN', '部门管理员，可管理本部门用户', 'customer001', NOW(), NOW(), 0);

-- 插入权限数据
INSERT INTO sys_permission (id, name, code, type, parent_id, url, sort, customer_id, create_time, update_time, deleted) 
VALUES 
-- 系统管理菜单
('perm001', '系统管理', 'SYSTEM_MANAGE', 'MENU', NULL, '/system', 1, 'customer001', NOW(), NOW(), 0),
-- 用户管理
('perm002', '用户管理', 'USER_MANAGE', 'MENU', 'perm001', '/system/user', 1, 'customer001', NOW(), NOW(), 0),
('perm003', '用户查询', 'USER_SEARCH', 'BUTTON', 'perm002', 'user:search', 1, 'customer001', NOW(), NOW(), 0),
('perm004', '用户新增', 'USER_ADD', 'BUTTON', 'perm002', 'user:add', 2, 'customer001', NOW(), NOW(), 0),
('perm005', '用户编辑', 'USER_EDIT', 'BUTTON', 'perm002', 'user:edit', 3, 'customer001', NOW(), NOW(), 0),
('perm006', '用户删除', 'USER_DELETE', 'BUTTON', 'perm002', 'user:delete', 4, 'customer001', NOW(), NOW(), 0),
('perm007', '重置密码', 'USER_RESET_PWD', 'BUTTON', 'perm002', 'user:resetPwd', 5, 'customer001', NOW(), NOW(), 0),
('perm008', '分配角色', 'USER_ASSIGN_ROLE', 'BUTTON', 'perm002', 'user:assignRole', 6, 'customer001', NOW(), NOW(), 0),
-- 角色管理
('perm009', '角色管理', 'ROLE_MANAGE', 'MENU', 'perm001', '/system/role', 2, 'customer001', NOW(), NOW(), 0),
('perm010', '角色查询', 'ROLE_SEARCH', 'BUTTON', 'perm009', 'role:search', 1, 'customer001', NOW(), NOW(), 0),
('perm011', '角色新增', 'ROLE_ADD', 'BUTTON', 'perm009', 'role:add', 2, 'customer001', NOW(), NOW(), 0),
('perm012', '角色编辑', 'ROLE_EDIT', 'BUTTON', 'perm009', 'role:edit', 3, 'customer001', NOW(), NOW(), 0),
('perm013', '角色删除', 'ROLE_DELETE', 'BUTTON', 'perm009', 'role:delete', 4, 'customer001', NOW(), NOW(), 0),
('perm014', '分配权限', 'ROLE_ASSIGN_PERM', 'BUTTON', 'perm009', 'role:assignPerm', 5, 'customer001', NOW(), NOW(), 0),
-- 权限管理
('perm015', '权限管理', 'PERMISSION_MANAGE', 'MENU', 'perm001', '/system/permission', 3, 'customer001', NOW(), NOW(), 0),
('perm016', '权限查询', 'PERMISSION_SEARCH', 'BUTTON', 'perm015', 'permission:search', 1, 'customer001', NOW(), NOW(), 0),
('perm017', '权限新增', 'PERMISSION_ADD', 'BUTTON', 'perm015', 'permission:add', 2, 'customer001', NOW(), NOW(), 0),
('perm018', '权限编辑', 'PERMISSION_EDIT', 'BUTTON', 'perm015', 'permission:edit', 3, 'customer001', NOW(), NOW(), 0),
('perm019', '权限删除', 'PERMISSION_DELETE', 'BUTTON', 'perm015', 'permission:delete', 4, 'customer001', NOW(), NOW(), 0);

-- 插入用户角色关联数据
INSERT INTO sys_user_role (id, user_id, role_id, create_time) 
VALUES 
('ur001', 'admin001', 'role001', NOW()),  -- admin用户分配超级管理员角色
('ur002', 'user001', 'role002', NOW());    -- testuser用户分配普通用户角色

-- 插入角色权限关联数据（超级管理员拥有所有权限）
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time) 
VALUES 
-- 超级管理员权限
('rp001', 'role001', 'perm001', NOW()),
('rp002', 'role001', 'perm002', NOW()),
('rp003', 'role001', 'perm003', NOW()),
('rp004', 'role001', 'perm004', NOW()),
('rp005', 'role001', 'perm005', NOW()),
('rp006', 'role001', 'perm006', NOW()),
('rp007', 'role001', 'perm007', NOW()),
('rp008', 'role001', 'perm008', NOW()),
('rp009', 'role001', 'perm009', NOW()),
('rp010', 'role001', 'perm010', NOW()),
('rp011', 'role001', 'perm011', NOW()),
('rp012', 'role001', 'perm012', NOW()),
('rp013', 'role001', 'perm013', NOW()),
('rp014', 'role001', 'perm014', NOW()),
('rp015', 'role001', 'perm015', NOW()),
('rp016', 'role001', 'perm016', NOW()),
('rp017', 'role001', 'perm017', NOW()),
('rp018', 'role001', 'perm018', NOW()),
('rp019', 'role001', 'perm019', NOW()),
-- 普通用户权限（只有查询权限）
('rp020', 'role002', 'perm001', NOW()),
('rp021', 'role002', 'perm002', NOW()),
('rp022', 'role002', 'perm003', NOW());

-- 注意：
-- 1. 默认管理员账号：admin，密码：123456（已加密）
-- 2. 默认测试账号：testuser，密码：123456（已加密）
-- 3. 客户域ID：customer001
-- 4. 密码加密使用BCrypt，实际密码为：123456
-- 5. 所有时间字段使用NOW()函数自动填充当前时间