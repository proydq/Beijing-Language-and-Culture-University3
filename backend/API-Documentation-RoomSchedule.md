# 教室课表API文档

## 概述
此API提供教室课表相关的查询功能，包括教学周信息查询和教室课表详情查询，主要为前端课表矩阵渲染和时间选择器提供数据支持。

## 接口详情

### 1. 获取所有教学周列表
**接口地址：** `GET /api/room-schedule/weeks`

**请求参数：** 无

**响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "week001",              // 教学周ID
      "week": 1,                   // 教学周数
      "weekName": "第1周",         // 教学周名称
      "xn": "2024-2025",          // 学年名称
      "xq": 1,                    // 学期代码 (1:第一学期, 2:第二学期)
      "startDate": "2025-01-01",  // 教学周开始日期
      "endDate": "2025-01-07"     // 教学周结束日期
    },
    {
      "id": "week002",
      "week": 2,
      "weekName": "第2周",
      "xn": "2024-2025",
      "xq": 1,
      "startDate": "2025-01-08",
      "endDate": "2025-01-14"
    }
  ]
}
```

### 2. 获取教室周课表详情
**接口地址：** `POST /api/room-schedule/week-detail`

**请求参数：**
```json
{
  "classRoomId": "string",  // 教室ID (必填)
  "timestamp": 1640995200000  // 时间戳 (必填)
}
```

**响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "week": 1,                    // 教学周数
    "weekName": "第1周",          // 教学周名称
    "startDate": "2025-01-01",    // 教学周开始日期
    "endDate": "2025-01-07",      // 教学周结束日期
    "courseTimeConfigList": [     // 课程时间配置列表
      {
        "id": "config1",
        "courseSo": 1,
        "startTime": "08:00",
        "endTime": "08:45",
        "cstmId": "customer1"
      }
    ],
    "coursePlanningList": [       // 课程排课列表
      {
        "id": "course1",
        "classRoomId": "room001",
        "courseDate": "2025-01-01",
        "courseTime": 1,
        "courseName": "数学"
      }
    ]
  }
}
```

## 错误响应
```json
{
  "code": 500,
  "message": "查询失败: 错误详情",
  "data": null
}
```

## 业务逻辑说明

1. **时间戳转换**：将前端传入的时间戳转换为Date对象
2. **教学周查询**：根据目标日期和客户ID查询对应的教学周信息
3. **课程时间配置查询**：获取该客户的所有课程时间配置，按课程序号排序
4. **排课信息查询**：查询指定教室在该教学周内的所有排课信息
5. **数据封装**：将查询结果封装为统一的响应格式

## 使用场景

1. **教学周列表接口**：
   - 前端时间选择弹窗的教学周下拉列表
   - 课表查询页面的周选择器
   - 教务管理系统的周导航

2. **教室课表详情接口**：
   - 前端时间选择弹窗的课表矩阵渲染
   - 教室预约系统的时间冲突检查
   - 课程安排的可视化展示

## 注意事项

1. 需要用户认证，接口会自动获取当前用户的客户ID
2. 如果查询不到对应的教学周信息，返回空的VO对象
3. 所有异常都会被捕获并返回友好的错误信息
4. 支持跨域访问

## 示例调用

### 获取教学周列表
```bash
curl -X GET "http://localhost:8080/api/room-schedule/weeks" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 获取教室周课表详情
```bash
curl -X POST "http://localhost:8080/api/room-schedule/week-detail" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "classRoomId": "room001",
    "timestamp": 1640995200000
  }'
```

### JavaScript 示例
```javascript
// 获取教学周列表
const weeksResponse = await fetch('/api/room-schedule/weeks', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token
  }
});
const weeks = await weeksResponse.json();
console.log('教学周列表:', weeks);

// 获取教室课表详情
const scheduleResponse = await fetch('/api/room-schedule/week-detail', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    classRoomId: 'room001',
    timestamp: Date.now()
  })
});
const schedule = await scheduleResponse.json();
console.log('课表详情:', schedule);
```