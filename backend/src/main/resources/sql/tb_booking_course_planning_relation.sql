-- 预约与排课关联关系表
CREATE TABLE IF NOT EXISTS `tb_booking_course_planning_relation` (
  `id` varchar(40) NOT NULL COMMENT '主键id',
  `booking_id` varchar(40) NOT NULL COMMENT '预约ID',
  `course_planning_id` varchar(40) NOT NULL COMMENT '排课记录ID',
  `cstm_id` varchar(40) DEFAULT NULL COMMENT '客户域ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_booking_id` (`booking_id`),
  KEY `idx_course_planning_id` (`course_planning_id`),
  KEY `idx_cstm_id` (`cstm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约与排课关联关系表';

-- 添加外键约束（可选）
-- ALTER TABLE `tb_booking_course_planning_relation` 
-- ADD CONSTRAINT `fk_booking_id` FOREIGN KEY (`booking_id`) REFERENCES `tb_room_booking` (`id`) ON DELETE CASCADE,
-- ADD CONSTRAINT `fk_course_planning_id` FOREIGN KEY (`course_planning_id`) REFERENCES `tb_course_planning` (`id`) ON DELETE CASCADE;