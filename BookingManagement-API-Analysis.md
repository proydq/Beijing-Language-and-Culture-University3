# BookingManagement.vue 后端接口需求分析

## 概述
本文档基于项目实际代码分析BookingManagement.vue组件所需的所有后端接口，包括请求参数、返回参数和接口用途。该组件包含三个主要功能模块：我的预约、全部借用、房间预约。

---

## 1. 我的预约模块（MyBookings）

### 1.1 获取我的预约列表
- **接口路径**: `POST /api/room-booking/my-bookings`
- **接口描述**: 获取当前用户的预约记录列表，支持筛选和分页
- **请求参数**:
```json
{
  "pageNumber": 1,                    // 页码，必填
  "pageSize": 20,                     // 页大小，必填
  "reservationName": "会议室预约",      // 预约名称（对应bookingName字段），可选
  "approvalStatus": "审核中",          // 审核状态，可选：审核中|通过|拒绝
  "usageStatus": "未开始",             // 使用状态，可选：未开始|进行中|已结束
  "startDate": "2024-01-01",          // 开始日期，可选
  "endDate": "2024-01-31"             // 结束日期，可选
}
```
- **返回参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "rows": [
      {
        "id": "uuid-string",                 // 预约ID
        "reservationName": "部门周会",        // 预约名称（bookingName）
        "reservationPeriod": "2024-01-15 第3节-第4节", // 预约周期（bookingPeriod）
        "roomName": "会议室A101",            // 房间名称
        "applicantName": "张三",             // 申请人姓名
        "approvalStatus": "审核中",          // 审核状态（PENDING/APPROVED/REJECTED/CANCELLED）
        "usageStatus": "未开始",             // 使用状态（NOT_STARTED/IN_PROGRESS/COMPLETED/CANCELLED）
        "createTime": "2024-01-10 14:30:00", // 创建时间
        "description": "部门例行周会",        // 预约描述
        "borrowTime": "2024-01-15 第3节-第4节", // 借用时间（与reservationPeriod相同）
        "participants": "张三,李四,王五",     // 参与人员（extend1字段存储）
        "approvers": "赵经理,钱主管"         // 审批人员（extend2字段存储）
      }
    ],
    "total": 100                            // 总记录数
  }
}
```

---

## 2. 全部借用模块（AllBookings）

### 2.1 获取全部借用列表
- **接口路径**: `POST /api/room-booking/all-bookings`
- **接口描述**: 获取所有用户的预约记录列表（管理员权限），支持筛选和分页
- **请求参数**:
```json
{
  "pageNumber": 1,                    // 页码，必填
  "pageSize": 20,                     // 页大小，必填
  "reservationName": "会议室预约",      // 预约名称（对应bookingName字段），可选
  "approvalStatus": "通过",            // 审核状态，可选：审核中|通过|拒绝
  "usageStatus": "进行中",             // 使用状态，可选：未开始|进行中|已结束
  "startDate": "2024-01-01",          // 开始日期，可选
  "endDate": "2024-01-31",            // 结束日期，可选
  "applicantName": "张三"              // 申请人姓名，可选
}
```
- **返回参数**: 与"我的预约列表"返回结构相同，但包含所有用户的数据

---

## 3. 房间预约模块（RoomReservation）

### 3.1 获取可预约房间列表
- **接口路径**: `POST /api/room-booking/rooms/available`
- **接口描述**: 根据区域和条件获取可预约的房间列表（复用教室管理接口）
- **请求参数**:
```json
{
  "roomAreaId": "uuid-string",        // 房间区域ID，可选
  "roomName": "会议室",                // 房间名称关键词，可选
  "available": true                   // 是否只显示可用房间，可选
}
```
- **返回参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "uuid-string",                 // 房间ID
      "name": "会议室A101",                // 房间名称（roomName）
      "capacity": 20,                      // 容纳人数（roomVolume）
      "building": "达力楼",                // 楼宇名称（roomAreaName）
      "floor": "3F",                       // 楼层（从roomNo解析）
      "available": true,                   // 是否可用（基于当前预约情况计算）
      "roomAreaId": "uuid-string",        // 所属区域ID
      "roomType": "会议室",                // 房间类型（roomTypeName）
      "equipment": "投影仪,白板,音响",      // 设备信息（从remark字段解析）
      "description": "标准会议室"          // 房间描述（remark）
    }
  ]
}
```

