-- 检查和修复用户状态
-- 确保所有用户状态都使用 ACTIVE/INACTIVE 而不是 NORMAL/DISABLED

-- 首先检查当前状态分布
SELECT 
    status, 
    COUNT(*) as count,
    CONCAT(status, ' (', ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sys_user), 2), '%)') as percentage
FROM sys_user 
WHERE deleted = 0 OR deleted IS NULL
GROUP BY status
ORDER BY count DESC;

-- 检查是否有旧的状态值
SELECT '检查是否存在NORMAL状态的用户' as check_type, COUNT(*) as count
FROM sys_user 
WHERE status = 'NORMAL' AND (deleted = 0 OR deleted IS NULL);

SELECT '检查是否存在DISABLED状态的用户' as check_type, COUNT(*) as count
FROM sys_user 
WHERE status = 'DISABLED' AND (deleted = 0 OR deleted IS NULL);

-- 检查是否有null状态
SELECT '检查是否存在NULL状态的用户' as check_type, COUNT(*) as count
FROM sys_user 
WHERE status IS NULL AND (deleted = 0 OR deleted IS NULL);

-- 修复旧状态值
UPDATE sys_user 
SET status = 'ACTIVE', update_time = NOW() 
WHERE status = 'NORMAL' AND (deleted = 0 OR deleted IS NULL);

UPDATE sys_user 
SET status = 'INACTIVE', update_time = NOW() 
WHERE status = 'DISABLED' AND (deleted = 0 OR deleted IS NULL);

-- 修复null状态（默认设为ACTIVE）
UPDATE sys_user 
SET status = 'ACTIVE', update_time = NOW() 
WHERE status IS NULL AND (deleted = 0 OR deleted IS NULL);

-- 修复空字符串状态
UPDATE sys_user 
SET status = 'ACTIVE', update_time = NOW() 
WHERE (status = '' OR status = ' ') AND (deleted = 0 OR deleted IS NULL);

-- 最终检查结果
SELECT 
    '修复后状态分布' as result_type,
    status, 
    COUNT(*) as count
FROM sys_user 
WHERE deleted = 0 OR deleted IS NULL
GROUP BY status
ORDER BY count DESC;

-- 检查是否还有非标准状态值
SELECT 
    '非标准状态值检查' as result_type,
    status,
    COUNT(*) as count
FROM sys_user 
WHERE status NOT IN ('ACTIVE', 'INACTIVE') 
  AND (deleted = 0 OR deleted IS NULL)
GROUP BY status;

SELECT 'Status fix completed' as message;