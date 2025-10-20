-- init_schema.sql

-- Table: sys_user
CREATE TABLE IF NOT EXISTS sys_user (
  id VARCHAR(32) NOT NULL COMMENT 'UUID 主键',
  username VARCHAR(50) NOT NULL COMMENT '登录用户名',
  password VARCHAR(100) NOT NULL COMMENT '登录密码（加密）',
  real_name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  status VARCHAR(10) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL / DISABLED',
  customer_id VARCHAR(32) DEFAULT NULL COMMENT '客户 ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- Table: sys_role
CREATE TABLE IF NOT EXISTS sys_role (
  id VARCHAR(32) NOT NULL COMMENT 'UUID 主键',
  name VARCHAR(50) NOT NULL COMMENT '角色名',
  code VARCHAR(50) NOT NULL COMMENT '角色编码（如 admin）',
  customer_id VARCHAR(32) DEFAULT NULL COMMENT '客户 ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- Table: sys_permission
CREATE TABLE IF NOT EXISTS sys_permission (
  id VARCHAR(32) NOT NULL COMMENT 'UUID',
  name VARCHAR(50) NOT NULL COMMENT '权限名称',
  code VARCHAR(100) NOT NULL COMMENT '权限编码',
  type VARCHAR(20) NOT NULL COMMENT 'menu / button',
  parent_id VARCHAR(32) DEFAULT NULL COMMENT '父级 ID',
  url VARCHAR(255) DEFAULT NULL COMMENT '路由地址',
  sort INT DEFAULT NULL COMMENT '排序',
  customer_id VARCHAR(32) DEFAULT NULL COMMENT '客户域',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- Table: sys_user_role
CREATE TABLE IF NOT EXISTS sys_user_role (
  id VARCHAR(32) NOT NULL COMMENT '主键',
  user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
  role_id VARCHAR(32) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- Table: sys_role_permission
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id VARCHAR(32) NOT NULL COMMENT '主键',
  role_id VARCHAR(32) NOT NULL COMMENT '角色ID',
  permission_id VARCHAR(32) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化数据
INSERT INTO sys_user (id, username, password, status, customer_id, create_time, update_time, deleted)
VALUES ('u_10001', 'admin', '$2a$10$oQNdZewkNAsZ3KSzRYvNIuBs2p0cpuFPvtu3Kj13yQ6ZPF12ImJ72', 'NORMAL', 'cstm_default', NOW(), NOW(), 0);

INSERT INTO sys_role (id, name, code, customer_id, create_time)
VALUES ('r_10001', '系统管理员', 'admin', 'cstm_default', NOW());

INSERT INTO sys_permission (id, name, code, type)
VALUES ('p_10001', '用户管理', 'user:manage', 'menu');

INSERT INTO sys_user_role (id, user_id, role_id)
VALUES ('ur_10001', 'u_10001', 'r_10001');

INSERT INTO sys_role_permission (id, role_id, permission_id)
VALUES ('rp_10001', 'r_10001', 'p_10001');

