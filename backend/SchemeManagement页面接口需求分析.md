# SchemeManagement.vue 页面后端接口需求分析

## 业务背景

基于 `SchemeManagement.vue` 页面的功能需求，该页面主要用于教室管理和审批权限配置。根据用户描述的业务逻辑：

1. **手动同步接口**：从 `Room` 实体中同步所有 `roomTypeName` 为 "教室" 的数据
2. **添加教室接口**：添加教室后需要同时在 `Room` 实体中新增对应记录，`roomTypeName` 字段设置为 "教室"

## 现有接口分析

### 已存在的接口

1. **RoomController** - 房间管理控制器
   - `/api/room/search` - 分页查询房间列表
   - `/api/room` - 保存房间（新增或编辑）
   - `/api/room/{id}` - 根据ID查询房间详情
   - `/api/room/{id}` - 删除房间
   - `/api/room/batch` - 批量删除房间
   - `/api/room/types` - 获取房屋类型列表
   - `/api/room/import` - 导入房间数据
   - `/api/room/export` - 导出房间数据

2. **AreaController** - 区域管理控制器
   - `getAreaTree` - 获取楼栋架构树数据

## 新增接口需求

### 1. 教室管理接口

#### 1.1 获取教室列表
- **接口路径：** `/scheme-management/classrooms/list`
- **请求方式：** POST
- **功能：** 分页获取教室列表（仅查询roomTypeName为"教室"的数据）
- **请求参数：**
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "roomName": "教室名称（可选）",
  "approvalFilter": "all/yes/no（可选）",
  "roomAreaId": "楼栋ID（可选）"
}
```
- **返回参数：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 201,
    "rows": [
      {
        "id": "教室ID",
        "roomName": "教室名称",
        "roomNo": "房间号",
        "roomAreaName": "所属楼",
        "isExchange": "yes/no",
        "needApproval": "yes/no",
        "firstApprover": "第一级审批人",
        "secondApprover": "第二级审批人",
        "thirdApprover": "第三级审批人",
        "roomCode": "教室编号",
        "roomVolume": "容纳人数",
        "remark": "备注"
      }
    ]
  }
}
```

#### 1.2 添加教室
- **接口路径：** `/scheme-management/classrooms/add`
- **请求方式：** POST
- **功能：** 添加新教室（同时在Room表中新增记录）
- **请求参数：**
```json
{
  "classroomName": "教室名称",
  "roomNumber": "房间号",
  "classroomCode": "教室编号",
  "building": "所属楼",
  "roomAreaId": "区域ID",
  "remarks": "备注",
  "roomVolume": "容纳人数",
  "allowBookerSelectApprover": "yes/no",
  "approvalLevels": [
    {
      "level": 1,
      "approvers": "审批人列表（逗号分隔）"
    }
  ]
}
```
- **返回参数：**
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": "新生成的教室ID"
  }
}
```

#### 1.3 更新教室信息
- **接口路径：** `/scheme-management/classrooms/update`
- **请求方式：** PUT
- **功能：** 更新教室信息和审批设置
- **请求参数：**
```json
{
  "id": "教室ID",
  "classroomName": "教室名称",
  "roomNumber": "房间号",
  "building": "所属楼",
  "roomAreaId": "区域ID",
  "allowBookerSelectApprover": "yes/no",
  "approvalLevels": [
    {
      "level": 1,
      "approvers": "审批人列表（逗号分隔）"
    }
  ]
}
```
- **返回参数：**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

#### 1.4 批量删除教室
- **接口路径：** `/scheme-management/classrooms/batch-delete`
- **请求方式：** DELETE
- **功能：** 批量删除教室
- **请求参数：**
```json
{
  "ids": ["教室ID1", "教室ID2"]
}
```
- **返回参数：**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

#### 1.5 批量设置审批权限
- **接口路径：** `/scheme-management/classrooms/batch-approval`
- **请求方式：** PUT
- **功能：** 批量设置教室审批权限
- **请求参数：**
```json
{
  "roomIds": ["教室ID1", "教室ID2"],
  "allowBookerSelectApprover": "yes/no",
  "approvalLevels": [
    {
      "level": 1,
      "approvers": "审批人列表（逗号分隔）"
    }
  ]
}
```
- **返回参数：**
```json
{
  "code": 200,
  "message": "批量设置成功"
}
```

#### 1.6 手动同步教室数据
- **接口路径：** `/scheme-management/classrooms/sync`
- **请求方式：** POST
- **功能：** 从Room表同步所有roomTypeName为"教室"的数据
- **请求参数：** 无
- **返回参数：**
```json
{
  "code": 200,
  "message": "同步成功",
  "data": {
    "syncCount": 5
  }
}
```

### 2. 人员管理接口

#### 2.1 获取人员列表
- **接口路径：** `/scheme-management/personnel/list`
- **请求方式：** POST
- **功能：** 分页获取人员列表（用于审批人选择）
- **请求参数：**
```json
{
  "pageNumber": 1,
  "pageSize": 10,
  "name": "姓名（可选）",
  "department": "部门（可选）"
}
```
- **返回参数：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": "人员ID",
        "name": "姓名",
        "department": "部门",
        "position": "职位",
        "email": "邮箱",
        "phone": "电话"
      }
    ]
  }
}
```

