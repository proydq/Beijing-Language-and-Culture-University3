-- =====================================================
-- 权限系统升级脚本 - Step 1: 数据库结构升级
-- =====================================================

-- 1. 扩展权限表，添加新字段
ALTER TABLE sys_permission
ADD COLUMN IF NOT EXISTS perm_code VARCHAR(100) COMMENT '新规范权限码 domain:resource:action',
ADD COLUMN IF NOT EXISTS description VARCHAR(500) COMMENT '权限描述',
ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
ADD INDEX idx_perm_code (perm_code);

-- 2. 创建权限别名表（兼容旧权限码）
CREATE TABLE IF NOT EXISTS sys_permission_alias (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    old_code VARCHAR(100) NOT NULL COMMENT '旧权限码如 ROOM_ADD',
    new_code VARCHAR(100) NOT NULL COMMENT '新权限码如 building:room:create',
    description VARCHAR(200) COMMENT '映射说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_old_code (old_code),
    KEY idx_new_code (new_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限码映射表';

-- 3. 创建URL权限映射表
CREATE TABLE IF NOT EXISTS sys_permission_url (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    http_method VARCHAR(10) NOT NULL COMMENT 'GET/POST/PUT/DELETE/PATCH',
    url_pattern VARCHAR(200) NOT NULL COMMENT 'Ant风格URL模式 如 /api/room/**',
    perm_code VARCHAR(100) NOT NULL COMMENT '所需权限码',
    description VARCHAR(200) COMMENT '接口描述',
    module VARCHAR(50) COMMENT '所属模块',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_method_url (http_method, url_pattern),
    KEY idx_perm_code (perm_code),
    KEY idx_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='URL权限映射表';

-- 4. 创建审计日志表
CREATE TABLE IF NOT EXISTS sys_audit_log (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    trace_id VARCHAR(36) COMMENT '追踪ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    real_name VARCHAR(50) COMMENT '真实姓名',
    action VARCHAR(50) COMMENT '操作类型 如 CREATE/UPDATE/DELETE/LOGIN',
    module VARCHAR(50) COMMENT '模块 如 USER/ROOM/BOOKING',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id VARCHAR(100) COMMENT '资源ID',
    resource_name VARCHAR(200) COMMENT '资源名称',
    perm_code VARCHAR(100) COMMENT '使用的权限码',
    success TINYINT COMMENT '是否成功 1成功 0失败',
    error_code VARCHAR(50) COMMENT '错误代码',
    error_msg TEXT COMMENT '错误信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数(脱敏)',
    response_data TEXT COMMENT '响应数据(脱敏)',
    request_time DATETIME COMMENT '请求时间',
    response_time DATETIME COMMENT '响应时间',
    duration BIGINT COMMENT '耗时毫秒',
    customer_id VARCHAR(50) COMMENT '租户ID',
    KEY idx_user_time (user_id, request_time),
    KEY idx_action (action),
    KEY idx_module (module),
    KEY idx_perm_code (perm_code),
    KEY idx_trace_id (trace_id),
    KEY idx_customer_time (customer_id, request_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 5. 创建权限变更历史表
CREATE TABLE IF NOT EXISTS sys_permission_history (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    role_id VARCHAR(36) NOT NULL COMMENT '角色ID',
    role_name VARCHAR(100) COMMENT '角色名称',
    change_type VARCHAR(20) COMMENT '变更类型 ADD/REMOVE/UPDATE',
    permissions_before TEXT COMMENT '变更前权限',
    permissions_after TEXT COMMENT '变更后权限',
    changed_by VARCHAR(36) COMMENT '操作人ID',
    changed_by_name VARCHAR(50) COMMENT '操作人姓名',
    change_reason VARCHAR(500) COMMENT '变更原因',
    change_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_role_time (role_id, change_time),
    KEY idx_changed_by (changed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限变更历史表';

-- 6. 扩展角色表，添加数据域字段
ALTER TABLE sys_role
ADD COLUMN IF NOT EXISTS data_scope VARCHAR(30) DEFAULT 'ALL' COMMENT '数据权限范围 ALL/DEPT/DEPT_AND_CHILDREN/SELF/CUSTOM',
ADD COLUMN IF NOT EXISTS data_scope_dept_ids TEXT COMMENT '自定义数据权限部门ID列表(逗号分隔)',
ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用';

-- 7. 创建权限缓存刷新记录表
CREATE TABLE IF NOT EXISTS sys_permission_refresh_log (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    refresh_type VARCHAR(20) COMMENT '刷新类型 MANUAL/AUTO/SCHEDULED',
    refresh_scope VARCHAR(20) COMMENT '刷新范围 ALL/ROLE/USER',
    target_id VARCHAR(36) COMMENT '目标ID(角色ID或用户ID)',
    refresh_by VARCHAR(36) COMMENT '刷新操作人',
    refresh_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    success TINYINT COMMENT '是否成功',
    message VARCHAR(500) COMMENT '结果信息',
    KEY idx_refresh_time (refresh_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限缓存刷新记录';

-- 输出升级完成信息
SELECT '数据库结构升级完成！' as message;
SELECT '- 扩展了sys_permission表' as step1;
SELECT '- 创建了sys_permission_alias权限别名表' as step2;
SELECT '- 创建了sys_permission_url URL映射表' as step3;
SELECT '- 创建了sys_audit_log审计日志表' as step4;
SELECT '- 创建了sys_permission_history变更历史表' as step5;
SELECT '- 扩展了sys_role表数据域字段' as step6;
SELECT '- 创建了sys_permission_refresh_log刷新记录表' as step7;