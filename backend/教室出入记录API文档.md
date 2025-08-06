# 教室出入记录接口文档

## 概述
本文档描述了教室出入记录管理相关的后端API接口，包括出入记录查询、统计、导出等功能。

## 基础URL
```
/api/room-booking
```

## 接口列表

### 1. 获取教室借用记录列表
**接口地址**: `POST /api/room-booking/access-records`

**功能描述**: 获取教室借用记录列表，支持多条件筛选和分页查询

**请求参数**:
```json
{
  "pageNum": 1,          // 页码，默认1
  "pageSize": 10,        // 每页条数，默认10
  "areaId": "",          // 区域ID（可选，用于楼栋/楼层筛选）
  "roomId": "",          // 教室ID（可选）
  "basicInfo": "",       // 基础信息（姓名/工号/联系方式，可选）
  "startTime": "",       // 开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
  "endTime": "",         // 结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
  "openMethod": "",      // 开门方式（刷卡/人脸识别/按钮，可选）
  "accessType": ""       // 通行类型（预约权限/永久权限，可选）
}
```

**返回参数**:
```json
{
  "success": true,
  "message": "操作成功",
  "result": {
    "rows": [{
      "id": "record001",
      "roomId": "room001",
      "roomName": "A-101",
      "buildingName": "教学楼A",
      "floor": "1F",
      "userId": "user001",
      "name": "张三",
      "gender": "男",
      "employeeId": "T001",
      "phone": "13800138000",
      "department": "计算机学院",
      "userType": "教师",
      "openMethod": "刷卡",
      "accessTime": "2024-08-04 14:30:00",
      "accessType": "预约权限",
      "bookingId": "booking001",
      "bookingName": "计算机基础课程",
      "bookingPeriod": "14:00-16:00",
      "deviceId": "device001",
      "deviceName": "A-101门禁",
      "createTime": "2024-08-04 14:30:00"
    }],
    "total": 100
  }
}
```

### 2. 导出教室借用记录
**接口地址**: `POST /api/room-booking/access-records/export`

**功能描述**: 导出教室借用记录为Excel文件

**请求参数**:
```json
{
  "exportType": "current",    // 导出类型：current-当前页，all-全部页
  "pageNum": 1,               // 页码（exportType为current时必填）
  "pageSize": 10,             // 每页条数（exportType为current时必填）
  "areaId": "",               // 区域ID（可选）
  "roomId": "",               // 教室ID（可选）
  "basicInfo": "",            // 基础信息（可选）
  "startTime": "",            // 开始时间（可选）
  "endTime": "",              // 结束时间（可选）
  "openMethod": "",           // 开门方式（可选）
  "accessType": ""            // 通行类型（可选）
}
```

**返回参数**:
```json
{
  "success": true,
  "message": "导出成功",
  "result": {
    "fileName": "教室借用记录_20240804_143000.xlsx",
    "fileUrl": "/download/temp/教室借用记录_20240804_143000.xlsx",
    "fileSize": 102400,
    "recordCount": 100
  }
}
```

### 3. 获取教室借用统计信息
**接口地址**: `GET /api/room-booking/access-records/stats`

**功能描述**: 获取教室借用的统计信息，包括总记录数、使用教室数、使用人数等

**请求参数**:
- `startTime` (string, 可选): 开始时间，格式：yyyy-MM-dd HH:mm:ss
- `endTime` (string, 可选): 结束时间，格式：yyyy-MM-dd HH:mm:ss
- `areaId` (string, 可选): 区域ID

**返回参数**:
```json
{
  "success": true,
  "message": "操作成功",
  "result": {
    "totalRecords": 1000,           // 总记录数
    "todayRecords": 50,             // 今日记录数
    "totalRooms": 30,               // 使用教室数
    "totalUsers": 200,              // 使用人数
    "accessTypeStats": {            // 通行类型统计
      "预约权限": 600,
      "永久权限": 400
    },
    "openMethodStats": {            // 开门方式统计
      "刷卡": 500,
      "人脸识别": 300,
      "按钮": 200
    },
    "userTypeStats": {              // 用户类型统计
      "教师": 400,
      "学生": 600
    }
  }
}
```

### 4. 获取教室实时使用状态
**接口地址**: `GET /api/room-booking/access-records/room-status`

**功能描述**: 获取教室的实时使用状态，包括当前使用人数、最后通行时间等

**请求参数**:
- `areaId` (string, 可选): 区域ID

**返回参数**:
```json
{
  "success": true,
  "message": "操作成功",
  "result": [{
    "roomId": "room001",
    "roomName": "A-101",
    "buildingName": "教学楼A",
    "floor": "1F",
    "status": "使用中",              // 使用状态：使用中/空闲
    "currentUsers": 5,               // 当前使用人数
    "lastAccessTime": "2024-08-04 14:30:00",  // 最后通行时间
    "currentBooking": {              // 当前预约信息
      "bookingId": "booking001",
      "bookingName": "计算机基础课程",
      "applicantName": "张三",
      "bookingPeriod": "14:00-16:00"
    }
  }]
}
```

