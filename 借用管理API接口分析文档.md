# 借用管理API接口分析文档

## 概述

本文档分析了借用管理系统中"我的预约"界面的功能需求，特别是**查看详情**和**取消预约**功能所需的后端API接口。通过对项目代码的分析，发现部分接口已存在，但缺少关键的详情查看和取消预约接口。

---

## 当前问题分析

### 1. 我的预约界面现状
- 文件位置：`src/components/RoomBooking/Booking/MyBookings.vue`
- 当前问题：
  - `handleView(row)` 方法仅使用模拟数据显示详情
  - `handleCancel(row)` 方法未调用实际的后端取消接口
  - 审批详情数据完全使用硬编码的模拟数据

### 2. 现有API接口分析
- API文件：`src/api/roomBookingManagement.js`
- 后端控制器：`backend/src/main/java/com/proshine/system/controller/RoomBookingController.java`

---

## 现有接口梳理

### ✅ 已实现的接口

#### 1. 获取我的预约列表
```javascript
// 前端接口
export const getMyBookings = (params) => {
  return request.post('/api/room-booking/my-bookings', params)
}

// 后端实现
@PostMapping("/my-bookings")
public ResponseEntity<ResponsePageDataEntity<BookingListResponse>> getMyBookings(@RequestBody MyBookingsRequest request)
```

**请求参数：**
```typescript
interface MyBookingsRequest {
  pageNumber: number;          // 页码
  pageSize: number;            // 页大小
  reservationName?: string;    // 预约名称（可选）
  approvalStatus?: string;     // 审核状态：审核中|通过|拒绝
  usageStatus?: string;        // 使用状态：未开始|进行中|已结束
  startDate?: string;          // 开始日期（可选）
  endDate?: string;            // 结束日期（可选）
}
```

**返回参数：**
```typescript
interface BookingListResponse {
  id: string;                  // 预约ID
  reservationName: string;     // 预约名称
  bookingName: string;         // 预约名称（别名）
  reservationPeriod: string;   // 预约周期
  bookingTime: string;         // 预约时间（别名）
  description: string;         // 描述
  applicantName: string;       // 申请人姓名
  applicant: string;           // 申请人（别名）
  roomName: string;            // 房间名称
  approvalStatus: string;      // 审批状态
  auditStatus: string;         // 审批状态（别名）
  usageStatus: string;         // 使用状态
  useStatus: string;           // 使用状态（别名）
}
```

#### 2. 取消预约接口
```javascript
// 前端接口（已定义）
export const cancelBooking = (id, cancelData) => {
  return request.post(`/api/room-booking/cancel/${id}`, cancelData)
}
```

**请求参数：**
```typescript
interface CancelBookingRequest {
  reason: string;              // 取消原因
}
```

**返回参数：**
```typescript
interface CancelBookingResponse {
  success: boolean;            // 是否成功
  message: string;             // 响应消息
  data?: any;                  // 额外数据（可选）
}
```

---

## ❌ 缺失的接口

### 1. 获取预约详情接口

#### 前端接口定义
```javascript
// 已在 roomBookingManagement.js 中定义，但后端未实现
export const getBookingDetail = (id) => {
  return request.get(`/api/room-booking/detail/${id}`)
}
```

#### 后端需要实现
```java
/**
 * 获取预约详情
 * @param id 预约ID
 * @return 预约详情信息
 */
@GetMapping("/detail/{id}")
public ResponseEntity<BookingDetailResponse> getBookingDetail(@PathVariable String id) {
    try {
        log.info("==========/api/room-booking/detail/{} [GET]=============id:{}", id, id);
        BookingDetailResponse result = roomBookingService.getBookingDetail(id);
        return ResponseEntity.success(result);
    } catch (Exception e) {
        log.error("获取预约详情失败：", e);
        return ResponseEntity.fail(e.getMessage());
    }
}
```

#### 请求参数
- **路径参数：** `id` (String) - 预约ID

#### 返回参数
```typescript
interface BookingDetailResponse {
  // 基本信息
  id: string;                          // 预约ID
  reservationName: string;             // 预约名称
  bookingName: string;                 // 预约名称
  userName: string;                    // 申请人姓名
  applicantName: string;               // 申请人姓名
  applicant: string;                   // 申请人
  
  // 时间信息
  reservationPeriod: string;           // 预约周期
  borrowTime: string;                  // 借用时间段
  bookingStartTime: string;            // 预约开始时间
  bookingEndTime: string;              // 预约结束时间
  
  // 房间信息
  roomId: string;                      // 房间ID
  roomName: string;                    // 房间名称
  roomAreaName: string;                // 区域名称
  
  // 申请信息
  description: string;                 // 借用描述
  borrowDesc: string;                  // 借用描述（别名）
  reason: string;                      // 申请理由
  remark: string;                      // 备注信息
  
  // 人员信息
  participants: string[];              // 参与人列表
  participantDetails: ParticipantDetail[]; // 参与人详细信息
  
  // 审批信息
  approvalStatus: string;              // 审批状态
  usageStatus: string;                 // 使用状态
  urgency: string;                     // 紧急程度
  
  // 审批流程
  approvalSteps: ApprovalStep[];       // 审批步骤列表
  
  // 时间戳
  createdTime: string;                 // 创建时间
  updatedTime: string;                 // 更新时间
}

interface ParticipantDetail {
  id: string;                          // 人员ID
  name: string;                        // 姓名
  department: string;                  // 部门
  phone?: string;                      // 电话（可选）
  email?: string;                      // 邮箱（可选）
}

interface ApprovalStep {
  levelName: string;                   // 审批级别名称
  approvers: string[];                 // 审批人列表
  confirmedApprover: string;           // 实际审批人
  approvalTime: string | null;         // 审批时间
  comment: string;                     // 审批意见
  status: string;                      // 审批状态：PENDING|APPROVED|REJECTED
}
```

