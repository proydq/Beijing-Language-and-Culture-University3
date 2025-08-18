# 用户类型管理API测试说明

## 概述
为用户管理模块添加了用户类型字段的支持，包括新增用户、编辑用户时设置用户类型的功能。

## 修改内容

### 1. UserVo DTO 修改
- 添加了 `userType` 字段，用于返回用户类型信息

### 2. 新增的API接口

#### 获取用户类型选项列表
```
GET /system/user/userTypes
```

**功能**: 返回可选的用户类型列表，供前端下拉框使用

**权限**: `system:user:view` 或 `system:user:manage`

**响应示例**:
```json
{
  "code": "0",
  "message": "成功",
  "data": [
    {
      "value": "教师",
      "label": "教师"
    },
    {
      "value": "学生", 
      "label": "学生"
    }
  ]
}
```

#### 更新用户类型
```
POST /system/user/updateUserType
```

**功能**: 单独更新指定用户的类型

**权限**: `system:user:update` 或 `system:user:manage`

**请求参数**:
- `userId` (string): 用户ID
- `userType` (string): 用户类型，只能是"教师"或"学生"

**请求示例**:
```
POST /system/user/updateUserType?userId=user123&userType=教师
```

**响应示例**:
```json
{
  "code": "0",
  "message": "成功",
  "data": null
}
```

### 3. 已有接口的增强

#### 用户查询接口
```
POST /system/user/search
GET /system/user/findById?id=userId
```

**增强**: 返回的 `UserVo` 对象现在包含 `userType` 字段

**响应示例**:
```json
{
  "code": "0",
  "message": "成功",
  "data": {
    "id": "user123",
    "username": "teacher001",
    "realName": "张教师",
    "userType": "教师",
    ...
  }
}
```

#### 用户保存/更新接口
```
POST /system/user/save
POST /system/user/update
```

**增强**: 请求的 `SysUser` 对象现在可以包含 `userType` 字段

**请求示例**:
```json
{
  "username": "teacher001",
  "realName": "张教师",
  "userType": "教师",
  "phone": "13800138000",
  ...
}
```

## 测试用例

### 1. 获取用户类型选项
```bash
curl -X GET "http://localhost:8080/system/user/userTypes" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. 创建新用户并设置类型
```bash
curl -X POST "http://localhost:8080/system/user/save" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "teacher001",
    "password": "123456",
    "realName": "张教师",
    "userType": "教师",
    "phone": "13800138000",
    "jobNumber": "T001"
  }'
```

### 3. 更新用户信息（包括类型）
```bash
curl -X POST "http://localhost:8080/system/user/update" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "id": "user123",
    "username": "student001", 
    "realName": "李学生",
    "userType": "学生",
    "phone": "13800138001"
  }'
```

### 4. 单独更新用户类型
```bash
curl -X POST "http://localhost:8080/system/user/updateUserType?userId=user123&userType=学生" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. 查询用户信息验证类型
```bash
curl -X GET "http://localhost:8080/system/user/findById?id=user123" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 6. 验证预约功能
创建用户并设置类型后，用该用户创建预约，验证预约记录的 `applicant_type` 字段是否正确：

```bash
curl -X POST "http://localhost:8080/api/room-booking/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer USER_TOKEN" \
  -d '{
    "roomId": "room123",
    "roomName": "测试教室",
    "applicant": "张教师",
    "bookingName": "测试预约",
    "bookingStartTime": "2025-01-16T10:00:00",
    "bookingEndTime": "2025-01-16T12:00:00",
    "description": "测试预约功能"
  }'
```

## 验证要点

### 1. 数据一致性验证
- 创建用户时设置的 `userType` 应该正确保存到数据库
- 查询用户信息时应该正确返回 `userType` 字段
- 更新用户类型后，预约功能应该使用新的用户类型

### 2. 权限验证
- 确保只有具有相应权限的用户才能访问用户类型管理功能
- 验证用户类型值的有效性（只能是"教师"或"学生"）

### 3. 业务逻辑验证
- 新用户默认类型为"教师"
- 更新用户类型后，新创建的预约应该使用正确的用户类型
- 用户类型字段在各个接口间保持一致

## 注意事项

1. **默认值**: 新创建的用户如果不指定 `userType`，默认为"教师"
2. **数据验证**: 用户类型只能是"教师"或"学生"，其他值会被拒绝
3. **向后兼容**: 所有修改都保持向后兼容，不影响现有功能
4. **权限控制**: 所有新增接口都添加了适当的权限注解

## 前端集成建议

1. **用户列表**: 在用户列表中显示用户类型列，可以进行筛选
2. **用户表单**: 在创建/编辑用户表单中添加用户类型选择器
3. **快速操作**: 提供快速修改用户类型的功能
4. **数据验证**: 前端也应该验证用户类型的有效性