### 5. 获取教室预约统计列表
**接口地址**: `POST /api/room-booking/access-records/booking-stats`

**功能描述**: 获取教室预约统计信息，包括预约次数、累计时长、累计人数等

**请求参数**:
```json
{
  "pageNum": 1,              // 页码
  "pageSize": 10,            // 每页条数
  "areaId": "",              // 区域ID（可选）
  "roomId": "",              // 教室ID（可选）
  "roomName": "",            // 教室名称（可选）
  "startTime": "",           // 开始时间（可选）
  "endTime": "",             // 结束时间（可选）
  "exportType": "current"    // 导出类型（用于导出时）
}
```

**返回参数**:
```json
{
  "success": true,
  "message": "操作成功",
  "result": {
    "rows": [{
      "roomId": "room001",
      "roomName": "A-101",
      "buildingName": "教学楼A",
      "floor": "1F",
      "bookingCount": 50,        // 预约次数
      "totalHours": 100.5,       // 累计时长（小时）
      "totalPersons": 200,       // 累计人数
      "lastBookingTime": "2024-08-04 14:00:00"  // 最后预约时间
    }],
    "total": 30
  }
}
```

### 6. 获取教室预约详情
**接口地址**: `POST /api/room-booking/access-records/room/{roomId}/details`

**功能描述**: 获取指定教室的详细预约记录列表，支持筛选和分页

**请求参数**:
```json
{
  "pageNum": 1,              // 页码
  "pageSize": 10,            // 每页条数
  "roomId": "",              // 教室ID（在路径参数中）
  "basicInfo": "",           // 基础信息（姓名/工号/联系方式，可选）
  "bookingStatus": "",       // 预约状态（可选）
  "startTime": "",           // 开始时间（可选）
  "endTime": "",             // 结束时间（可选）
  "exportType": "current"    // 导出类型（用于导出时）
}
```

**返回参数**:
```json
{
  "success": true,
  "message": "操作成功",
  "result": {
    "roomInfo": {
      "roomId": "room001",
      "roomName": "A-101",
      "buildingName": "教学楼A",
      "floor": "1F"
    },
    "bookingDetails": {
      "rows": [{
        "bookingId": "booking001",
        "bookingName": "计算机基础课程",
        "applicantName": "张三",
        "department": "计算机学院",
        "phone": "13800138000",
        "bookingDate": "2024-08-04",
        "bookingPeriod": "14:00-16:00",
        "participantCount": 30,
        "bookingStatus": "已通过",
        "createTime": "2024-08-01 10:00:00"
      }],
      "total": 50
    }
  }
}
```

### 7. 导出教室预约统计数据
**接口地址**: `POST /api/room-booking/access-records/booking-stats/export`

**功能描述**: 导出教室预约统计数据为Excel文件

**请求参数**: 同"获取教室预约统计列表"接口

**返回参数**: 同"导出教室借用记录"接口

### 8. 导出指定教室的预约详情
**接口地址**: `POST /api/room-booking/access-records/room/{roomId}/export`

**功能描述**: 导出指定教室的预约详情为Excel文件

**请求参数**: 同"获取教室预约详情"接口

**返回参数**: 同"导出教室借用记录"接口

## 数据字典

### 开门方式 (openMethod)
- `刷卡`: 使用门禁卡刷卡进入
- `人脸识别`: 使用人脸识别系统进入
- `按钮`: 使用按钮开门进入

### 通行类型 (accessType)
- `预约权限`: 基于预约的临时通行权限
- `永久权限`: 长期有效的通行权限

### 用户类型 (userType)
- `教师`: 教职工用户
- `学生`: 学生用户

### 教室状态 (status)
- `使用中`: 教室当前有人使用
- `空闲`: 教室当前无人使用

### 预约状态 (bookingStatus)
- `待审批`: 预约申请待审批
- `已通过`: 预约申请已通过
- `已拒绝`: 预约申请已拒绝
- `已取消`: 预约已被取消

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有时间相关的参数都使用 `yyyy-MM-dd HH:mm:ss` 格式
2. 分页参数 `pageNum` 从1开始，不是从0开始
3. 导出功能有两种模式：
   - `current`: 导出当前页数据
   - `all`: 导出所有符合条件的数据
4. 统计接口的时间范围参数为可选，不传则统计所有数据
5. 实际实现中需要根据真实的数据库表结构调整查询逻辑
6. 当前服务层实现使用了模拟数据，实际项目需要替换为真实的数据库查询

## 数据权限说明

1. 普通用户只能查看自己的借用记录
2. 管理员可以查看所有借用记录
3. 导出功能需要特定权限
4. 统计数据根据用户权限返回相应范围的数据