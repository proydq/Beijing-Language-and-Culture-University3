-- 创建增强的文件信息表（如果不存在）
CREATE TABLE IF NOT EXISTS sys_file_enhanced (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键UUID',
    original_name VARCHAR(255) COMMENT '原始文件名',
    stored_name VARCHAR(255) COMMENT '存储文件名',
    file_hash VARCHAR(64) UNIQUE COMMENT '文件哈希值(SHA256)',
    file_size BIGINT COMMENT '文件大小（字节）',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    extension VARCHAR(20) COMMENT '文件扩展名',
    relative_path VARCHAR(500) COMMENT '相对路径',
    absolute_path VARCHAR(500) COMMENT '绝对路径',
    access_url VARCHAR(500) COMMENT '访问URL',
    reference_count INT DEFAULT 0 COMMENT '引用计数',
    upload_user_id VARCHAR(32) COMMENT '上传用户ID',
    upload_time DATETIME COMMENT '上传时间',
    last_access_time DATETIME COMMENT '最后访问时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    file_category VARCHAR(50) COMMENT '文件分类(avatar/face/document等)',
    description VARCHAR(500) COMMENT '文件描述',
    INDEX idx_file_hash (file_hash),
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_upload_time (upload_time),
    INDEX idx_category (file_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='增强文件信息表';

-- 创建文件引用关系表（如果不存在）
CREATE TABLE IF NOT EXISTS sys_file_reference (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键UUID',
    file_id VARCHAR(32) NOT NULL COMMENT '文件ID',
    entity_type VARCHAR(50) NOT NULL COMMENT '引用实体类型(USER/PRODUCT等)',
    entity_id VARCHAR(32) NOT NULL COMMENT '引用实体ID',
    field_name VARCHAR(50) COMMENT '引用字段名(avatar/faceImage等)',
    create_time DATETIME COMMENT '创建时间',
    create_user_id VARCHAR(32) COMMENT '创建用户ID',
    INDEX idx_file_id (file_id),
    INDEX idx_entity (entity_type, entity_id),
    UNIQUE KEY uk_file_entity_field (file_id, entity_type, entity_id, field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件引用关系表';

INSERT IGNORE INTO sys_user(id, username, password, status, create_time, update_time, deleted)
VALUES ('11111111111111111111111111111111', 'admin', '$2a$10$N8VIj/TvXvFqBqXLfMd4xO7DzTfxPGNDrjBdUJzE8W7qCGc7lKw.G', 'NORMAL', NOW(), NOW(), 0);

INSERT IGNORE INTO sys_role(id, name, code)
VALUES ('22222222222222222222222222222222', '管理员', 'ROLE_USER');

INSERT IGNORE INTO sys_user_role(id, user_id, role_id)
VALUES ('33333333333333333333333333333333', '11111111111111111111111111111111', '22222222222222222222222222222222');
