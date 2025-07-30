# SchemeManagement 接口测试文档

## 接口概览

本文档描述了 SchemeManagement.vue 页面所需的后端接口的使用方法和测试示例。

## 基础信息

- **基础URL**: `http://localhost:8080/scheme-management`
- **认证方式**: Bearer Token（需要在请求头中添加 `Authorization: Bearer {token}`）
- **内容类型**: `application/json`

## 接口列表

### 1. 获取教室列表

**接口地址**: `POST /scheme-management/classrooms/list`

**请求示例**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "roomName": "教室",
  "approvalFilter": "all",
  "roomAreaId": "area-001"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 201,
    "rows": [
      {
        "id": "room-001",
        "roomName": "教学楼A-101",
        "roomNo": "101",
        "roomAreaName": "教学楼A",
        "isExchange": "yes",
        "needApproval": "yes",
        "firstApprover": "张三,李四",
        "secondApprover": "王五",
        "thirdApprover": "",
        "roomCode": "A101",
        "roomVolume": 50,
        "remark": "多媒体教室",
        "allowBookerSelectApprover": "yes",
        "lastUpdateTime": "2024-01-01 10:30:00"
      }
    ]
  }
}
```

**cURL 测试命令**:
```bash
curl -X POST "http://localhost:8080/scheme-management/classrooms/list" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "pageNumber": 1,
    "pageSize": 10,
    "roomName": "教室",
    "approvalFilter": "all"
  }'
```

### 2. 添加教室

**接口地址**: `POST /scheme-management/classrooms/add`

**请求示例**:
```json
{
  "classroomName": "教学楼B-201",
  "roomNumber": "201",
  "classroomCode": "B201",
  "building": "教学楼B",
  "roomAreaId": "area-002",
  "remarks": "新建多媒体教室",
  "roomVolume": 60,
  "roomArea": "80.5",
  "allowBookerSelectApprover": "yes",
  "approvalLevels": [
    {
      "level": 1,
      "approvers": "张三,李四",
      "approverIds": "user-001,user-002"
    },
    {
      "level": 2,
      "approvers": "王五",
      "approverIds": "user-003"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": "room-new-001"
  }
}
```

**cURL 测试命令**:
```bash
curl -X POST "http://localhost:8080/scheme-management/classrooms/add" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "classroomName": "教学楼B-201",
    "roomNumber": "201",
    "classroomCode": "B201",
    "building": "教学楼B",
    "roomAreaId": "area-002",
    "remarks": "新建多媒体教室",
    "roomVolume": 60,
    "allowBookerSelectApprover": "yes",
    "approvalLevels": [
      {
        "level": 1,
        "approvers": "张三,李四",
        "approverIds": "user-001,user-002"
      }
    ]
  }'
