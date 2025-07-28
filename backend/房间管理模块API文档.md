# 房间管理模块 API 文档

## 概述

房间管理模块提供了完整的房间信息管理功能，包括房间的增删改查、批量操作、导入导出等功能。

## 基础信息

- **模块名称**: 房间管理
- **基础路径**: `/api/room`
- **认证方式**: JWT Token
- **数据格式**: JSON

## 接口列表

### 1. 分页查询房间列表

**接口地址**: `POST /api/room/search`

**请求参数**:
```json
{
  "roomName": "会议室",
  "roomNo": "101",
  "roomTypeName": "会议室",
  "roomAreaId": "area123",
  "roomAreaName": "A栋",
  "roomCode": "R001",
  "pageNumber": 1,
  "pageSize": 10
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
        "id": "room123",
        "roomName": "大会议室",
        "roomAreaName": "A栋",
        "roomNo": "101",
        "roomTypeName": "会议室",
        "roomVolume": 50,
        "remark": "可容纳50人",
        "lastUpdateTime": "2024-01-01 10:00:00",
        "roomCode": "R001",
        "roomArea": "100平方米",
        "capacity": "50",
        "updateTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 2. 查询房间详情

**接口地址**: `GET /api/room/{id}`

**路径参数**:
- `id`: 房间ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "room123",
    "roomName": "大会议室",
    "roomAreaName": "A栋",
    "roomNo": "101",
    "roomTypeName": "会议室",
    "roomVolume": 50,
    "remark": "可容纳50人",
    "roomCode": "R001"
  }
}
```

### 3. 新增房间

**接口地址**: `POST /api/room`

**请求参数**:
```json
{
  "roomName": "大会议室",
  "roomAreaId": "area123",
  "roomAreaName": "A栋",
  "roomNo": "101",
  "roomTypeId": "type123",
  "roomTypeName": "会议室",
  "roomVolume": 50,
  "remark": "可容纳50人",
  "roomCode": "R001",
  "roomArea": "100平方米",
  "capacity": "50"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "room123",
    "roomName": "大会议室",
    "roomNo": "101",
    "roomTypeName": "会议室"
  }
}
```

### 4. 编辑房间

**接口地址**: `PUT /api/room/{id}`

**路径参数**:
- `id`: 房间ID

**请求参数**: 同新增房间

### 5. 删除房间

**接口地址**: `DELETE /api/room/{id}`

**路径参数**:
- `id`: 房间ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 6. 批量删除房间

**接口地址**: `DELETE /api/room/batch`

**请求参数**:
```json
["room123", "room456", "room789"]
```

### 7. 获取房屋类型列表

**接口地址**: `GET /api/room/types`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "CLASSROOM",
      "typeName": "教室"
    },
    {
      "id": "MEETING_ROOM",
      "typeName": "会议室"
    },
    {
      "id": "LABORATORY",
      "typeName": "实验室"
    },
    {
      "id": "OFFICE",
      "typeName": "办公室"
    }
  ]
}
```

### 8. 校验房间唯一性

**接口地址**: `GET /api/room/check-uniqueness`

**查询参数**:
- `roomName`: 房屋名称
- `roomNo`: 房间号码
- `excludeId`: 排除的房间ID（编辑时使用，可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 9. 导入房间数据

**接口地址**: `POST /api/room/import`

**请求参数**: 
- `file`: Excel文件（multipart/form-data）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "导入成功，共导入100条记录"
}
```

### 10. 导出房间数据

**接口地址**: `POST /api/room/export`

**请求参数**: 同分页查询参数

**响应**: Excel文件下载

## 数据模型

### Room 实体

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| id | String | 房间ID | 是 |
| cstmId | String | 客户域ID | 是 |
| roomName | String | 房屋名称 | 是 |
| roomAreaId | String | 房间区域ID | 否 |
| roomAreaName | String | 房间区域名称 | 否 |
| roomNo | String | 房间号码 | 是 |
| roomTypeId | String | 房屋类型ID | 否 |
| roomTypeName | String | 房屋类型名称 | 是 |
| roomVolume | Integer | 容纳人数 | 否 |
| remark | String | 备注 | 否 |
| createTime | LocalDateTime | 创建时间 | 是 |
| lastUpdateTime | LocalDateTime | 最后更新时间 | 是 |
| extend1 | String | 扩展字段1（房间编码） | 否 |
| extend2 | String | 扩展字段2 | 否 |

### 房屋类型枚举

| 类型ID | 类型名称 |
|--------|----------|
| CLASSROOM | 教室 |
| MEETING_ROOM | 会议室 |
| LABORATORY | 实验室 |
| OFFICE | 办公室 |
| DORMITORY | 宿舍 |
| LIBRARY | 图书馆 |
| CANTEEN | 食堂 |
| GYMNASIUM | 体育馆 |
| AUDITORIUM | 礼堂 |
| OTHER | 其他 |

## 业务规则

### 1. 唯一性约束
- 房屋名称 + 房间号码在同一客户域内必须唯一
- 新增和编辑时都会进行唯一性校验

### 2. 客户域隔离
- 所有操作自动设置当前用户的客户域
- 查询时只返回当前客户域的数据
- 确保多租户数据隔离

### 3. 数据验证
- 房屋名称、房间号码、房屋类型名称为必填字段
- 容纳人数必须为正整数
- 房间编码建议使用统一格式

### 4. 时间戳处理
- 创建时自动设置 createTime 和 lastUpdateTime
- 更新时自动更新 lastUpdateTime
- 前端显示时格式化为 "yyyy-MM-dd HH:mm:ss"

### 5. 删除规则
- 删除前会检查房间是否被其他业务系统引用
- 支持单个删除和批量删除
- 删除操作不可恢复，请谨慎操作

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 404 | 房间不存在 |
| 409 | 房间重复（唯一性冲突） |
| 500 | 服务器内部错误 |

## 常见错误信息

- "房间不存在，ID: xxx" - 查询或操作的房间ID不存在
- "房屋名称[xxx]和房间号码[xxx]组合已存在" - 违反唯一性约束
- "房间数据验证失败: xxx" - 数据格式或内容不符合要求
- "房间数据导入失败: xxx" - Excel导入过程中出现错误
- "房间数据导出失败: xxx" - Excel导出过程中出现错误

## 使用示例

### 前端调用示例

```javascript
// 查询房间列表
const searchRooms = async (condition) => {
  const response = await request.post('/api/room/search', condition)
  return response.data
}

// 新增房间
const addRoom = async (roomData) => {
  const response = await request.post('/api/room', roomData)
  return response.data
}

// 编辑房间
const updateRoom = async (id, roomData) => {
  const response = await request.put(`/api/room/${id}`, roomData)
  return response.data
}

// 删除房间
const deleteRoom = async (id) => {
  const response = await request.delete(`/api/room/${id}`)
  return response.data
}

// 获取房屋类型
const getRoomTypes = async () => {
  const response = await request.get('/api/room/types')
  return response.data
}
```

## 注意事项

1. **权限控制**: 所有接口都需要用户登录，并会自动应用客户域隔离
2. **数据一致性**: 房间信息可能被其他业务模块引用，删除时需要注意数据一致性
3. **性能优化**: 大量数据查询时建议使用分页和筛选条件
4. **导入导出**: Excel文件格式需要符合模板要求，具体格式请参考系统提供的模板
5. **扩展字段**: extend1和extend2字段可用于存储自定义信息，如房间编码、房间面积等

## 更新日志

- **v1.0.0** (2024-01-01): 初始版本，提供基础的房间管理功能
- 后续版本将根据业务需求持续优化和扩展功能