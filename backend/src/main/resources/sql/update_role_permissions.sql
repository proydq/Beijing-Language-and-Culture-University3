-- 更新角色权限关联

-- 先查看test用户当前拥有的权限
SELECT 
    p.code, 
    p.name 
FROM sys_permission p
JOIN sys_role_permission rp ON p.id = rp.permission_id
WHERE rp.role_id = 'role002' AND p.deleted = 0;

-- 为role002（普通用户）添加更多权限
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) VALUES
-- 教室相关权限
('rp_user_022', 'role002', 'perm022', NOW(), 'demo'),  -- 教室管理菜单
('rp_user_023', 'role002', 'perm023', NOW(), 'demo'),  -- 教室查询
('rp_user_024', 'role002', 'perm024', NOW(), 'demo'),  -- 教室查看
('rp_user_028', 'role002', 'perm028', NOW(), 'demo'),  -- 教室预订
('rp_user_029', 'role002', 'perm029', NOW(), 'demo'),  -- 取消预订
-- 预订相关权限
('rp_user_031', 'role002', 'perm031', NOW(), 'demo'),  -- 预订管理菜单
('rp_user_032', 'role002', 'perm032', NOW(), 'demo'),  -- 预订查询
('rp_user_033', 'role002', 'perm033', NOW(), 'demo'),  -- 预订查看
('rp_user_034', 'role002', 'perm034', NOW(), 'demo'),  -- 创建预订
('rp_user_036', 'role002', 'perm036', NOW(), 'demo'),  -- 取消预订
-- 报表查看权限
('rp_user_043', 'role002', 'perm043', NOW(), 'demo'),  -- 报表查看
-- 文件上传权限
('rp_user_046', 'role002', 'perm046', NOW(), 'demo');  -- 文件上传

-- 为超级管理员角色(role001)分配所有新权限
INSERT INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id)
SELECT 
    CONCAT('rp_admin_new_', SUBSTRING(id, 5)) as id,
    'role001' as role_id,
    id as permission_id,
    NOW() as create_time,
    'demo' as customer_id
FROM sys_permission 
WHERE deleted = 0 
AND customer_id = 'demo'
AND id >= 'perm022'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission 
    WHERE role_id = 'role001' AND permission_id = sys_permission.id
);

-- 验证test用户现在的权限
SELECT DISTINCT
    p.code as permission_code,
    p.name as permission_name,
    p.type as permission_type
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE u.username = 'test' 
AND u.deleted = 0 
AND r.deleted = 0 
AND p.deleted = 0
ORDER BY p.code;

-- 统计各角色的权限数量
SELECT 
    r.name as role_name,
    r.code as role_code,
    COUNT(DISTINCT rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id AND p.deleted = 0
WHERE r.deleted = 0
GROUP BY r.id, r.name, r.code;