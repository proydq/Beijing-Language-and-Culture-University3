# 预约时间控制权限测试指南

## 1. 权限初始化

### 执行SQL脚本
请在数据库客户端中执行以下步骤：

1. 连接数据库：
   - 主机：127.0.0.1:12100
   - 用户名：bunny
   - 密码：bunny
   - 数据库：bunny_bs_v4.3.0_cumt6

2. 执行权限初始化脚本：
```sql
-- 添加权限
INSERT IGNORE INTO sys_permission (
    id, code, name, type, parent_id, url, sort, 
    customer_id, deleted, create_time, update_time
) VALUES 
('perm_booking_time_config_view', 'booking:time-config:view', '查看预约时间配置', 'BUTTON', 'perm031', '/api/room-booking/booking-time-config', 100, 'demo', 0, NOW(), NOW()),
('perm_booking_time_validate', 'booking:time-config:validate', '验证预约时间', 'BUTTON', 'perm031', '/api/room-booking/validate-booking-time', 101, 'demo', 0, NOW(), NOW()),
('perm_booking_time_info', 'booking:time-info:view', '查看当前时间信息', 'BUTTON', 'perm031', '/api/room-booking/current-time-info', 102, 'demo', 0, NOW(), NOW());
```

3. 查看现有角色：
```sql
SELECT id, name FROM sys_role WHERE deleted = 0;
```

4. 为角色分配权限（根据实际角色ID调整）：
```sql
-- 替换 'YOUR_ROLE_ID' 为实际的角色ID
INSERT IGNORE INTO sys_role_permission (id, role_id, permission_id, create_time, customer_id) 
VALUES 
('rp_admin_time_config_view', 'YOUR_ROLE_ID', 'perm_booking_time_config_view', NOW(), 'demo'),
('rp_admin_time_validate', 'YOUR_ROLE_ID', 'perm_booking_time_validate', NOW(), 'demo'),
('rp_admin_time_info', 'YOUR_ROLE_ID', 'perm_booking_time_info', NOW(), 'demo');
```

## 2. 权限验证

### 检查权限是否创建成功
```sql
SELECT id, code, name, type, url 
FROM sys_permission 
WHERE code IN ('booking:time-config:view', 'booking:time-config:validate', 'booking:time-info:view') 
AND deleted = 0;
```

### 检查角色权限关联
```sql
SELECT 
    r.name as role_name,
    p.code as permission_code,
    p.name as permission_name
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE p.code IN ('booking:time-config:view', 'booking:time-config:validate', 'booking:time-info:view')
AND r.deleted = 0 AND p.deleted = 0;
```

## 3. API权限测试

### 测试接口访问权限

1. **获取预约时间配置**
   - URL: `GET /api/room-booking/booking-time-config?roomId=test`
   - 需要权限：`booking:time-config:view` 或 `booking:booking:view` 或 `booking:booking:manage`

2. **验证预约时间**
   - URL: `POST /api/room-booking/validate-booking-time`
   - 需要权限：`booking:time-config:validate` 或 `booking:booking:create` 或 `booking:booking:manage`
   - 请求体：
   ```json
   {
     "roomId": "test",
     "bookingStartTime": "2025-08-15 09:00:00",
     "bookingEndTime": "2025-08-15 17:00:00"
   }
   ```

3. **获取当前时间信息**
   - URL: `GET /api/room-booking/current-time-info`
   - 需要权限：`booking:time-info:view` 或 `booking:booking:view` 或 `booking:booking:manage`

### 测试步骤

1. **有权限用户测试**：
   - 使用有相应权限的用户登录
   - 调用上述API，应该返回正常数据

2. **无权限用户测试**：
   - 使用没有相应权限的用户登录
   - 调用上述API，应该返回403 Forbidden

3. **未登录测试**：
   - 不带认证token调用API
   - 应该返回401 Unauthorized

## 4. 权限配置说明

### 权限层级设计
- **父权限**：`booking:booking:manage` - 预约管理（最高权限）
- **子权限**：
  - `booking:time-config:view` - 查看预约时间配置
  - `booking:time-config:validate` - 验证预约时间
  - `booking:time-info:view` - 查看当前时间信息

### 权限兼容性
新权限与现有预约权限兼容：
- 有 `booking:booking:manage` 的用户自动拥有所有新权限
- 有 `booking:booking:view` 的用户可以查看配置和时间信息
- 有 `booking:booking:create` 的用户可以进行时间验证

## 5. 故障排除

如果权限不生效，检查：
1. 权限是否正确添加到数据库
2. 用户角色是否正确分配了权限
3. Spring Security配置是否正确
4. 用户token是否有效
5. 控制器注解是否正确