# Area 区域管理模块 API 文档

## 模块概述

Area 区域管理模块用于管理楼宇、楼层、房间等层级结构的区域信息，支持树形结构的完整 CRUD 操作。

## 数据库表结构

### area 表

| 字段名 | 类型 | 说明 | 备注 |
|--------|------|------|------|
| id | VARCHAR(32) | 主键UUID | 主键 |
| area_name | VARCHAR(100) | 区域名称 | 必填 |
| parent_id | VARCHAR(32) | 父节点ID | 可空，根节点为空 |
| path_deep | INT | 路径深度 | 自动计算 |
| type | VARCHAR(50) | 类型 | building/floor/room |
| cstm_id | VARCHAR(32) | 客户域 | 默认 'demo' |
| create_time | BIGINT | 创建时间 | 时间戳 |
| last_update_time | BIGINT | 最后更新时间 | 时间戳 |
| extend1 | VARCHAR(255) | 扩展字段1 | 可空 |
| extend2 | VARCHAR(255) | 扩展字段2 | 可空 |

## API 接口列表

### 1. 获取区域树形结构

**接口地址：** `GET /area/tree`

**请求参数：**
- `cstmId` (可选): 客户域ID
- `type` (可选): 区域类型筛选

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "uuid1",
      "areaName": "总部大楼",
      "parentId": null,
      "pathDeep": 1,
      "type": "building",
      "createTime": 1640000000000,
      "lastUpdateTime": 1640000000000,
      "children": [
        {
          "id": "uuid2",
          "areaName": "1楼",
          "parentId": "uuid1",
          "pathDeep": 2,
          "type": "floor",
          "children": []
        }
      ]
    }
  ]
}
```

### 2. 获取单个区域详情

**接口地址：** `GET /area/{id}`

**路径参数：**
- `id`: 区域ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "uuid1",
    "areaName": "总部大楼",
    "parentId": null,
    "pathDeep": 1,
    "type": "building",
    "cstmId": "demo",
    "createTime": 1640000000000,
    "lastUpdateTime": 1640000000000,
    "extend1": null,
    "extend2": null
  }
}
```

### 3. 新增区域

**接口地址：** `POST /area`

**请求体：**
```json
{
  "areaName": "新建楼宇",
  "parentId": null,
  "type": "building",
  "extend1": "扩展信息1",
  "extend2": "扩展信息2"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "uuid3",
    "areaName": "新建楼宇",
    "parentId": null,
    "pathDeep": 1,
    "type": "building",
    "cstmId": "demo",
    "createTime": 1640000000000,
    "lastUpdateTime": 1640000000000,
    "extend1": "扩展信息1",
    "extend2": "扩展信息2"
  }
}
```

### 4. 更新区域

**接口地址：** `PUT /area/{id}`

**路径参数：**
- `id`: 区域ID

**请求体：**
```json
{
  "areaName": "更新后的名称",
  "type": "building",
  "extend1": "更新的扩展信息1"
}
```

### 5. 删除区域

**接口地址：** `DELETE /area/{id}`

**路径参数：**
- `id`: 区域ID

**说明：** 仅删除当前节点，如果有子节点则不允许删除

### 6. 级联删除区域

**接口地址：** `DELETE /area/{id}/cascade`

**路径参数：**
- `id`: 区域ID

**说明：** 删除当前节点及其所有子节点

### 7. 分页查询区域

**接口地址：** `POST /area/search`

**请求体：**
```json
{
  "areaName": "搜索关键词",
  "parentId": "父节点ID",
  "type": "building",
  "pathDeep": 1,
  "extend1": "扩展字段1",
  "extend2": "扩展字段2",
  "pageNumber": 1,
  "pageSize": 10
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": "uuid1",
        "areaName": "总部大楼",
        "parentId": null,
        "pathDeep": 1,
        "type": "building",
        "count": 5,
        "createTime": 1640000000000,
        "lastUpdateTime": 1640000000000
      }
    ]
  }
}
```

## 业务规则

### 1. 路径深度自动计算
- 根节点深度为 1
- 子节点深度 = 父节点深度 + 1
- 新增节点时自动计算并设置

### 2. 客户域隔离
- 所有操作自动设置当前用户的客户域
- 查询时只返回当前客户域的数据
- 确保多租户数据隔离

### 3. 删除规则
- 普通删除：有子节点时不允许删除
- 级联删除：删除节点及其所有子节点
- 删除前会进行权限验证

### 4. 时间戳处理
- 创建时自动设置 createTime 和 lastUpdateTime
- 更新时自动更新 lastUpdateTime
- 使用长整型时间戳格式

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 500 | 服务器内部错误 |

## 常见错误信息

- "区域不存在" - 查询或操作的区域ID不存在
- "存在子节点，无法删除" - 尝试删除有子节点的区域
- "父节点不存在" - 指定的父节点ID不存在

## 使用示例

### 前端调用示例

```javascript
// 获取区域树
const getAreaTree = async () => {
  const response = await fetch('/area/tree');
  const result = await response.json();
  return result.data;
};

// 新增区域
const addArea = async (areaData) => {
  const response = await fetch('/area', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(areaData)
  });
  return await response.json();
};

// 分页查询
const searchAreas = async (condition) => {
  const response = await fetch('/area/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(condition)
  });
  return await response.json();
};
```

## 注意事项

1. **权限控制**：所有接口都需要用户登录认证
2. **数据隔离**：自动根据用户客户域进行数据隔离
3. **事务处理**：涉及数据修改的操作都使用事务保证数据一致性
4. **性能优化**：树形结构查询已优化，避免N+1查询问题
5. **扩展性**：预留了 extend1 和 extend2 字段用于业务扩展

## 部署说明

1. 确保数据库中已创建 `area` 表
2. 配置好 Spring Security 认证
3. 确保 `SecurityUtil.getCustomerId()` 方法可正常获取用户客户域
4. 建议为 `area_name`、`parent_id`、`type`、`cstm_id` 字段添加数据库索引以提升查询性能