### 3.2 创建房间预约
- **接口路径**: `POST /api/room-booking/create`
- **接口描述**: 提交房间预约申请
- **请求参数**:
```json
{
  "roomId": "uuid-string",                // 房间ID，必填
  "roomName": "会议室A101",               // 房间名称，必填
  "applicant": "张三",                    // 申请人姓名，必填
  "applicantId": "user-uuid",             // 申请人ID（从当前用户获取）
  "applicantType": "教师",                // 申请人类型（教师/学生/管理员）
  "applicantPhone": "13800138000",        // 申请人电话
  "applicantDepartment": "技术部",        // 申请人部门
  "bookingName": "部门周会",              // 预约名称，必填
  "borrowTime": "2024-01-15 第3节-第4节",  // 借用时间（bookingPeriod），必填
  "bookingStartTime": "2024-01-15 09:00:00", // 预约开始时间，必填
  "bookingEndTime": "2024-01-15 11:00:00",   // 预约结束时间，必填
  "description": "部门例行周会讨论",       // 借用描述，可选
  "reason": "周例会讨论项目进展",          // 申请理由，可选
  "participants": ["张三", "李四", "王五"], // 参与人姓名列表，可选
  "remark": "需要准备投影仪",             // 备注详情，可选
  "approvers": ["赵经理", "钱主管"],       // 审批人姓名列表，必填
  "participantDetails": [                 // 参与人详细信息（存入extend1），可选
    {
      "id": "uuid-string",
      "name": "张三",
      "department": "技术部",
      "position": "工程师",
      "phone": "138****1234",
      "email": "zhangsan@company.com"
    }
  ],
  "approverDetails": [                    // 审批人详细信息（存入extend2），必填
    {
      "id": "uuid-string",
      "name": "赵经理",
      "department": "管理层",
      "position": "部门经理",
      "phone": "138****5678",
      "email": "zhaomgr@company.com"
    }
  ],
  "urgency": "NORMAL"                     // 紧急程度：NORMAL/URGENT/VERY_URGENT
}
```
- **返回参数**:
```json
{
  "code": 200,
  "message": "预约申请提交成功",
  "data": {
    "bookingId": "uuid-string",            // 生成的预约ID
    "bookingName": "部门周会",
    "approvalStatus": "PENDING",           // 初始状态为审核中
    "createTime": "2024-01-15 09:00:00"
  }
}
```

---

## 4. 通用功能接口

### 4.1 楼栋架构接口（已存在）
- **接口路径**: `GET /api/area/tree`
- **接口描述**: 获取楼栋树形结构数据
- **用途**: 左侧楼栋架构树展示
- **请求参数**: 无
- **返回参数**: 
```json
{
  "code": 200,
  "data": [
    {
      "id": "AREA_001",
      "areaName": "达力楼",
      "type": "building",
      "children": [
        {
          "id": "AREA_001_01",
          "areaName": "1F",
          "type": "floor",
          "children": []
        }
      ]
    }
  ]
}
```

### 4.2 人员搜索接口（已存在）
- **接口路径**: `POST /scheme-management/personnel/list`
- **接口描述**: 人员选择弹窗中的人员搜索
- **用途**: PersonSelectorDialog组件中使用
- **请求参数**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "name": "张三",                        // 姓名关键词，可选
  "department": "技术部",                // 部门关键词，可选
  "position": ""                         // 职位关键词，可选
}
```

---

## 5. 预约管理相关接口

### 5.1 编辑预约
- **接口路径**: `PUT /api/room-booking/update/{id}`
- **接口描述**: 编辑已提交的预约申请（仅限审核中状态）
- **请求参数**: 与创建预约接口相同，但id在路径中
- **返回参数**:
```json
{
  "code": 200,
  "message": "预约信息更新成功",
  "data": {
    "bookingId": "uuid-string",
    "bookingName": "部门周会（修改）",
    "lastUpdateTime": "2024-01-15 10:00:00"
  }
}
```

### 5.2 获取预约详情
- **接口路径**: `GET /api/room-booking/detail/{id}`
- **接口描述**: 获取指定预约的详细信息
- **请求参数**: 无（ID在URL路径中）
- **返回参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "uuid-string",
    "bookingName": "部门周会",
    "roomId": "room-uuid",
    "roomName": "会议室A101",
    "applicantName": "张三",
    "applicantType": "教师",
    "applicantPhone": "13800138000",
    "applicantDepartment": "技术部",
    "bookingStartTime": "2024-01-15 09:00:00",
    "bookingEndTime": "2024-01-15 11:00:00",
    "bookingPeriod": "2024-01-15 第3节-第4节",
    "description": "部门例行周会讨论",
    "reason": "周例会讨论项目进展",
    "approvalStatus": "PENDING",
    "usageStatus": "NOT_STARTED",
    "urgency": "NORMAL",
    "createTime": "2024-01-10 14:30:00",
    "participants": "张三,李四,王五",     // 从extend1解析
    "approvers": "赵经理,钱主管"         // 从extend2解析
  }
}
```

