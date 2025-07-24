-- Initial RBAC data

-- sys_user
INSERT INTO sys_user (id, username, password, status, email, create_time, customer_id)
VALUES ('U001', 'admin', '$2b$10$DSUjKHGTNxdTHz9WOTQIXuW6c.H4JVhO.syqIQjIG9AAqNUN.cVDi', 'ACTIVE', 'admin@example.com', NOW(), 'DEFAULT');

-- sys_role
INSERT INTO sys_role (id, name, code, create_time, customer_id) VALUES
  ('R001', '管理员', 'ROLE_ADMIN', NOW(), 'DEFAULT'),
  ('R002', '普通用户', 'ROLE_USER', NOW(), 'DEFAULT');

-- sys_permission
INSERT INTO sys_permission (id, name, code, type, path, create_time, customer_id) VALUES
  ('P001', '用户管理', 'user:menu', 'MENU', '/user-management', NOW(), 'DEFAULT'),
  ('P002', '新增用户按钮', 'user:add', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P003', '删除用户按钮', 'user:delete', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P004', '房屋管理', 'house:menu', 'MENU', '/house-management', NOW(), 'DEFAULT'),
  ('P005', '新增房屋按钮', 'house:add', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P006', '编辑房屋按钮', 'house:edit', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P007', '删除房屋按钮', 'house:delete', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P008', '借用审批', 'booking:menu', 'MENU', '/room-booking', NOW(), 'DEFAULT'),
  ('P009', '审批按钮', 'booking:approve', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P010', '导入用户按钮', 'user:import', 'BUTTON', NULL, NOW(), 'DEFAULT'),
  ('P011', '禁用用户按钮', 'user:disable', 'BUTTON', NULL, NOW(), 'DEFAULT');

-- sys_user_role
INSERT INTO sys_user_role (id, user_id, role_id, create_time, customer_id)
VALUES ('UR001', 'U001', 'R001', NOW(), 'DEFAULT');

-- sys_role_permission
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) VALUES
  ('RP001', 'R001', 'P001', NOW(), 'DEFAULT'),
  ('RP002', 'R001', 'P002', NOW(), 'DEFAULT'),
  ('RP003', 'R001', 'P003', NOW(), 'DEFAULT'),
  ('RP004', 'R001', 'P004', NOW(), 'DEFAULT'),
  ('RP005', 'R001', 'P005', NOW(), 'DEFAULT'),
  ('RP006', 'R001', 'P006', NOW(), 'DEFAULT'),
  ('RP007', 'R001', 'P007', NOW(), 'DEFAULT'),
  ('RP008', 'R001', 'P008', NOW(), 'DEFAULT'),
  ('RP009', 'R001', 'P009', NOW(), 'DEFAULT'),
  ('RP010', 'R001', 'P010', NOW(), 'DEFAULT'),
  ('RP011', 'R001', 'P011', NOW(), 'DEFAULT');