```

### 3. 更新教室信息

**接口地址**: `PUT /scheme-management/classrooms/update`

**请求示例**:
```json
{
  "id": "room-001",
  "classroomName": "教学楼A-101（更新）",
  "roomNumber": "101",
  "building": "教学楼A",
  "roomAreaId": "area-001",
  "allowBookerSelectApprover": "no",
  "approvalLevels": [
    {
      "level": 1,
      "approvers": "张三",
      "approverIds": "user-001"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 4. 批量删除教室

**接口地址**: `DELETE /scheme-management/classrooms/batch-delete`

**请求示例**:
```json
{
  "ids": ["room-001", "room-002", "room-003"]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

**cURL 测试命令**:
```bash
curl -X DELETE "http://localhost:8080/scheme-management/classrooms/batch-delete" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "ids": ["room-001", "room-002"]
  }'
```

### 5. 批量设置审批权限

**接口地址**: `PUT /scheme-management/classrooms/batch-approval`

**请求示例**:
```json
{
  "roomIds": ["room-001", "room-002"],
  "allowBookerSelectApprover": "yes",
  "approvalLevels": [
    {
      "level": 1,
      "approvers": "张三,李四",
      "approverIds": "user-001,user-002"
    },
    {
      "level": 2,
      "approvers": "王五",
      "approverIds": "user-003"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量设置成功"
}
```

### 6. 手动同步教室数据

**接口地址**: `POST /scheme-management/classrooms/sync`

**请求示例**: 无需请求体

**响应示例**:
```json
{
  "code": 200,
  "message": "同步成功",
  "data": {
    "syncCount": 5
  }
}
```

**cURL 测试命令**:
```bash
curl -X POST "http://localhost:8080/scheme-management/classrooms/sync" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 7. 获取人员列表

**接口地址**: `POST /scheme-management/personnel/list`

**请求示例**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "name": "张",
  "department": "教务处"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": "user-001",
        "name": "张三",
        "department": "教务处",
        "position": "主任",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "employeeNo": "E001",
        "username": "zhangsan"
      }
    ]
  }
}
```

### 8. 导出教室数据

**接口地址**: `POST /scheme-management/classrooms/export`

**请求示例**:
```json
{
  "roomIds": ["room-001", "room-002"]
}
```

**响应**: Excel文件下载流

### 9. 导入教室数据

**接口地址**: `POST /scheme-management/classrooms/import`

**请求**: FormData格式，包含Excel文件

**响应示例**:
```json
{
  "code": 200,
  "message": "导入成功",
  "data": {
    "successCount": 10,
    "failCount": 0,
    "errors": []
  }
}
```

**cURL 测试命令**:
```bash
curl -X POST "http://localhost:8080/scheme-management/classrooms/import" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@classroom_data.xlsx"
```

## 错误响应格式

当接口调用失败时，会返回以下格式的错误响应：

```json
{
  "code": 400,
  "message": "错误描述信息",
  "data": null
}
```

常见错误码：
- `400`: 请求参数错误
- `401`: 未授权访问
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 测试流程建议

### 1. 基础测试流程

1. **获取人员列表** - 确保有可用的审批人
2. **手动同步教室数据** - 从Room表同步现有教室
3. **获取教室列表** - 查看同步后的教室数据
4. **添加新教室** - 测试添加功能
5. **更新教室信息** - 测试更新功能
6. **批量设置审批权限** - 测试批量操作
7. **批量删除教室** - 测试删除功能

### 2. 数据准备

在测试前，请确保：

1. **Room表中有教室数据**:
```sql
INSERT INTO tb_room (id, cstm_id, room_name, room_no, room_type_name, room_area_id, room_area_name, create_time, last_update_time)
VALUES 
('test-room-001', 'your-customer-id', '测试教室1', '101', '教室', 'area-001', '教学楼A', 1704067200000, 1704067200000),
('test-room-002', 'your-customer-id', '测试教室2', '102', '教室', 'area-001', '教学楼A', 1704067200000, 1704067200000);
```

2. **SysUser表中有用户数据**:
```sql
INSERT INTO sys_user (id, username, real_name, department_name, position_name, customer_id, deleted, create_time)
VALUES 
('test-user-001', 'zhangsan', '张三', '教务处', '主任', 'your-customer-id', 0, NOW()),
('test-user-002', 'lisi', '李四', '教务处', '副主任', 'your-customer-id', 0, NOW());
```

### 3. 环境变量

测试时需要替换以下变量：
- `YOUR_TOKEN`: 有效的JWT令牌
- `your-customer-id`: 实际的客户域ID
- `localhost:8080`: 实际的服务器地址和端口

## 注意事项

1. **权限控制**: 所有接口都需要有效的认证令牌
2. **客户域隔离**: 数据会根据当前用户的客户域ID进行隔离
3. **事务处理**: 添加、更新、删除操作都使用了事务，确保数据一致性
4. **数据验证**: 请求参数会进行严格的验证，确保数据完整性
5. **错误处理**: 接口会返回详细的错误信息，便于调试

## 前端集成建议

1. **统一错误处理**: 建议在前端统一处理接口错误响应
2. **加载状态**: 对于耗时操作（如同步、导入导出），建议显示加载状态
3. **数据刷新**: 在执行增删改操作后，建议刷新列表数据
4. **权限控制**: 根据用户权限控制按钮的显示和隐藏