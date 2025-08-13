-- =====================================================
-- 管理员设置模块权限验证脚本
-- =====================================================

-- 1. 验证权限是否正确创建
-- =====================================================

-- 检查管理员管理权限总数
SELECT 
    '管理员管理权限总数' as check_item,
    COUNT(*) as count
FROM sys_permission 
WHERE permission_code LIKE 'system:admin:%'
  AND customer_id = 'demo';

-- 详细列出所有管理员管理权限
SELECT 
    permission_code,
    permission_name,
    resource_type,
    resource_url,
    http_method,
    CASE 
        WHEN parent_id IS NULL THEN '顶级权限'
        ELSE (SELECT permission_name FROM sys_permission WHERE id = p.parent_id)
    END as parent_permission
FROM sys_permission p
WHERE permission_code LIKE 'system:admin:%'
  AND customer_id = 'demo'
ORDER BY resource_type DESC, sort_order;

-- 2. 验证角色权限分配情况
-- =====================================================

-- 检查超级管理员的管理员管理权限
SELECT 
    '超级管理员' as role_name,
    COUNT(DISTINCT p.id) as permission_count,
    GROUP_CONCAT(DISTINCT p.permission_code ORDER BY p.permission_code) as permissions
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.role_code = 'SUPER_ADMIN'
  AND r.customer_id = 'demo'
  AND p.permission_code LIKE 'system:admin:%';

-- 检查系统管理员的管理员管理权限
SELECT 
    '系统管理员' as role_name,
    COUNT(DISTINCT p.id) as permission_count,
    GROUP_CONCAT(DISTINCT p.permission_code ORDER BY p.permission_code) as permissions
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.role_code = 'SYSTEM_ADMIN'
  AND r.customer_id = 'demo'
  AND p.permission_code LIKE 'system:admin:%';

-- 检查普通管理员的管理员管理权限
SELECT 
    '普通管理员' as role_name,
    COUNT(DISTINCT p.id) as permission_count,
    GROUP_CONCAT(DISTINCT p.permission_code ORDER BY p.permission_code) as permissions
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.role_code = 'ADMIN'
  AND r.customer_id = 'demo'
  AND p.permission_code LIKE 'system:admin:%';

-- 3. 验证权限URL映射
-- =====================================================

-- 检查每个API端点的权限映射
SELECT 
    '权限URL映射检查' as check_type,
    resource_url,
    http_method,
    GROUP_CONCAT(permission_code) as permission_codes,
    COUNT(*) as permission_count
FROM sys_permission
WHERE resource_url LIKE '/admin-management%'
  AND customer_id = 'demo'
GROUP BY resource_url, http_method
ORDER BY resource_url, http_method;

-- 4. 验证权限别名映射
-- =====================================================

-- 检查管理员管理权限别名
SELECT 
    '权限别名映射' as check_type,
    old_code,
    new_code,
    description
FROM sys_permission_alias
WHERE new_code LIKE 'system:admin:%'
   OR old_code LIKE 'admin:%'
ORDER BY old_code;

-- 5. 检查潜在的权限冲突
-- =====================================================

-- 检查是否有重复的权限代码
SELECT 
    permission_code,
    COUNT(*) as duplicate_count,
    GROUP_CONCAT(id) as permission_ids
FROM sys_permission
WHERE permission_code LIKE 'system:admin:%'
  AND customer_id = 'demo'
GROUP BY permission_code
HAVING COUNT(*) > 1;

-- 检查相同URL的不同权限配置
SELECT 
    resource_url,
    http_method,
    COUNT(DISTINCT permission_code) as different_permissions,
    GROUP_CONCAT(DISTINCT permission_code) as permission_codes
FROM sys_permission
WHERE resource_url LIKE '/admin-management%'
  AND customer_id = 'demo'
GROUP BY resource_url, http_method
HAVING COUNT(DISTINCT permission_code) > 1;

-- 6. 生成测试用户权限检查SQL
-- =====================================================

-- 为测试用户生成权限检查语句
SELECT CONCAT(
    'SELECT u.username, u.real_name, r.role_name, ',
    'COUNT(DISTINCT p.id) as admin_permissions ',
    'FROM sys_user u ',
    'JOIN sys_user_role ur ON u.id = ur.user_id ',
    'JOIN sys_role r ON ur.role_id = r.id ',
    'LEFT JOIN sys_role_permission rp ON r.id = rp.role_id ',
    'LEFT JOIN sys_permission p ON rp.permission_id = p.id AND p.permission_code LIKE ''system:admin:%'' ',
    'WHERE u.username = ''', username, ''' ',
    'AND u.customer_id = ''demo'' ',
    'GROUP BY u.id, u.username, u.real_name, r.role_name;'
) as check_user_permission_sql
FROM sys_user
WHERE status = 'ACTIVE'
  AND customer_id = 'demo'
  AND username IN ('admin', 'admin001', 'system')
LIMIT 3;

-- 7. 验证菜单权限结构
-- =====================================================

-- 显示管理员管理菜单层级结构
WITH RECURSIVE menu_tree AS (
    -- 查找管理员管理菜单
    SELECT 
        id,
        permission_code,
        permission_name,
        resource_type,
        parent_id,
        sort_order,
        0 as level,
        permission_name as path
    FROM sys_permission
    WHERE permission_code = 'system:admin:menu'
      AND customer_id = 'demo'
    
    UNION ALL
    
    -- 递归查找子权限
    SELECT 
        p.id,
        p.permission_code,
        p.permission_name,
        p.resource_type,
        p.parent_id,
        p.sort_order,
        mt.level + 1,
        CONCAT(mt.path, ' > ', p.permission_name)
    FROM sys_permission p
    INNER JOIN menu_tree mt ON p.parent_id = mt.id
    WHERE p.customer_id = 'demo'
)
SELECT 
    REPEAT('  ', level) || permission_name as menu_structure,
    permission_code,
    resource_type
FROM menu_tree
ORDER BY level, sort_order;

-- 8. 总结报告
-- =====================================================

SELECT '===================== 权限验证总结 =====================' as summary;

SELECT CONCAT(
    '✅ 管理员管理权限创建成功: ', 
    (SELECT COUNT(*) FROM sys_permission WHERE permission_code LIKE 'system:admin:%' AND customer_id = 'demo'),
    ' 个权限'
) as status;

SELECT CONCAT(
    '✅ 超级管理员权限分配: ', 
    (SELECT COUNT(DISTINCT p.id) FROM sys_role r 
     JOIN sys_role_permission rp ON r.id = rp.role_id 
     JOIN sys_permission p ON rp.permission_id = p.id 
     WHERE r.role_code = 'SUPER_ADMIN' AND p.permission_code LIKE 'system:admin:%'),
    ' 个权限'
) as super_admin_status;

SELECT CONCAT(
    '✅ 权限URL映射: ', 
    (SELECT COUNT(DISTINCT resource_url) FROM sys_permission WHERE resource_url LIKE '/admin-management%'),
    ' 个端点'
) as url_mapping_status;

SELECT '⚠️ 请重新登录或刷新权限缓存以使新权限生效！' as reminder;