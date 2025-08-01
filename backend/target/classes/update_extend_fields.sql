-- 更新RoomBooking表的extend1和extend2字段类型
-- 从VARCHAR(255)改为TEXT以支持更长的JSON数据

ALTER TABLE tb_room_booking 
MODIFY COLUMN extend1 TEXT COMMENT '扩展字段1（参与人详情JSON）';

ALTER TABLE tb_room_booking 
MODIFY COLUMN extend2 TEXT COMMENT '扩展字段2（审批人详情JSON）';