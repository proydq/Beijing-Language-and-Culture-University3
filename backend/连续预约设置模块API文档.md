# 连续预约设置模块API文档

## 1. 获取区域树形结构

### 接口描述
获取区域的树形结构数据，用于楼层筛选等功能。

### 请求信息
- **接口路径**: `GET /area/tree`
- **请求方法**: GET
- **请求参数**: 无

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "721757b0b0984404ba0c0eb00589377c",
      "areaName": "全部",
      "parentId": "",
      "pathDeep": 1,
      "type": null,
      "cstmId": "demo",
      "createTime": 1605765029430,
      "lastUpdateTime": 1605765029430,
      "extend1": null,
      "extend2": null,
      "children": [
        {
          "id": "07009425a4a6470ab4194d59a1ceb11b",
          "areaName": "全部教室",
          "parentId": "721757b0b0984404ba0c0eb00589377c",
          "pathDeep": 2,
          "type": null,
          "cstmId": "demo",
          "createTime": 1638846393898,
          "lastUpdateTime": 1732255026708,
          "extend1": null,
          "extend2": null,
          "children": []
        },
        {
          "id": "257c86b0d0a242cab9c70125498274b8",
          "areaName": "123",
          "parentId": "721757b0b0984404ba0c0eb00589377c",
          "pathDeep": 2,
          "type": null,
          "cstmId": "demo",
          "createTime": 1752804350834,
          "lastUpdateTime": 1752804350834,
          "extend1": null,
          "extend2": null,
          "children": []
        }
      ]
    }
  ]
}
```

#### 失败响应
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 区域ID |
| areaName | String | 区域名称 |
| parentId | String | 父区域ID |
| pathDeep | Integer | 路径深度 |
| type | String | 区域类型 |
| cstmId | String | 客户域 |
| createTime | Long | 创建时间（时间戳） |
| lastUpdateTime | Long | 最后更新时间（时间戳） |
| extend1 | String | 扩展字段1 |
| extend2 | String | 扩展字段2 |
| children | Array | 子区域列表 |

---

## 2. 获取教室连续预约设置列表

### 接口描述
获取当前客户域下所有教室的连续预约设置。

### 请求信息
- **接口路径**: `GET /continuous-booking-settings`
- **请求方法**: GET
- **请求参数**: 无

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "cbs001",
      "roomId": "room001",
      "continuousDays": 7,
      "continuousType": "DAYS",
      "isUnlimited": false,
      "canContinuous": true,
      "cstmId": "demo",
      "createTime": 1605765029430,
      "lastUpdateTime": 1605765029430
    }
  ]
}
```

### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 设置ID |
| roomId | String | 房间ID |
| continuousDays | Integer | 可连续预约天数 |
| continuousType | String | 连续类型（DAYS/WEEKS/MONTHS） |
| isUnlimited | Boolean | 是否无限制 |
| canContinuous | Boolean | 是否允许连续预约 |
| cstmId | String | 客户域 |
| createTime | Long | 创建时间（时间戳） |
| lastUpdateTime | Long | 最后更新时间（时间戳） |

---

## 3. 根据房间ID获取连续预约设置

### 接口描述
根据房间ID获取该房间的连续预约设置，如果不存在则创建默认设置。

### 请求信息
- **接口路径**: `GET /continuous-booking-settings/{roomId}`
- **请求方法**: GET
- **路径参数**:
  - `roomId`: 房间ID

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "cbs001",
    "roomId": "room001",
    "continuousDays": 7,
    "continuousType": "DAYS",
    "isUnlimited": false,
    "canContinuous": true,
    "cstmId": "demo",
    "createTime": 1605765029430,
    "lastUpdateTime": 1605765029430
  }
}
```

---

## 4. 更新单个教室连续预约设置

### 接口描述
更新指定房间的连续预约设置。

### 请求信息
- **接口路径**: `PUT /continuous-booking-settings/{roomId}`
- **请求方法**: PUT
- **路径参数**:
  - `roomId`: 房间ID
- **请求体**:

```json
{
  "continuousDays": 30,
  "continuousType": "DAYS",
  "isUnlimited": false,
  "canContinuous": true
}
```

### 请求参数说明

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| continuousDays | Integer | 否 | 可连续预约天数 |
| continuousType | String | 否 | 连续类型（DAYS/WEEKS/MONTHS） |
| isUnlimited | Boolean | 否 | 是否无限制 |
| canContinuous | Boolean | 否 | 是否允许连续预约 |

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "设置成功",
  "data": null
}
```

#### 失败响应
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

---

## 5. 批量更新教室连续预约设置

### 接口描述
批量更新多个房间的连续预约设置。

### 请求信息
- **接口路径**: `POST /continuous-booking-settings/batch-update`
- **请求方法**: POST
- **请求体**:

```json
{
  "roomIds": ["room123", "room456"],
  "continuousDays": 30,
  "continuousType": "DAYS",
  "isUnlimited": false,
  "canContinuous": true
}
```

### 请求参数说明

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roomIds | Array | 是 | 房间ID列表 |
| continuousDays | Integer | 否 | 可连续预约天数 |
| continuousType | String | 否 | 连续类型（DAYS/WEEKS/MONTHS） |
| isUnlimited | Boolean | 否 | 是否无限制 |
| canContinuous | Boolean | 否 | 是否允许连续预约 |

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "批量设置成功",
  "data": null
}
```

#### 失败响应
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

---

## 6. 获取楼层选项列表

### 接口描述
获取楼层类型的区域列表，用于楼层筛选下拉框。

### 请求信息
- **接口路径**: `GET /continuous-booking-settings/floor-options`
- **请求方法**: GET
- **请求参数**: 无

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "floor001",
      "areaName": "一楼",
      "parentId": "building001",
      "pathDeep": 2,
      "type": "floor",
      "cstmId": "demo",
      "createTime": 1605765029430,
      "lastUpdateTime": 1605765029430,
      "extend1": null,
      "extend2": null,
      "children": null
    }
  ]
}
```

---

## 7. 删除房间连续预约设置

### 接口描述
删除指定房间的连续预约设置。

### 请求信息
- **接口路径**: `DELETE /continuous-booking-settings/{roomId}`
- **请求方法**: DELETE
- **路径参数**:
  - `roomId`: 房间ID

### 响应信息

#### 成功响应
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 失败响应
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

---

## 数据字典

### 连续类型（ContinuousType）

| 值 | 说明 |
|----|------|
| DAYS | 天数 |
| WEEKS | 周数 |
| MONTHS | 月数 |

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 500 | 服务器内部错误 |

### 注意事项

1. 所有接口都需要进行身份认证，请在请求头中携带有效的JWT令牌。
2. 客户域（cstmId）会自动从当前登录用户的上下文中获取，无需手动传递。
3. 时间字段使用时间戳格式（毫秒）。
4. 如果房间不存在连续预约设置，系统会自动创建默认设置（7天，允许连续预约）。
5. 批量更新操作会自动处理不存在设置的房间，为其创建新的设置记录。