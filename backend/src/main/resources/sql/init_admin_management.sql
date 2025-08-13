-- 管理员管理模块初始化SQL脚本

-- 1. 确保sys_user表结构正确
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS last_login_time DATETIME COMMENT '最后登录时间',
ADD COLUMN IF NOT EXISTS email VARCHAR(100) COMMENT '邮箱',
ADD COLUMN IF NOT EXISTS wechat_nickname VARCHAR(100) COMMENT '微信昵称',
ADD COLUMN IF NOT EXISTS wechat_phone VARCHAR(20) COMMENT '微信绑定手机号',
ADD COLUMN IF NOT EXISTS wechat_bind_time DATETIME COMMENT '微信绑定时间';

-- 2. 确保sys_role表结构正确
ALTER TABLE sys_role 
ADD COLUMN IF NOT EXISTS role_name VARCHAR(50) COMMENT '角色名称',
ADD COLUMN IF NOT EXISTS role_description TEXT COMMENT '角色描述',
ADD COLUMN IF NOT EXISTS role_type VARCHAR(20) COMMENT '角色类型',
ADD COLUMN IF NOT EXISTS is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认角色',
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '角色状态';

-- 3. 确保sys_user_role表结构正确
ALTER TABLE sys_user_role 
ADD COLUMN IF NOT EXISTS customer_id VARCHAR(32) COMMENT '客户域ID';

-- 4. 插入默认角色数据（如果不存在）
INSERT IGNORE INTO sys_role (id, role_name, code, role_description, role_type, is_default, status, customer_id, create_time, update_time, deleted) 
VALUES 
('admin_role_001', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 'ADMIN', 1, 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
('admin_role_002', '系统管理员', 'SYSTEM_ADMIN', '系统管理员，拥有大部分管理权限', 'ADMIN', 0, 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
('admin_role_003', '审批管理员', 'APPROVAL_ADMIN', '审批管理员，主要负责审批相关业务', 'ADMIN', 0, 'ACTIVE', 'default_customer', NOW(), NOW(), 0);

-- 5. 创建管理员相关权限（如果不存在）
INSERT IGNORE INTO sys_permission (id, permission_name, permission_code, permission_type, status, customer_id, create_time, update_time, deleted)
VALUES 
('admin_perm_001', '查看管理员', 'system:admin:view', 'MENU', 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
('admin_perm_002', '创建管理员', 'system:admin:create', 'BUTTON', 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
('admin_perm_003', '更新管理员', 'system:admin:update', 'BUTTON', 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
('admin_perm_004', '删除管理员', 'system:admin:delete', 'BUTTON', 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
('admin_perm_005', '管理员管理', 'system:admin:manage', 'MENU', 'ACTIVE', 'default_customer', NOW(), NOW(), 0);

-- 6. 为超级管理员角色分配权限
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
  CONCAT('admin_role_perm_', p.id),
  'admin_role_001',
  p.id,
  'default_customer',
  NOW()
FROM sys_permission p 
WHERE p.permission_code LIKE 'system:admin:%' AND p.deleted = 0;

-- 7. 更新现有用户状态字段（如果使用的是旧状态值）
UPDATE sys_user SET status = 'ACTIVE' WHERE status = 'NORMAL' OR status = '1' OR status = 'enabled';
UPDATE sys_user SET status = 'INACTIVE' WHERE status = 'DISABLED' OR status = '0' OR status = 'disabled';

-- 8. 创建索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_sys_user_status_customer ON sys_user (customer_id, status);
CREATE INDEX IF NOT EXISTS idx_sys_user_role_user_customer ON sys_user_role (user_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_sys_user_role_role_customer ON sys_user_role (role_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_sys_role_status_customer ON sys_role (customer_id, status);

-- 9. 插入测试用户数据（仅在开发环境使用）
-- INSERT IGNORE INTO sys_user (id, username, password, real_name, gender, phone, email, department_name, position_name, job_number, status, customer_id, create_time, update_time, deleted)
-- VALUES 
-- ('test_user_001', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctqWFQ4RL5AH6Zk0xtj.Dw.Kq6', '系统管理员', '男', '13800000001', 'admin@example.com', '技术部', '技术总监', 'T001', 'ACTIVE', 'default_customer', NOW(), NOW(), 0),
-- ('test_user_002', 'manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctqWFQ4RL5AH6Zk0xtj.Dw.Kq6', '部门经理', '女', '13800000002', 'manager@example.com', '产品部', '产品经理', 'P001', 'ACTIVE', 'default_customer', NOW(), NOW(), 0);

-- 10. 为测试用户分配角色（仅在开发环境使用）
-- INSERT IGNORE INTO sys_user_role (id, user_id, role_id, customer_id, create_time)
-- VALUES 
-- ('test_user_role_001', 'test_user_001', 'admin_role_001', 'default_customer', NOW()),
-- ('test_user_role_002', 'test_user_002', 'admin_role_002', 'default_customer', NOW());

COMMIT;