### 5.3 审批预约
- **接口路径**: `POST /api/room-booking/approve/{id}`
- **接口描述**: 审批预约申请（基于教室审批配置）
- **请求参数**:
```json
{
  "action": "APPROVED",                  // 审批动作：APPROVED(通过)|REJECTED(拒绝)
  "comment": "同意预约申请",              // 审批意见（approvalComment），可选
  "approverId": "uuid-string",           // 审批人ID，必填
  "approverName": "赵经理"               // 审批人姓名，必填
}
```
- **返回参数**:
```json
{
  "code": 200,
  "message": "审批操作成功",
  "data": {
    "bookingId": "uuid-string",
    "approvalStatus": "APPROVED",          // 新的审批状态
    "approvalTime": "2024-01-15 10:30:00"
  }
}
```

### 5.4 取消预约
- **接口路径**: `POST /api/room-booking/cancel/{id}`
- **接口描述**: 取消预约申请
- **请求参数**:
```json
{
  "reason": "会议取消，不需要预约房间"      // 取消原因（cancelReason），必填
}
```
- **返回参数**:
```json
{
  "code": 200,
  "message": "预约已取消",
  "data": {
    "bookingId": "uuid-string",
    "approvalStatus": "CANCELLED",
    "usageStatus": "CANCELLED",
    "cancelTime": "2024-01-14 16:30:00"
  }
}
```

