-- 删除普通用户不应该有的管理权限

-- 1. 删除审批、数据记录等管理权限
DELETE rp FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
AND p.code IN (
    'BOOKING_APPROVE',      -- 审批预订
    'BOOKING_REJECT',       -- 拒绝预订
    'BOOKING_RECORDS_VIEW', -- 数据记录查看
    'BOOKING_CONFIG',       -- 预订配置
    'BOOKING_EDIT',         -- 编辑预订
    'ROOM_ADD',            -- 添加教室
    'ROOM_DELETE',         -- 删除教室
    'ROOM_EDIT',           -- 编辑教室
    'ROOM_APPROVE',        -- 审批教室
    'ROOM_CANCEL_BOOK'     -- 取消预订（管理员功能）
);

-- 2. 验证删除结果
SELECT '=== 检查是否还有不应该的权限 ===' as title;
SELECT p.code, p.name 
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
AND p.code IN ('BOOKING_APPROVE', 'BOOKING_REJECT', 'BOOKING_RECORDS_VIEW', 'BOOKING_CONFIG');

-- 3. 显示普通用户当前所有权限
SELECT '=== 普通用户当前权限列表 ===' as title;
SELECT p.code, p.name, p.type
FROM sys_role_permission rp
JOIN sys_permission p ON rp.permission_id = p.id
WHERE rp.role_id = 'role_normal'
ORDER BY p.type, p.code;