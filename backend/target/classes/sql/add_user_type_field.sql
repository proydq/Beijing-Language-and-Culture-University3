-- 用户表添加用户类型字段
-- 用途：标识用户是教师还是学生，用于预约时自动设置申请人类型
-- 创建时间：2025-01-15

-- 添加用户类型字段
ALTER TABLE sys_user 
ADD COLUMN user_type VARCHAR(10) DEFAULT '教师' 
COMMENT '用户类型：教师/学生，用于预约时标识申请人身份';

-- 验证字段添加结果
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'sys_user' 
  AND COLUMN_NAME = 'user_type';

-- 检查当前数据状态
SELECT 
    user_type,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sys_user WHERE deleted = 0), 2) as percentage
FROM sys_user 
WHERE deleted = 0
GROUP BY user_type;