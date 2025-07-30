# ViolationSettings.vue 后端接口设计

## 1. 数据实体设计

### 1.1 ViolationSetting 违规设置实体
```java
@Entity
@Table(name = "tb_violation_setting")
public class ViolationSetting {
    @Id
    private String id;
    
    @Column(name = "cstm_id")
    private String cstmId; // 客户域ID
    
    @Column(name = "room_id")
    private String roomId; // 教室ID
    
    @Column(name = "start_time_minutes")
    private Integer startTimeMinutes; // 超时分钟数（默认30分钟）
    
    @Column(name = "violation_count_threshold")
    private Integer violationCountThreshold; // 违规次数阈值（默认3次）
    
    @Column(name = "create_time")
    private Long createTime;
    
    @Column(name = "last_update_time")
    private Long lastUpdateTime;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
```

### 1.2 UserViolationRecord 用户违规记录实体
```java
@Entity
@Table(name = "tb_user_violation_record")
public class UserViolationRecord {
    @Id
    private String id;
    
    @Column(name = "cstm_id")
    private String cstmId;
    
    @Column(name = "user_id")
    private String userId; // 违规用户ID
    
    @Column(name = "room_id")
    private String roomId; // 违规教室ID
    
    @Column(name = "booking_id")
    private String bookingId; // 相关预约ID
    
    @Column(name = "violation_type")
    private String violationType; // 违规类型：OVERTIME_NO_CHECKIN
    
    @Column(name = "overtime_minutes")
    private Integer overtimeMinutes; // 超时分钟数
    
    @Column(name = "violation_time")
    private Long violationTime; // 违规时间
    
    @Column(name = "is_processed")
    private Boolean isProcessed = false; // 是否已处理（加入黑名单）
}
```

### 1.3 UserBlacklist 用户黑名单实体
```java
@Entity
@Table(name = "tb_user_blacklist")
public class UserBlacklist {
    @Id
    private String id;
    
    @Column(name = "cstm_id")
    private String cstmId;
    
    @Column(name = "user_id")
    private String userId; // 黑名单用户ID
    
    @Column(name = "reason")
    private String reason; // 加入黑名单原因
    
    @Column(name = "blacklist_type")
    private String blacklistType; // 类型：AUTO_VIOLATION（自动违规）、MANUAL（手动添加）
    
    @Column(name = "violation_count")
    private Integer violationCount; // 违规次数（自动加入时）
    
    @Column(name = "create_time")
    private Long createTime;
    
    @Column(name = "create_user_id")
    private String createUserId; // 创建人ID（手动添加时）
    
    @Column(name = "is_active")
    private Boolean isActive = true; // 是否生效
}
```

## 2. API接口设计

### 2.1 查询教室违规设置列表
```
GET /api/violation-settings/rooms/search
POST /api/violation-settings/rooms/search
```

**请求参数：**
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "roomName": "教室名称或房间号",
  "roomAreaId": "楼栋ID"
}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": "room-id-1",
        "roomName": "多媒体教室（101）",
        "roomNo": "101",
        "roomAreaName": "科研楼",
        "startTime": 30,
        "violationCount": 3,
        "lastUpdateTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 2.2 获取单个教室违规设置详情
```
GET /api/violation-settings/rooms/{roomId}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": "setting-id-1",
    "roomId": "room-id-1",
    "roomName": "多媒体教室（101）",
    "roomNo": "101",
    "startTime": 30,
    "violationCount": 3
  }
}
```

### 2.3 更新单个教室违规设置
```
PUT /api/violation-settings/rooms/{roomId}
```

**请求参数：**
```json
{
  "startTime": 30,
  "violationCount": 3
}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 2.4 批量更新教室违规设置
```
PUT /api/violation-settings/rooms/batch-update
```

**请求参数：**
```json
{
  "roomIds": ["room-id-1", "room-id-2"],
  "startTime": 30,
  "violationCount": 3
}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "批量更新成功",
  "data": {
    "successCount": 2,
    "failCount": 0
  }
}
```

### 2.5 查询用户违规记录
```
GET /api/violation-settings/violations/search
POST /api/violation-settings/violations/search
```

**请求参数：**
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "userId": "用户ID",
  "roomId": "教室ID",
  "startTime": "2024-01-01 00:00:00",
  "endTime": "2024-01-31 23:59:59"
}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 50,
    "rows": [
      {
        "id": "violation-id-1",
        "userName": "张三",
        "userEmployeeId": "123456",
        "roomName": "多媒体教室（101）",
        "overtimeMinutes": 45,
        "violationTime": "2024-01-01 10:30:00",
        "isProcessed": true
      }
    ]
  }
}
```

## 3. 黑名单管理相关接口

### 3.1 查询黑名单用户列表
```
GET /api/blacklist/users/search
POST /api/blacklist/users/search
```

**请求参数：**
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "name": "姓名",
  "employeeId": "工号",
  "department": "部门"
}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 20,
    "rows": [
      {
        "id": "blacklist-id-1",
        "userId": "user-id-1",
        "name": "李远达",
        "employeeId": "1958990148",
        "department": "教师",
        "reason": "连续违规3次，自动加入黑名单",
        "blacklistType": "AUTO_VIOLATION",
        "violationCount": 3,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 3.2 手动添加用户到黑名单
```
POST /api/blacklist/users
```

**请求参数：**
```json
{
  "userId": "user-id-1",
  "reason": "手动添加原因"
}
```

### 3.3 移除用户出黑名单
```
DELETE /api/blacklist/users/{blacklistId}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "移除成功"
}
```

### 3.4 导出黑名单
```
GET /api/blacklist/users/export
```

**返回：Excel文件下载**

## 4. 统计分析接口

### 4.1 获取违规统计数据
```
GET /api/violation-settings/statistics
```

**请求参数：**
```json
{
  "startTime": "2024-01-01",
  "endTime": "2024-01-31",
  "roomAreaId": "楼栋ID（可选）"
}
```

**返回参数：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "totalViolations": 150,
    "autoBlacklistCount": 25,
    "topViolationRooms": [
      {
        "roomName": "多媒体教室（101）",
        "violationCount": 15
      }
    ],
    "violationTrend": [
      {
        "date": "2024-01-01",
        "count": 5
      }
    ]
  }
}
```

## 5. 自动化处理逻辑

### 5.1 违规检测机制
- 系统定时任务检测预约使用情况
- 当用户超过设定时间未签到时，记录违规
- 累计违规次数达到阈值时，自动加入黑名单

### 5.2 黑名单生效机制
- 用户在黑名单期间无法进行新的预约
- 已有的预约可以正常使用
- 管理员可以手动移除黑名单

## 6. 数据库索引建议

```sql
-- 违规设置表索引
CREATE INDEX idx_violation_setting_room ON tb_violation_setting(room_id, cstm_id);

-- 违规记录表索引
CREATE INDEX idx_violation_record_user ON tb_user_violation_record(user_id, cstm_id);
CREATE INDEX idx_violation_record_room ON tb_user_violation_record(room_id, violation_time);
CREATE INDEX idx_violation_record_time ON tb_user_violation_record(violation_time);

-- 黑名单表索引
CREATE INDEX idx_blacklist_user ON tb_user_blacklist(user_id, cstm_id, is_active);
CREATE INDEX idx_blacklist_type ON tb_user_blacklist(blacklist_type, create_time);
```