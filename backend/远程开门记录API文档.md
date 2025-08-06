# 远程开门记录API文档

## 概述

本文档描述了远程开门记录管理系统的后端API接口设计。远程开门记录是教室门禁系统中通过远程操作（如管理员授权、应急开门等）产生的特殊开门记录，与普通的刷卡、人脸识别等方式有所区别。

## 核心实体分析

### RoomAccessRecord 实体
基于现有的 `RoomAccessRecord` 实体，远程开门记录具有以下特征：
- **access_type**: 值为"远程开门"
- **open_method**: 远程开门的具体方式（如"管理员授权"、"应急开门"、"系统开门"等）
- **user_id**: 操作人员ID（可能是管理员而非实际使用者）
- **device_id**: 触发开门的设备ID
- **booking_id**: 关联的预约ID（如果有）

## API接口列表

### 1. 获取远程开门记录列表

**接口描述**: 分页查询远程开门记录列表，支持多条件筛选。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records`

**请求参数**:
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "areaId": "区域ID，可选",
  "roomId": "教室ID，可选",
  "basicInfo": "姓名/工号/联系方式模糊查询，可选",
  "startTime": "开始时间，格式：yyyy-MM-dd HH:mm:ss，可选",
  "endTime": "结束时间，格式：yyyy-MM-dd HH:mm:ss，可选",
  "openMethod": "开门方式（管理员授权/应急开门/系统开门），可选",
  "operatorType": "操作员类型（管理员/系统/应急），可选"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": "记录ID",
        "roomId": "教室ID",
        "roomName": "教室名称",
        "buildingName": "楼栋名称",
        "floor": "楼层",
        "userId": "操作用户ID",
        "name": "操作用户姓名",
        "gender": "性别",
        "employeeId": "工号",
        "phone": "联系方式",
        "department": "部门",
        "userType": "用户类型",
        "openMethod": "开门方式",
        "accessTime": "2024-08-05T10:30:00",
        "accessType": "远程开门",
        "bookingId": "关联预约ID",
        "bookingName": "预约名称",
        "bookingPeriod": "预约时段",
        "deviceId": "设备ID",
        "deviceName": "设备名称",
        "operatorId": "实际操作员ID",
        "operatorName": "实际操作员姓名",
        "operatorType": "操作员类型",
        "reason": "开门原因",
        "createTime": "2024-08-05T10:30:00"
      }
    ]
  }
}
```

### 2. 导出远程开门记录

**接口描述**: 导出远程开门记录数据到Excel文件。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records/export`

**请求参数**:
```json
{
  "exportType": "导出类型（current/all）",
  "pageNum": 1,
  "pageSize": 10,
  "areaId": "区域ID，可选",
  "roomId": "教室ID，可选", 
  "basicInfo": "姓名/工号/联系方式模糊查询，可选",
  "startTime": "开始时间，可选",
  "endTime": "结束时间，可选",
  "openMethod": "开门方式，可选",
  "operatorType": "操作员类型，可选"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "fileUrl": "http://example.com/files/remote_access_records_20240805.xlsx",
    "fileName": "远程开门记录_20240805.xlsx"
  }
}
```

### 3. 获取远程开门统计信息

**接口描述**: 获取远程开门记录的统计数据，用于dashboard展示。

**请求方式**: `GET`  
**请求路径**: `/api/room-booking/remote-access-records/stats`

**请求参数**:
- `startTime`: 开始时间（可选）
- `endTime`: 结束时间（可选）
- `areaId`: 区域ID（可选）

**返回数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalRecords": 150,
    "todayRecords": 25,
    "usedRooms": 12,
    "activeOperators": 5,
    "openMethodStats": [
      {
        "method": "管理员授权",
        "count": 80,
        "percentage": 53.33
      },
      {
        "method": "应急开门", 
        "count": 45,
        "percentage": 30.00
      },
      {
        "method": "系统开门",
        "count": 25,
        "percentage": 16.67
      }
    ],
    "operatorTypeStats": [
      {
        "type": "管理员",
        "count": 100,
        "percentage": 66.67
      },
      {
        "type": "系统",
        "count": 35,
        "percentage": 23.33
      },
      {
        "type": "应急",
        "count": 15,
        "percentage": 10.00
      }
    ],
    "hourlyStats": [
      {
        "hour": "08:00",
        "count": 15
      },
      {
        "hour": "09:00", 
        "count": 22
      }
    ]
  }
}
```

### 4. 执行远程开门操作

**接口描述**: 管理员执行远程开门操作，并记录操作日志。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records/open-door`

**请求参数**:
```json
{
  "roomId": "教室ID，必填",
  "deviceId": "设备ID，必填",
  "reason": "开门原因，必填",
  "openMethod": "开门方式（管理员授权/应急开门），必填",
  "bookingId": "关联预约ID，可选",
  "targetUserId": "目标用户ID（为谁开门），可选"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "远程开门成功",
  "data": {
    "recordId": "操作记录ID",
    "openTime": "2024-08-05T10:30:00",
    "success": true
  }
}
```

### 5. 获取远程开门操作日志

**接口描述**: 获取远程开门操作的详细日志记录，用于审计。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records/operation-logs`