### 2. 取消预约接口（后端实现）

虽然前端已定义，但需要确认后端是否已实现：

```java
/**
 * 取消预约
 * @param id 预约ID
 * @param request 取消请求参数
 * @return 取消结果
 */
@PostMapping("/cancel/{id}")
public ResponseEntity<String> cancelBooking(@PathVariable String id, @RequestBody CancelBookingRequest request) {
    try {
        log.info("==========/api/room-booking/cancel/{} [POST]=============id:{}, request:{}", id, id, request);
        String result = roomBookingService.cancelBooking(id, request);
        return ResponseEntity.success(result);
    } catch (Exception e) {
        log.error("取消预约失败：", e);
        return ResponseEntity.fail(e.getMessage());
    }
}
```

---

## 数据传输对象（DTO）定义建议

### 1. CancelBookingRequest
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingRequest {
    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String reason;
}
```

### 2. BookingDetailResponse
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    // 基本信息
    private String id;
    private String reservationName;
    private String userName;
    private String applicantName;
    
    // 时间信息
    private String reservationPeriod;
    private String borrowTime;
    private LocalDateTime bookingStartTime;
    private LocalDateTime bookingEndTime;
    
    // 房间信息
    private String roomId;
    private String roomName;
    private String roomAreaName;
    
    // 申请信息
    private String description;
    private String reason;
    private String remark;
    
    // 人员信息
    private List<String> participants;
    private List<ParticipantDetail> participantDetails;
    
    // 审批信息
    private String approvalStatus;
    private String usageStatus;
    private String urgency;
    
    // 审批流程
    private List<ApprovalStep> approvalSteps;
    
    // 时间戳
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantDetail {
        private String id;
        private String name;
        private String department;
        private String phone;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalStep {
        private String levelName;
        private List<String> approvers;
        private String confirmedApprover;
        private LocalDateTime approvalTime;
        private String comment;
        private String status;
    }
}
```

---

## 前端修改建议

### 1. 修改 MyBookings.vue 中的 handleView 方法
```javascript
// 当前实现（使用模拟数据）
function handleView(row) {
  reservationDetail.value = {
    userName: row.applicantName,
    reservationTitle: row.reservationName,
    borrowTime: row.reservationPeriod,
    borrowDesc: row.description,
    participants: (bookingBaseInfo[row.id]?.participants || '张三, 李四, 王五').split(', '),
    remark: bookingBaseInfo[row.id]?.remark || '',
    approvalSteps: auditDetailData[row.id] || [],
  }
  detailDialogVisible.value = true
}

// 建议修改为调用真实API
async function handleView(row) {
  try {
    const response = await getBookingDetail(row.id)
    if (response.code === 200) {
      reservationDetail.value = {
        id: response.data.id,
        userName: response.data.userName || response.data.applicantName,
        reservationTitle: response.data.reservationName,
        borrowTime: response.data.borrowTime || response.data.reservationPeriod,
        borrowDesc: response.data.description,
        participants: response.data.participants || [],
        remark: response.data.remark || '',
        approvalSteps: response.data.approvalSteps || [],
      }
      detailDialogVisible.value = true
    } else {
      ElMessage.error('获取预约详情失败：' + response.message)
    }
  } catch (error) {
    console.error('获取预约详情失败：', error)
    ElMessage.error('获取预约详情失败')
  }
}
```

### 2. 修改 handleCancel 方法
```javascript
// 当前实现（仅触发事件）
function handleCancel(row) {
  emit('cancel', row)
}

// 建议修改为调用真实API
async function handleCancel(row) {
  try {
    // 可以弹出输入框让用户填写取消原因
    const reason = await ElMessageBox.prompt('请输入取消原因：', '取消预约', {
      confirmButtonText: '确认取消',
      cancelButtonText: '取消',
      inputValidator: (value) => {
        if (!value || value.trim() === '') {
          return '取消原因不能为空'
        }
        return true
      }
    })
    
    const response = await cancelBooking(row.id, {
      reason: reason.value
    })
    
    if (response.code === 200) {
      ElMessage.success('预约已成功取消')
      emit('cancel', row) // 通知父组件刷新列表
    } else {
      ElMessage.error('取消预约失败：' + response.message)
    }
  } catch (error) {
    if (error !== 'cancel') { // 用户没有点击取消按钮
      console.error('取消预约失败：', error)
      ElMessage.error('取消预约失败')
    }
  }
}
```

---

## 实施优先级

### 高优先级（必须实现）
1. **获取预约详情接口** - 解决查看详情功能
2. **取消预约接口后端实现** - 确保取消功能正常工作

### 中优先级（建议实现）
1. 前端方法的API调用改造
2. 错误处理和用户体验优化

### 低优先级（可选实现）
1. 审批流程详情的进一步优化
2. 参与人详细信息的展示优化

---

## 总结

通过分析发现，项目在API层面已经有较好的规划，但关键的**预约详情查看**接口在后端缺失实现。**取消预约**接口前端已定义，但需要确认后端实现。建议优先实现预约详情接口，然后修改前端代码以调用真实的API接口，替换当前的模拟数据逻辑。

这样可以让"我的预约"界面真正具备查看详情和取消预约的完整功能。