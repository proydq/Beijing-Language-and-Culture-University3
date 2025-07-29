-- 连续预约设置表建表语句
CREATE TABLE IF NOT EXISTS tb_continuous_booking_setting (
    id VARCHAR(32) NOT NULL COMMENT '主键UUID',
    room_id VARCHAR(32) NOT NULL COMMENT '房间ID',
    continuous_days INT DEFAULT 7 COMMENT '可连续预约天数',
    continuous_type VARCHAR(20) DEFAULT 'DAYS' COMMENT '连续类型（DAYS-天数，WEEKS-周数，MONTHS-月数）',
    is_unlimited BOOLEAN DEFAULT FALSE COMMENT '是否无限制',
    can_continuous BOOLEAN DEFAULT TRUE COMMENT '是否允许连续预约',
    cstm_id VARCHAR(32) DEFAULT 'demo' COMMENT '客户域',
    create_time BIGINT COMMENT '创建时间（时间戳）',
    last_update_time BIGINT COMMENT '最后更新时间（时间戳）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_cstm (room_id, cstm_id),
    INDEX idx_cstm_id (cstm_id),
    INDEX idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室连续预约设置表';

-- 插入示例数据
INSERT INTO tb_continuous_booking_setting (id, room_id, continuous_days, continuous_type, is_unlimited, can_continuous, cstm_id, create_time, last_update_time)
VALUES 
('cbs001', 'room001', 7, 'DAYS', FALSE, TRUE, 'demo', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('cbs002', 'room002', 14, 'DAYS', FALSE, TRUE, 'demo', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('cbs003', 'room003', 30, 'DAYS', FALSE, TRUE, 'demo', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('cbs004', 'room004', 0, 'DAYS', TRUE, TRUE, 'demo', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('cbs005', 'room005', 7, 'DAYS', FALSE, FALSE, 'demo', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000)
ON DUPLICATE KEY UPDATE 
    continuous_days = VALUES(continuous_days),
    continuous_type = VALUES(continuous_type),
    is_unlimited = VALUES(is_unlimited),
    can_continuous = VALUES(can_continuous),
    last_update_time = UNIX_TIMESTAMP() * 1000;