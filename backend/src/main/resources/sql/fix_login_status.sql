-- 修复登录状态问题的SQL脚本
-- 检查和更新用户状态字段以确保登录正常

-- 1. 查看当前用户状态分布
SELECT 'Current user status distribution:' as message;
SELECT status, COUNT(*) as count FROM sys_user WHERE deleted = 0 GROUP BY status;

-- 2. 显示具体的用户状态值
SELECT 'Detailed user status values:' as message;
SELECT id, username, real_name, status, create_time FROM sys_user WHERE deleted = 0 LIMIT 10;

-- 3. 更新所有可能的旧状态值到新状态
SELECT 'Updating user status to new format...' as message;

-- 更新正常状态的用户
UPDATE sys_user SET status = 'ACTIVE' WHERE 
    (status = 'NORMAL' OR status = '1' OR status = 'enabled' OR status = 'ENABLE' OR status = 'normal')
    AND deleted = 0;

-- 更新禁用状态的用户  
UPDATE sys_user SET status = 'INACTIVE' WHERE 
    (status = 'DISABLED' OR status = '0' OR status = 'disabled' OR status = 'DISABLE' OR status = 'disable')
    AND deleted = 0;

-- 更新NULL状态的用户为ACTIVE（默认活跃状态）
UPDATE sys_user SET status = 'ACTIVE' WHERE status IS NULL AND deleted = 0;

-- 更新空字符串状态的用户为ACTIVE
UPDATE sys_user SET status = 'ACTIVE' WHERE (status = '' OR TRIM(status) = '') AND deleted = 0;

-- 4. 验证更新后的状态分布
SELECT 'Updated user status distribution:' as message;
SELECT status, COUNT(*) as count FROM sys_user WHERE deleted = 0 GROUP BY status;

-- 5. 检查是否还有非标准状态值
SELECT 'Non-standard status values (should be empty):' as message;
SELECT DISTINCT status FROM sys_user 
WHERE status NOT IN ('ACTIVE', 'INACTIVE') AND deleted = 0;

-- 6. 显示几个示例用户的最终状态
SELECT 'Sample users after update:' as message;
SELECT id, username, real_name, status, update_time FROM sys_user WHERE deleted = 0 LIMIT 5;

COMMIT;