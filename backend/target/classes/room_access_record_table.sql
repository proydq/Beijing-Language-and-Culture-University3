-- 教室出入记录表
CREATE TABLE IF NOT EXISTS `tb_room_access_record` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `cstm_id` varchar(36) NOT NULL COMMENT '客户域ID',
  `room_id` varchar(36) NOT NULL COMMENT '教室ID',
  `room_name` varchar(100) DEFAULT NULL COMMENT '教室名称（冗余）',
  `building_name` varchar(100) DEFAULT NULL COMMENT '楼栋名称（冗余）',
  `floor` varchar(20) DEFAULT NULL COMMENT '楼层（冗余）',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名（冗余）',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别（冗余）',
  `employee_id` varchar(50) DEFAULT NULL COMMENT '工号（冗余）',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系方式（冗余）',
  `department` varchar(100) DEFAULT NULL COMMENT '部门（冗余）',
  `user_type` varchar(20) DEFAULT NULL COMMENT '用户类型',
  `open_method` varchar(20) DEFAULT NULL COMMENT '开门方式',
  `access_time` datetime NOT NULL COMMENT '通行时间',
  `access_type` varchar(20) DEFAULT NULL COMMENT '通行类型',
  `booking_id` varchar(36) DEFAULT NULL COMMENT '预约ID',
  `booking_name` varchar(200) DEFAULT NULL COMMENT '预约名称（冗余）',
  `booking_period` varchar(100) DEFAULT NULL COMMENT '预约时段（冗余）',
  `device_id` varchar(36) DEFAULT NULL COMMENT '门禁设备ID',
  `device_name` varchar(100) DEFAULT NULL COMMENT '门禁设备名称（冗余）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_access_record_cstm_id` (`cstm_id`),
  KEY `idx_room_access_record_room_id` (`room_id`),
  KEY `idx_room_access_record_user_id` (`user_id`),
  KEY `idx_room_access_record_access_time` (`access_time`),
  KEY `idx_room_access_record_booking_id` (`booking_id`),
  KEY `idx_room_access_record_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室出入记录表';

-- 插入示例数据（可选）
INSERT INTO `tb_room_access_record` (`id`, `cstm_id`, `room_id`, `room_name`, `building_name`, `floor`, `user_id`, `name`, `gender`, `employee_id`, `phone`, `department`, `user_type`, `open_method`, `access_time`, `access_type`, `booking_id`, `booking_name`, `booking_period`, `device_id`, `device_name`, `create_time`) VALUES
('access001', 'default_customer', 'room001', 'A-101', '教学楼A', '1F', 'user001', '张三', '男', 'T001', '13800138000', '计算机学院', '教师', '刷卡', '2024-08-04 08:30:00', '预约权限', 'booking001', '计算机基础课程', '08:00-10:00', 'device001', 'A-101门禁', '2024-08-04 08:30:00'),
('access002', 'default_customer', 'room001', 'A-101', '教学楼A', '1F', 'user002', '李四', '女', 'S001', '13900139000', '数学学院', '学生', '人脸识别', '2024-08-04 08:35:00', '预约权限', 'booking001', '计算机基础课程', '08:00-10:00', 'device001', 'A-101门禁', '2024-08-04 08:35:00'),
('access003', 'default_customer', 'room002', 'A-102', '教学楼A', '1F', 'user003', '王五', '男', 'T002', '13700137000', '物理学院', '教师', '刷卡', '2024-08-04 10:15:00', '永久权限', NULL, NULL, NULL, 'device002', 'A-102门禁', '2024-08-04 10:15:00'),
('access004', 'default_customer', 'room003', 'B-201', '教学楼B', '2F', 'user004', '赵六', '女', 'S002', '13600136000', '化学学院', '学生', '按钮', '2024-08-04 14:20:00', '预约权限', 'booking002', '化学实验课', '14:00-16:00', 'device003', 'B-201门禁', '2024-08-04 14:20:00'),
('access005', 'default_customer', 'room001', 'A-101', '教学楼A', '1F', 'user005', '钱七', '男', 'T003', '13500135000', '英语学院', '教师', '人脸识别', '2024-08-04 16:45:00', '预约权限', 'booking003', '英语口语课', '16:30-18:30', 'device001', 'A-101门禁', '2024-08-04 16:45:00');