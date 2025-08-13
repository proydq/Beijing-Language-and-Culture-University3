-- 创建权限别名表（用于新旧权限码映射）
CREATE TABLE IF NOT EXISTS sys_permission_alias (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    old_code VARCHAR(100) NOT NULL COMMENT '旧权限码',
    new_code VARCHAR(100) NOT NULL COMMENT '新权限码',
    description VARCHAR(200) COMMENT '描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_old_code (old_code),
    KEY idx_new_code (new_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限别名映射表';

-- 插入映射数据
INSERT INTO sys_permission_alias (old_code, new_code, description) VALUES
-- 用户管理
('USER_ADD', 'system:user:create', '创建用户'),
('USER_VIEW', 'system:user:read', '查看用户'),
('USER_EDIT', 'system:user:update', '编辑用户'),
('USER_DELETE', 'system:user:delete', '删除用户'),
('USER_MANAGE', 'system:user:manage', '用户管理'),

-- 角色管理
('ROLE_ADD', 'system:role:create', '创建角色'),
('ROLE_VIEW', 'system:role:read', '查看角色'),
('ROLE_EDIT', 'system:role:update', '编辑角色'),
('ROLE_DELETE', 'system:role:delete', '删除角色'),
('ROLE_MANAGE', 'system:role:manage', '角色管理'),

-- 房间管理
('ROOM_ADD', 'building:room:create', '创建房间'),
('ROOM_VIEW', 'building:room:read', '查看房间'),
('ROOM_EDIT', 'building:room:update', '编辑房间'),
('ROOM_DELETE', 'building:room:delete', '删除房间'),
('ROOM_MANAGE', 'building:room:manage', '房间管理'),

-- 区域管理
('AREA_ADD', 'building:area:create', '创建区域'),
('AREA_VIEW', 'building:area:read', '查看区域'),
('AREA_EDIT', 'building:area:update', '编辑区域'),
('AREA_DELETE', 'building:area:delete', '删除区域'),
('AREA_MANAGE', 'building:area:manage', '区域管理'),

-- 预约管理
('BOOKING_ADD', 'booking:booking:create', '创建预约'),
('BOOKING_VIEW', 'booking:booking:read', '查看预约'),
('BOOKING_EDIT', 'booking:booking:update', '编辑预约'),
('BOOKING_DELETE', 'booking:booking:delete', '删除预约'),
('BOOKING_MANAGE', 'booking:booking:manage', '预约管理'),
('BOOKING_APPROVE', 'booking:booking:approve', '审批预约'),
('BOOKING_REJECT', 'booking:booking:reject', '拒绝预约'),

-- 其他权限
('SYSTEM_MANAGE', 'system:system:manage', '系统管理'),
('PERMISSION_MANAGE', 'system:permission:manage', '权限管理'),
('FILE_UPLOAD', 'system:file:upload', '文件上传');