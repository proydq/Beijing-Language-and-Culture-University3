-- 教室审批配置表
CREATE TABLE IF NOT EXISTS tb_classroom_approval_config (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    cstm_id VARCHAR(36) NOT NULL COMMENT '客户域ID',
    room_id VARCHAR(36) NOT NULL COMMENT '房间ID',
    allow_booker_select_approver TINYINT(1) DEFAULT 1 COMMENT '是否允许预约人自选审批人',
    need_approval TINYINT(1) DEFAULT 0 COMMENT '是否需要审批',
    create_time BIGINT COMMENT '创建时间',
    last_update_time BIGINT COMMENT '最后更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_cstm_id (cstm_id),
    UNIQUE KEY uk_room_cstm (room_id, cstm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室审批配置表';

-- 审批级别配置表
CREATE TABLE IF NOT EXISTS tb_classroom_approval_level (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    cstm_id VARCHAR(36) NOT NULL COMMENT '客户域ID',
    config_id VARCHAR(36) NOT NULL COMMENT '审批配置ID',
    level_number INT NOT NULL COMMENT '审批级别（1,2,3）',
    approver_ids TEXT COMMENT '审批人ID列表（JSON格式）',
    approver_names TEXT COMMENT '审批人姓名列表（逗号分隔）',
    create_time BIGINT COMMENT '创建时间',
    INDEX idx_config_id (config_id),
    INDEX idx_cstm_id (cstm_id),
    INDEX idx_level_number (level_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批级别配置表';

-- 添加外键约束（可选）
-- ALTER TABLE tb_classroom_approval_config ADD CONSTRAINT fk_config_room FOREIGN KEY (room_id) REFERENCES tb_room(id) ON DELETE CASCADE;
-- ALTER TABLE tb_classroom_approval_level ADD CONSTRAINT fk_level_config FOREIGN KEY (config_id) REFERENCES tb_classroom_approval_config(id) ON DELETE CASCADE;