### 3. 数据导入导出接口

#### 3.1 导出教室数据
- **接口路径：** `/scheme-management/classrooms/export`
- **请求方式：** POST
- **功能：** 导出选中的教室数据
- **请求参数：**
```json
{
  "roomIds": ["教室ID1", "教室ID2"]
}
```
- **返回参数：** 文件下载流

#### 3.2 导入教室数据
- **接口路径：** `/scheme-management/classrooms/import`
- **请求方式：** POST
- **功能：** 批量导入教室数据
- **请求参数：** FormData（包含Excel文件）
- **返回参数：**
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

## 数据库设计考虑

### 1. Room实体扩展

现有的Room实体已经包含了基本的房间信息，但需要考虑以下扩展：

1. **审批相关字段**：可以使用现有的扩展字段或新增专门的审批配置表
2. **教室特有属性**：如设备信息、座位布局等

### 2. 审批配置表设计建议

```sql
-- 教室审批配置表
CREATE TABLE tb_classroom_approval_config (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    cstm_id VARCHAR(36) COMMENT '客户域ID',
    room_id VARCHAR(36) COMMENT '房间ID',
    allow_booker_select_approver TINYINT(1) DEFAULT 1 COMMENT '是否允许预约人自选审批人',
    need_approval TINYINT(1) DEFAULT 0 COMMENT '是否需要审批',
    create_time BIGINT COMMENT '创建时间',
    last_update_time BIGINT COMMENT '最后更新时间',
    INDEX idx_room_id (room_id),
    INDEX idx_cstm_id (cstm_id)
) COMMENT='教室审批配置表';

-- 审批级别配置表
CREATE TABLE tb_classroom_approval_level (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    cstm_id VARCHAR(36) COMMENT '客户域ID',
    config_id VARCHAR(36) COMMENT '审批配置ID',
    level_number INT COMMENT '审批级别（1,2,3）',
    approver_ids TEXT COMMENT '审批人ID列表（JSON格式）',
    approver_names TEXT COMMENT '审批人姓名列表（逗号分隔）',
    create_time BIGINT COMMENT '创建时间',
    INDEX idx_config_id (config_id),
    INDEX idx_cstm_id (cstm_id)
) COMMENT='审批级别配置表';
```

## 实现要点

### 1. 数据同步逻辑

手动同步接口需要：
1. 查询Room表中所有roomTypeName为"教室"的记录
2. 检查是否已存在对应的审批配置
3. 为新的教室创建默认的审批配置
4. 返回同步的数量

### 2. 添加教室逻辑

添加教室接口需要：
1. 在Room表中新增记录，roomTypeName设置为"教室"
2. 同时创建对应的审批配置记录
3. 保证数据一致性（使用事务）

### 3. 权限控制

所有接口都需要：
1. 客户域隔离（cstmId字段）
2. 用户权限验证
3. 操作日志记录

### 4. 异常处理

需要处理的异常情况：
1. 教室名称重复
2. 房间号重复
3. 区域不存在
4. 审批人不存在
5. 数据同步失败

## 总结

该接口设计方案充分考虑了SchemeManagement.vue页面的功能需求，同时与现有的Room实体进行了有效整合。通过新增专门的教室管理接口，既保持了与现有房间管理系统的兼容性，又满足了教室特有的审批权限配置需求。