-- 远程开门记录功能数据库更新脚本
-- 执行日期: 2024-08-05

-- 1. 扩展 tb_room_access_record 表，添加远程开门相关字段
ALTER TABLE tb_room_access_record 
ADD COLUMN operator_id VARCHAR(36) COMMENT '实际操作员ID' AFTER device_name,
ADD COLUMN operator_name VARCHAR(50) COMMENT '实际操作员姓名' AFTER operator_id,
ADD COLUMN operator_type VARCHAR(20) COMMENT '操作员类型' AFTER operator_name,
ADD COLUMN reason VARCHAR(500) COMMENT '开门原因' AFTER operator_type;

-- 2. 创建远程操作日志表
CREATE TABLE tb_remote_operation_log (
    id VARCHAR(36) NOT NULL COMMENT '主键ID',
    operator_id VARCHAR(36) COMMENT '操作员ID',
    operator_name VARCHAR(50) COMMENT '操作员姓名',
    operation_type VARCHAR(20) COMMENT '操作类型',
    room_id VARCHAR(36) COMMENT '教室ID',
    room_name VARCHAR(100) COMMENT '教室名称',
    device_id VARCHAR(36) COMMENT '设备ID',
    reason VARCHAR(500) COMMENT '操作原因',
    result VARCHAR(20) COMMENT '操作结果',
    operation_time DATETIME COMMENT '操作时间',
    ip_address VARCHAR(50) COMMENT '操作IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='远程操作日志表';

-- 3. 为 tb_room_access_record 表添加索引以优化查询性能
CREATE INDEX idx_access_type ON tb_room_access_record (access_type);
CREATE INDEX idx_operator_id ON tb_room_access_record (operator_id);
CREATE INDEX idx_operator_type ON tb_room_access_record (operator_type);
CREATE INDEX idx_access_time_type ON tb_room_access_record (access_time, access_type);

-- 4. 为 tb_remote_operation_log 表添加索引
CREATE INDEX idx_operator_operation ON tb_remote_operation_log (operator_id, operation_time);
CREATE INDEX idx_room_operation ON tb_remote_operation_log (room_id, operation_time);
CREATE INDEX idx_operation_time ON tb_remote_operation_log (operation_time);

-- 5. 插入一些测试数据（可选）
INSERT INTO tb_room_access_record (
    id, cstm_id, room_id, room_name, building_name, floor,
    user_id, name, gender, employee_id, phone, department, user_type,
    open_method, access_time, access_type, device_id, device_name,
    operator_id, operator_name, operator_type, reason, create_time
) VALUES 
(
    UUID(), 'default-cstm', 'room-001', 'A-101', 'A栋教学楼', '1F',
    'user-001', '张三', '男', 'T001', '13800138000', '信息技术部', '教师',
    '管理员授权', NOW() - INTERVAL 2 HOUR, '远程开门', 'device-001', 'A-101门禁设备',
    'admin-001', '管理员', '管理员', '应急开门，钥匙丢失', NOW() - INTERVAL 2 HOUR
),
(
    UUID(), 'default-cstm', 'room-002', 'A-102', 'A栋教学楼', '1F',
    'user-002', '李四', '女', 'T002', '13800138001', '数学系', '教师',
    '系统开门', NOW() - INTERVAL 1 HOUR, '远程开门', 'device-002', 'A-102门禁设备',
    'system', '系统', '系统', '预约时间到达自动开门', NOW() - INTERVAL 1 HOUR
),
(
    UUID(), 'default-cstm', 'room-003', 'B-201', 'B栋实验楼', '2F',
    'user-003', '王五', '男', 'T003', '13800138002', '物理系', '教师',
    '应急开门', NOW() - INTERVAL 30 MINUTE, '远程开门', 'device-003', 'B-201门禁设备',
    'admin-002', '值班管理员', '应急', '火警演练需要', NOW() - INTERVAL 30 MINUTE
);

INSERT INTO tb_remote_operation_log (
    id, operator_id, operator_name, operation_type, room_id, room_name,
    device_id, reason, result, operation_time, ip_address, user_agent, create_time
) VALUES 
(
    UUID(), 'admin-001', '管理员', '开门', 'room-001', 'A-101',
    'device-001', '应急开门，钥匙丢失', '成功', NOW() - INTERVAL 2 HOUR,
    '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', NOW() - INTERVAL 2 HOUR
),
(
    UUID(), 'system', '系统', '开门', 'room-002', 'A-102',
    'device-002', '预约时间到达自动开门', '成功', NOW() - INTERVAL 1 HOUR,
    '127.0.0.1', 'System Auto', NOW() - INTERVAL 1 HOUR
),
(
    UUID(), 'admin-002', '值班管理员', '开门', 'room-003', 'B-201',
    'device-003', '火警演练需要', '成功', NOW() - INTERVAL 30 MINUTE,
    '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', NOW() - INTERVAL 30 MINUTE
);

-- 6. 更新统计信息
ANALYZE TABLE tb_room_access_record;
ANALYZE TABLE tb_remote_operation_log;