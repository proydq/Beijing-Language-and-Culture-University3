-- Reverse permission mapping: Update sys_permission to use old codes
-- This allows the system to work with existing code while preparing for future upgrade

-- First, let's check what permissions we currently have
SELECT code, name FROM sys_permission WHERE deleted = 0;

-- Update sys_permission back to old permission codes based on the alias table
UPDATE sys_permission p
JOIN sys_permission_alias a ON p.code = a.new_code
SET p.code = a.old_code
WHERE p.deleted = 0;

-- Verify the update
SELECT '权限代码已恢复为旧格式' as message;
SELECT code, name FROM sys_permission WHERE deleted = 0 ORDER BY code;