### 5.5 检查房间可用性
- **接口路径**: `POST /api/room-booking/check-availability`
- **接口描述**: 检查房间在指定时间段是否可用
- **请求参数**:
```json
{
  "roomId": "uuid-string",               // 房间ID，必填
  "startDateTime": "2024-01-15 09:00:00", // 开始时间，必填
  "endDateTime": "2024-01-15 11:00:00",   // 结束时间，必填
  "excludeBookingId": "uuid-string"       // 排除的预约ID（编辑时使用），可选
}
```
- **返回参数**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "available": false,                    // 是否可用
    "conflictBookings": [                  // 冲突的预约列表
      {
        "id": "uuid-string",
        "bookingName": "技术分享会",
        "bookingPeriod": "2024-01-15 第4节-第5节",
        "applicantName": "李四",
        "approvalStatus": "APPROVED"
      }
    ]
  }
}
```

---

## 6. 数据统计接口（已存在）

### 6.1 获取借用统计数据
- **接口路径**: `GET /api/room-booking/stats`
- **接口描述**: 获取借用统计数据（已实现）
- **请求参数**:
```
startTime: 2024-01-01 00:00:00  // 开始时间，可选
endTime: 2024-01-31 23:59:59    // 结束时间，可选
```
- **返回参数**: BookingStatsDto结构

### 6.2 获取借用数据分布
- **接口路径**: `GET /api/room-booking/distribution`
- **接口描述**: 获取借用数据分布（饼图数据）（已实现）
- **请求参数**: 同上
- **返回参数**: List<BookingDistributionDto>

### 6.3 获取借用趋势数据
- **接口路径**: `GET /api/room-booking/trend`
- **接口描述**: 获取借用趋势数据（折线图数据）（已实现）
- **请求参数**:
```
days: 7  // 天数（7、15、30、90）
```
- **返回参数**: List<BookingTrendDto>

---

## 7. 接口汇总

### 7.1 需要新开发的接口
| 序号 | 接口路径 | 方法 | 描述 | 优先级 |
|------|----------|------|------|--------|
| 1 | `/api/room-booking/my-bookings` | POST | 获取我的预约列表 | 高 |
| 2 | `/api/room-booking/all-bookings` | POST | 获取全部借用列表 | 高 |
| 3 | `/api/room-booking/rooms/available` | POST | 获取可预约房间列表 | 高 |
| 4 | `/api/room-booking/create` | POST | 创建房间预约 | 高 |
| 5 | `/api/room-booking/update/{id}` | PUT | 编辑预约 | 中 |
| 6 | `/api/room-booking/detail/{id}` | GET | 获取预约详情 | 中 |
| 7 | `/api/room-booking/approve/{id}` | POST | 审批预约 | 中 |
| 8 | `/api/room-booking/cancel/{id}` | POST | 取消预约 | 中 |
| 9 | `/api/room-booking/check-availability` | POST | 检查房间可用性 | 低 |

### 7.2 已存在的接口
| 序号 | 接口路径 | 方法 | 描述 | 状态 |
|------|----------|------|------|------|
| 1 | `/api/area/tree` | GET | 获取楼栋架构 | 已存在 |
| 2 | `/scheme-management/personnel/list` | POST | 人员搜索 | 已存在 |
| 3 | `/api/room-booking/stats` | GET | 获取借用统计数据 | 已存在 |
| 4 | `/api/room-booking/distribution` | GET | 获取借用数据分布 | 已存在 |
| 5 | `/api/room-booking/trend` | GET | 获取借用趋势数据 | 已存在 |
| 6 | `/api/room-schedule/weeks` | GET | 获取教学周列表 | 已存在 |
| 7 | `/api/room-schedule/week-detail` | POST | 获取教室周课表详情 | 已存在 |

### 7.3 开发建议
1. **第一阶段**：优先开发高优先级接口（1-4），实现基本的预约查看和创建功能
2. **第二阶段**：开发中优先级接口（5-8），完善预约管理功能  
3. **第三阶段**：开发低优先级接口（9），提供辅助功能
4. **复用建议**：可以复用教室管理（SchemeManagement）的部分接口

### 7.4 技术要点
- 所有接口需要用户认证，使用SecurityUtil.getCustomerId()获取客户域ID
- 分页接口统一使用`pageNumber`和`pageSize`参数
- 时间相关字段使用LocalDateTime，前端传递`YYYY-MM-DD HH:mm:ss`格式
- 审批流程需要结合ClassroomApprovalConfig配置
- 房间可用性检查需要查询RoomBooking表的时间冲突
- 使用@PrePersist和@PreUpdate自动维护时间戳

---

## 8. 数据库设计（基于现有结构）

### 8.1 主要数据表（已存在）
- `tb_room_booking` - 预约主表（RoomBooking实体）
- `tb_room` - 房间表（Room实体）  
- `tb_classroom_approval_config` - 教室审批配置表
- `tb_classroom_approval_level` - 教室审批级别表

### 8.2 状态枚举（RoomBooking.java中定义）
- **审核状态(ApprovalStatus)**: 
  - `PENDING("审核中")`
  - `APPROVED("通过")`
  - `REJECTED("拒绝")`
  - `CANCELLED("已取消")`
  
- **使用状态(UsageStatus)**: 
  - `NOT_STARTED("未开始")`
  - `IN_PROGRESS("进行中")`
  - `COMPLETED("已结束")`
  - `CANCELLED("已取消")`
  
- **紧急程度(Urgency)**:
  - `NORMAL("普通")`
  - `URGENT("紧急")`
  - `VERY_URGENT("非常紧急")`

### 8.3 特殊字段说明
- `extend1`: 用于存储参与人详细信息的JSON字符串
- `extend2`: 用于存储审批人详细信息的JSON字符串
- `cstmId`: 客户域ID，通过SecurityUtil.getCustomerId()获取

---

*文档创建时间: 2024年*  
*最后更新时间: 2024年*  
*基于项目实际代码分析*