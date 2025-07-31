# ApprovalManagement.vue 后端接口分析

## 概述

本文档分析了 ApprovalManagement.vue 组件所需的后端接口，基于项目中现有的代码结构和业务逻辑。

## 前端组件功能分析

### ApprovalManagement.vue 主要功能：
1. **审批类型筛选**：全部审批、待审批、已通过、已拒绝
2. **搜索过滤**：按预约名称、预约人、预约时间范围搜索
3. **审批列表展示**：分页显示待审批和已审批的预约记录
4. **审批操作**：对待审批的预约进行通过/拒绝操作
5. **详情查看**：查看预约详情和审批历史

### ApprovalTable.vue 功能：
- 展示审批列表数据
- 根据状态显示不同操作按钮（立即审批/查看详情）

### ApprovalDialog.vue 功能：
- 审批操作对话框
- 填写审批意见和选择审批结果

## 所需后端接口分析

### 1. 获取待审批列表接口

**接口路径**: `POST /api/room-booking/approvals/pending`

**请求参数**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "reservationName": "预约名称（可选）",
  "applicantName": "申请人姓名（可选）",
  "startDate": "2025-01-01（可选）",
  "endDate": "2025-01-31（可选）"
}
```

**返回参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "rows": [
      {
        "id": "预约ID",
        "bookingName": "预约名称",
        "applicantName": "申请人姓名",
        "applicantType": "申请人类型",
        "roomName": "房间名称",
        "bookingPeriod": "预约时段",
        "bookingStartTime": "2025-01-01T09:00:00",
        "bookingEndTime": "2025-01-01T12:00:00",
        "description": "预约描述",
        "reason": "申请理由",
        "approvalStatus": "PENDING",
        "urgency": "NORMAL",
        "createTime": "2025-01-01T08:00:00",
        "participants": "参与人列表",
        "approvers": "审批人列表"
      }
    ],
    "total": 100
  }
}
```

### 2. 获取全部审批列表接口

**接口路径**: `POST /api/room-booking/approvals/all`

**请求参数**:
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "reservationName": "预约名称（可选）",
  "applicantName": "申请人姓名（可选）",
  "approvalStatus": "PENDING|APPROVED|REJECTED（可选）",
  "startDate": "2025-01-01（可选）",
  "endDate": "2025-01-31（可选）"
}
```

**返回参数**: 同待审批列表接口

### 3. 获取预约详情接口

**接口路径**: `GET /api/room-booking/detail/{id}`

**请求参数**: 路径参数 `id` - 预约ID

**返回参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": "预约ID",
    "bookingName": "预约名称",
    "applicantName": "申请人姓名",
    "applicantType": "申请人类型",
    "applicantPhone": "申请人电话",
    "applicantDepartment": "申请人部门",
    "roomId": "房间ID",
    "roomName": "房间名称",
    "bookingStartTime": "2025-01-01T09:00:00",
    "bookingEndTime": "2025-01-01T12:00:00",
    "bookingPeriod": "预约时段描述",
    "description": "预约描述",
    "reason": "申请理由",
    "approvalStatus": "PENDING",
    "usageStatus": "NOT_STARTED",
    "urgency": "NORMAL",
    "createTime": "2025-01-01T08:00:00",
    "participants": ["参与人1", "参与人2"],
    "approvers": ["审批人1", "审批人2"],
    "participantDetails": [
      {
        "id": "人员ID",
        "name": "姓名",
        "department": "部门",
        "position": "职位"
      }
    ],
    "approverDetails": [
      {
        "id": "审批人ID",
        "name": "姓名",
        "department": "部门",
        "position": "职位"
      }
    ]
  }
}
```

### 4. 审批预约接口

**接口路径**: `POST /api/room-booking/approve/{id}`

**请求参数**:
```json
{
  "action": "APPROVED|REJECTED",
  "comment": "审批意见",
  "approverId": "审批人ID（可选，后端从token获取）",
  "approverName": "审批人姓名（可选，后端从token获取）"
}
```

