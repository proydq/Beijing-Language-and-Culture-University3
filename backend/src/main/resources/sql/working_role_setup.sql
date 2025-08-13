-- 工作版本：正确配置角色权限（包含所需的ID字段）

-- 1. 给超级管理员角色(role001)分配所有权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time) 
SELECT 
    CONCAT('rp_', REPLACE(UUID(),'-','')),
    'role001',
    p.id,
    'customer001',
    NOW()
FROM sys_permission p
WHERE p.deleted = 0;

-- 2. 创建区域查看员角色
INSERT IGNORE INTO sys_role (id, name, code, remark, customer_id, create_time, update_time, deleted) 
VALUES ('area_viewer_role', '区域查看员', 'AREA_VIEWER', '只能查看区域信息', 'customer001', NOW(), NOW(), 0);

-- 3. 给区域查看员分配区域查看权限
INSERT INTO sys_role_permission (id, role_id, permission_id, customer_id, create_time)
SELECT 
    CONCAT('rp_area_view_', REPLACE(UUID(),'-','')),
    'area_viewer_role',
    p.id,
    'customer001',
    NOW()
FROM sys_permission p
WHERE p.code = 'building:area:view' AND p.deleted = 0;

-- 4. 创建测试用户
INSERT IGNORE INTO sys_user (id, username, real_name, password, customer_id, create_time, update_time, deleted)
VALUES ('test_user_id', 'testuser', '测试用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdYMvFOk2rqIIHJpeSaggE1L8Ri', 'customer001', NOW(), NOW(), 0);

-- 5. 给测试用户分配区域查看员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
VALUES ('test_user_id', 'area_viewer_role');

-- 查看结果
SELECT 'Configuration completed successfully!' as status;

-- 统计结果
SELECT 
    r.name as role_name,
    r.code as role_code,
    COUNT(rp.permission_id) as permission_count
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
WHERE r.deleted = 0
GROUP BY r.id, r.name, r.code
HAVING permission_count > 0
ORDER BY permission_count DESC;