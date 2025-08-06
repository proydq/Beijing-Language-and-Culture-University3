# 远程开门记录API文档 - 简化版

## 概述

远程开门记录功能是基于现有教室出入记录系统的扩展，通过复用现有接口并固定 `accessType = "远程开门"` 来实现。这种设计避免了重复开发，保持了系统的一致性。

## 设计理念

**核心思想**: 远程开门记录实际上就是教室出入记录的一个子集，只是开门方式都是远程开门。

**实现方式**: 
- 复用现有的 `AccessRecordsRequest` 和 `AccessRecordResponse`
- 在查询时固定 `accessType = "远程开门"`
- 通过数据转换适配远程开门记录的特殊字段需求

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

**实现原理**:
1. 将 `RemoteAccessRecordsRequest` 转换为 `AccessRecordsRequest`
2. 固定设置 `accessType = "远程开门"`
3. 调用现有的 `getAccessRecords()` 方法
4. 将 `AccessRecordResponse` 转换为 `RemoteAccessRecordResponse`

### 2. 导出远程开门记录

**接口描述**: 导出远程开门记录数据到Excel文件。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records/export`

**实现原理**:
1. 转换为 `ExportAccessRecordsRequest`，固定 `accessType = "远程开门"`
2. 调用现有的 `exportAccessRecords()` 方法
3. 修改文件名为"远程开门记录"

### 3. 获取远程开门统计信息

**接口描述**: 获取远程开门记录的统计数据。

**请求方式**: `GET`  
**请求路径**: `/api/room-booking/remote-access-records/stats`

**实现原理**:
- 基于现有统计接口，提供模拟的远程开门统计数据
- 返回开门方式分布、操作员类型分布、小时分布等统计信息

### 4. 执行远程开门操作

**接口描述**: 管理员执行远程开门操作，并记录操作日志。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records/open-door`

**核心功能**:
1. 记录操作日志到 `tb_remote_operation_log` 表
2. 创建出入记录到 `tb_room_access_record` 表，`accessType = "远程开门"`
3. 支持事务回滚确保数据一致性

### 5. 获取远程开门操作日志

**接口描述**: 获取远程开门操作的详细日志记录。

**请求方式**: `POST`  
**请求路径**: `/api/room-booking/remote-access-records/operation-logs`

**功能特点**:
- 查询 `tb_remote_operation_log` 表
- 支持按操作员、教室、时间范围筛选
- 用于审计和监控目的

### 6. 获取可远程开门的教室列表

**接口描述**: 获取当前用户有权限进行远程开门的教室列表。

**请求方式**: `GET`  
**请求路径**: `/api/room-booking/remote-access-records/available-rooms`

**实现原理**:
- 复用现有的 `getRoomStatus()` 方法
- 支持按状态筛选（使用中/空闲/全部）

## 技术优势

### 1. 代码复用
- **减少重复代码**: 复用现有的查询、分页、筛选逻辑
- **保持一致性**: 与现有教室出入记录接口行为一致
- **降低维护成本**: 修改通用逻辑时自动适用于远程开门记录

### 2. 数据统一
- **统一数据模型**: 远程开门记录和普通出入记录使用相同的数据表
- **简化数据库设计**: 避免创建冗余的数据表
- **便于数据分析**: 可以统一分析所有类型的出入记录

### 3. 灵活扩展
- **支持新的开门方式**: 只需在现有字段中添加新的枚举值
- **便于权限控制**: 可以基于 `accessType` 进行细粒度权限控制
- **易于报表统计**: 可以按 `accessType` 分组进行各种维度的统计

## 核心实现代码

### Service层关键方法

```java
@Override
public ResponsePageDataEntity<RemoteAccessRecordResponse> getRemoteAccessRecords(RemoteAccessRecordsRequest request) {
    // 转换为通用的教室出入记录请求
    AccessRecordsRequest accessRequest = new AccessRecordsRequest();
    // ... 设置查询参数
    accessRequest.setAccessType("远程开门"); // 固定为远程开门
    
    // 调用现有的教室出入记录查询方法
    ResponsePageDataEntity<AccessRecordResponse> accessRecords = getAccessRecords(accessRequest);
    
    // 转换响应格式
    List<RemoteAccessRecordResponse> responseList = accessRecords.getRows().stream()
        .map(this::convertAccessRecordToRemoteAccessRecord)
        .collect(Collectors.toList());
    
    // 返回结果
    ResponsePageDataEntity<RemoteAccessRecordResponse> result = new ResponsePageDataEntity<>();
    result.setTotal(accessRecords.getTotal());
    result.setRows(responseList);
    return result;
}
```

### 数据转换方法

```java
private RemoteAccessRecordResponse convertAccessRecordToRemoteAccessRecord(AccessRecordResponse accessRecord) {
    return RemoteAccessRecordResponse.builder()
        // ... 复制所有通用字段
        .operatorId(accessRecord.getUserId()) // 使用userId作为operatorId
        .operatorName(accessRecord.getName()) // 使用name作为operatorName
        .operatorType("管理员") // 设置默认操作员类型
        .reason("远程开门操作") // 设置默认原因
        .build();
}
```

## 数据库设计

### 现有表扩展
- **tb_room_access_record**: 添加了远程开门相关字段
  - `operator_id`: 实际操作员ID
  - `operator_name`: 实际操作员姓名
  - `operator_type`: 操作员类型
  - `reason`: 开门原因

### 新增表
- **tb_remote_operation_log**: 远程操作日志表
  - 记录所有远程开门操作的详细信息
  - 用于审计和监控

## 使用建议

### 1. 前端调用
```javascript
// 获取远程开门记录
const response = await api.post('/api/room-booking/remote-access-records', {
  pageNum: 1,
  pageSize: 10,
  // accessType 会自动设置为"远程开门"，不需要前端传递
});
```

### 2. 筛选条件
- 所有现有的教室出入记录筛选条件都适用
- 系统会自动过滤出 `accessType = "远程开门"` 的记录

### 3. 权限控制
- 可以在Controller层添加权限注解
- 可以基于用户角色限制可查看的记录范围

## 扩展方向

### 1. 实时统计
如果需要实时的远程开门统计，可以考虑：
- 在Repository层添加专门的统计查询方法
- 使用缓存提高统计查询性能

### 2. 权限细化
可以进一步细化权限控制：
- 按楼栋/区域限制可操作的教室
- 按时间段限制远程开门权限
- 添加审批流程

### 3. 监控告警
- 异常开门行为监控
- 频繁开门告警
- 操作日志异常分析

## 总结

通过复用现有的教室出入记录接口，我们实现了一个轻量级、高效的远程开门记录管理系统。这种设计方案具有以下优点：

1. **开发效率高**: 大量复用现有代码，快速实现功能
2. **维护成本低**: 统一的数据模型和业务逻辑
3. **扩展性好**: 可以轻松添加新的记录类型
4. **一致性强**: 与现有系统行为保持一致

这种基于现有系统扩展的设计理念，在企业级应用开发中是一个很好的实践案例。