**返回参数**:
```json
{
  "code": 200,
  "message": "审批成功",
  "data": {
    "bookingId": "预约ID",
    "approvalStatus": "APPROVED|REJECTED",
    "approvalTime": "2025-01-01T10:00:00",
    "approverName": "审批人姓名"
  }
}
```

### 5. 获取审批历史接口

**接口路径**: `GET /api/room-booking/approval-history/{bookingId}`

**请求参数**: 路径参数 `bookingId` - 预约ID

**返回参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": "审批记录ID",
      "approverName": "审批人姓名",
      "approverType": "审批人类型",
      "approvalAction": "APPROVE|REJECT",
      "approvalComment": "审批意见",
      "approvalTime": "2025-01-01T10:00:00",
      "approvalLevel": 1,
      "isFinalApproval": true
    }
  ]
}
```

### 6. 批量审批接口

**接口路径**: `POST /api/room-booking/batch-approve`

**请求参数**:
```json
{
  "bookingIds": ["预约ID1", "预约ID2"],
  "action": "APPROVED|REJECTED",
  "comment": "批量审批意见"
}
```

**返回参数**:
```json
{
  "code": 200,
  "message": "批量审批成功",
  "data": {
    "successCount": 2,
    "failureCount": 0,
    "details": [
      {
        "bookingId": "预约ID1",
        "status": "success",
        "message": "审批成功"
      }
    ]
  }
}
```

## 现有后端代码分析

### 已存在的相关实体类：

1. **RoomBooking.java** - 房间预约实体
   - 包含审批状态 `ApprovalStatus` 枚举
   - 包含使用状态 `UsageStatus` 枚举
   - 包含紧急程度 `Urgency` 枚举
   - 包含审批相关字段：`approvalTime`, `approverId`, `approverName`, `approvalComment`

2. **BookingApproval.java** - 预约审批记录实体
   - 记录审批历史
   - 包含审批动作 `ApprovalAction` 枚举
   - 支持多级审批

### 已存在的相关接口：

1. **approveBooking** - 已在 `roomBookingManagement.js` 中定义
2. **getBookingDetail** - 已在 `roomBookingManagement.js` 中定义
3. **getAllBookings** - 可复用，需要添加审批状态过滤

### 需要新增的DTO类：

1. **ApprovalListRequest.java** - 审批列表查询请求
2. **ApprovalListResponse.java** - 审批列表响应
3. **ApprovalRequest.java** - 审批操作请求
4. **ApprovalResponse.java** - 审批操作响应
5. **ApprovalHistoryResponse.java** - 审批历史响应
6. **BatchApprovalRequest.java** - 批量审批请求

### 需要新增的Service方法：

1. `getPendingApprovals()` - 获取待审批列表
2. `getAllApprovals()` - 获取全部审批列表
3. `approveBooking()` - 审批预约（需要完整实现）
4. `getApprovalHistory()` - 获取审批历史
5. `batchApprove()` - 批量审批

### 需要新增的Controller方法：

1. `/api/room-booking/approvals/pending` - 获取待审批列表
2. `/api/room-booking/approvals/all` - 获取全部审批列表
3. `/api/room-booking/approval-history/{bookingId}` - 获取审批历史
4. `/api/room-booking/batch-approve` - 批量审批

## 实现建议

1. **复用现有代码**：
   - 使用现有的 `RoomBooking` 和 `BookingApproval` 实体
   - 扩展现有的 `RoomBookingService` 和 `RoomBookingController`
   - 复用 `getAllBookings` 方法，添加审批状态过滤

2. **权限控制**：
   - 只有具备审批权限的用户才能查看待审批列表
   - 审批操作需要验证用户权限

3. **审批流程**：
   - 支持多级审批流程
   - 记录完整的审批历史
   - 审批后更新预约状态

4. **数据一致性**：
   - 审批操作使用事务保证数据一致性
   - 同时更新 `RoomBooking` 和 `BookingApproval` 表

## 总结

ApprovalManagement.vue 组件需要6个主要接口支持其功能，其中部分接口已存在，需要新增4个接口和相应的DTO类。建议基于现有的 `RoomBooking` 相关代码进行扩展，以保持代码的一致性和可维护性。