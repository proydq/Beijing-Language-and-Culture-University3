-- 紧急修复用户状态枚举值的SQL脚本
-- 这个脚本必须在应用启动之前执行

-- 显示修复前的状态
SELECT '=== 修复前的用户状态分布 ===' as message;
SELECT status, COUNT(*) as user_count FROM sys_user GROUP BY status;

-- 显示具体的问题数据
SELECT '=== 包含旧状态值的用户 ===' as message;
SELECT id, username, real_name, status, create_time 
FROM sys_user 
WHERE status IN ('NORMAL', 'DISABLED') OR status IS NULL OR status = ''
LIMIT 10;

-- 开始修复
SELECT '=== 开始修复用户状态值 ===' as message;

-- 修复 NORMAL -> ACTIVE
UPDATE sys_user SET status = 'ACTIVE' WHERE status = 'NORMAL';
SELECT CONCAT('修复 NORMAL -> ACTIVE，影响行数: ', ROW_COUNT()) as result;

-- 修复 DISABLED -> INACTIVE  
UPDATE sys_user SET status = 'INACTIVE' WHERE status = 'DISABLED';
SELECT CONCAT('修复 DISABLED -> INACTIVE，影响行数: ', ROW_COUNT()) as result;

-- 修复 NULL -> ACTIVE
UPDATE sys_user SET status = 'ACTIVE' WHERE status IS NULL;
SELECT CONCAT('修复 NULL -> ACTIVE，影响行数: ', ROW_COUNT()) as result;

-- 修复空字符串 -> ACTIVE
UPDATE sys_user SET status = 'ACTIVE' WHERE status = '';
SELECT CONCAT('修复空字符串 -> ACTIVE，影响行数: ', ROW_COUNT()) as result;

-- 修复其他可能的旧值
UPDATE sys_user SET status = 'ACTIVE' WHERE status IN ('1', 'enabled', 'ENABLE', 'normal');
SELECT CONCAT('修复其他旧值 -> ACTIVE，影响行数: ', ROW_COUNT()) as result;

UPDATE sys_user SET status = 'INACTIVE' WHERE status IN ('0', 'disabled', 'DISABLE', 'disable');
SELECT CONCAT('修复其他旧值 -> INACTIVE，影响行数: ', ROW_COUNT()) as result;

-- 显示修复后的状态分布
SELECT '=== 修复后的用户状态分布 ===' as message;
SELECT status, COUNT(*) as user_count FROM sys_user GROUP BY status;

-- 检查是否还有非标准值
SELECT '=== 检查是否还有非标准状态值 ===' as message;
SELECT DISTINCT status FROM sys_user WHERE status NOT IN ('ACTIVE', 'INACTIVE');

-- 显示修复成功的用户示例
SELECT '=== 修复成功的用户示例 ===' as message;
SELECT id, username, real_name, status, update_time 
FROM sys_user 
WHERE status IN ('ACTIVE', 'INACTIVE')
ORDER BY update_time DESC
LIMIT 5;

SELECT '=== 用户状态修复完成！现在可以安全启动应用 ===' as message;

COMMIT;