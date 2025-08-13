-- 完成权限系统升级：将所有权限代码更新为新格式

-- Step 1: 使用已有的alias表更新权限代码
UPDATE sys_permission p
JOIN sys_permission_alias a ON p.code = a.old_code
SET p.code = a.new_code
WHERE p.deleted = 0;

-- Step 2: 验证更新结果
SELECT COUNT(*) as updated_count FROM sys_permission WHERE code LIKE '%:%';
SELECT '权限代码已更新为新格式' as message;

-- Step 3: 查看更新后的权限代码
SELECT code, name FROM sys_permission WHERE deleted = 0 ORDER BY code LIMIT 20;