**请求参数**:
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "startTime": "开始时间，可选",
  "endTime": "结束时间，可选",
  "operatorId": "操作员ID，可选",
  "roomId": "教室ID，可选",
  "operationType": "操作类型（开门/关门），可选"
}
```

**返回数据**:
```json
{
  "code": 200,
  "message": "success", 
  "data": {
    "total": 50,
    "rows": [
      {
        "id": "日志ID",
        "operatorId": "操作员ID",
        "operatorName": "操作员姓名",
        "operationType": "操作类型",
        "roomId": "教室ID",
        "roomName": "教室名称",
        "deviceId": "设备ID",
        "reason": "操作原因",
        "result": "操作结果",
        "operationTime": "2024-08-05T10:30:00",
        "ipAddress": "操作IP地址",
        "userAgent": "用户代理"
      }
    ]
  }
}
```

### 6. 获取可远程开门的教室列表

**接口描述**: 获取当前用户有权限进行远程开门的教室列表。

**请求方式**: `GET`  
**请求路径**: `/api/room-booking/remote-access-records/available-rooms`

**请求参数**:
- `areaId`: 区域ID（可选）
- `status`: 教室状态（可选，all/occupied/vacant）

**返回数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "roomId": "教室ID",
      "roomName": "教室名称",
      "buildingName": "楼栋名称",
      "floor": "楼层",
      "status": "教室状态（使用中/空闲）",
      "deviceId": "门禁设备ID",
      "deviceStatus": "设备状态（在线/离线）",
      "currentBooking": {
        "bookingId": "当前预约ID",
        "bookingName": "预约名称",
        "applicantName": "申请人",
        "startTime": "2024-08-05T10:00:00",
        "endTime": "2024-08-05T12:00:00"
      },
      "canRemoteOpen": true
    }
  ]
}
```

## 数据字典

### 开门方式 (openMethod)
- `管理员授权`: 管理员通过系统授权开门
- `应急开门`: 紧急情况下的开门操作
- `系统开门`: 系统自动开门（如预约时间到达）
- `维护开门`: 设备维护时的开门操作

### 操作员类型 (operatorType)
- `管理员`: 系统管理员
- `系统`: 系统自动操作
- `应急`: 应急响应人员
- `维护`: 设备维护人员

### 操作类型 (operationType)
- `开门`: 远程开门操作
- `关门`: 远程关门操作
- `重置`: 设备重置操作

### 教室状态 (status)
- `使用中`: 教室正在使用
- `空闲`: 教室空闲可用
- `维护中`: 教室维护状态
- `故障`: 教室设备故障

### 设备状态 (deviceStatus)
- `在线`: 设备正常在线
- `离线`: 设备离线
- `故障`: 设备故障
- `维护`: 设备维护中

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 操作成功 | - |
| 400 | 请求参数错误 | 检查请求参数格式和必填项 |
| 401 | 未授权访问 | 检查登录状态和token |
| 403 | 权限不足 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 检查教室ID、设备ID等是否正确 |
| 500 | 服务器内部错误 | 联系技术支持 |
| 1001 | 教室不存在 | 检查教室ID是否正确 |
| 1002 | 设备离线 | 检查设备网络连接 |
| 1003 | 设备故障 | 联系设备维护人员 |
| 1004 | 无权限操作该教室 | 联系管理员分配权限 |
| 1005 | 教室正在使用中 | 确认是否需要强制开门 |
| 1006 | 远程开门失败 | 检查设备状态和网络连接 |

## 安全考虑

### 1. 权限控制
- 只有具备相应权限的用户才能执行远程开门操作
- 不同级别的用户有不同的操作权限范围
- 所有操作都记录详细的审计日志

### 2. 操作审计
- 记录所有远程开门操作的详细信息
- 包括操作人、操作时间、操作原因、操作结果等
- 支持操作日志的查询和导出

### 3. 设备安全
- 验证设备的在线状态和健康状况
- 防止对故障或离线设备的误操作
- 支持设备状态的实时监控

### 4. 数据安全
- 所有API都需要有效的身份认证
- 敏感操作需要额外的权限验证
- 传输过程中的数据加密保护

## 业务规则

### 1. 开门权限
- 管理员可以对所有教室执行远程开门
- 普通用户只能对自己有预约的教室执行开门
- 应急人员在紧急情况下可以开启任何教室

### 2. 操作限制
- 每个用户每天的远程开门次数有限制
- 连续开门操作需要有时间间隔
- 特殊时段（如深夜）的开门需要特殊权限

### 3. 记录保留
- 远程开门记录至少保留1年
- 操作日志至少保留2年
- 重要的审计信息永久保留

## 实现注意事项

### 1. 性能优化
- 分页查询避免大数据量传输
- 合理的索引设计提高查询效率
- 缓存机制减少数据库访问

### 2. 异常处理
- 完善的异常捕获和处理机制
- 用户友好的错误信息提示
- 系统异常的详细日志记录

### 3. 数据一致性
- 确保远程开门记录与实际开门状态一致
- 处理并发操作可能导致的数据冲突
- 定期的数据一致性检查和修复

### 4. 扩展性
- 支持新的开门方式和操作类型
- 灵活的权限配置机制
- 可配置的业务规则参数