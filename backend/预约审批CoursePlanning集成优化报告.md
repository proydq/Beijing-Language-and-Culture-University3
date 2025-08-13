# 预约审批CoursePlanning集成优化报告

## 📋 优化概述

基于学校现有的课程时间配置表和校历信息，优化了预约审批通过后自动创建CoursePlanning记录的功能，使生成的课程排课数据更加准确和规范。

## 🔧 涉及的实体类

### 1. TbAttendanceAdministrativeCourseTimeConfig (课程时间配置表)
- **用途**: 定义学校的标准课程节次时间安排
- **关键字段**:
  - `courseSort`: 课程节次序号
  - `courseStartTime`: 节次开始时间 (HH:mm格式)
  - `courseEndTime`: 节次结束时间 (HH:mm格式)
  - `timeType`: 时间类型 (1=上午, 2=中午, 3=晚上)

### 2. SchoolCalendar2 (教学周信息表)
- **用途**: 定义学校的教学周安排
- **关键字段**:
  - `xn`: 学年名称
  - `xq`: 学期代码 (1=上学期, 2=下学期)
  - `week`: 教学周周次
  - `startDate/endDate`: 教学周起止日期

### 3. CoursePlanning (课程排课表)
- **用途**: 存储实际的课程排课信息
- **优化字段**:
  - `courseSection`: 精确的课程节次
  - `weekNumber`: 详细的教学周信息

## ⚡ 核心优化功能

### 1. 精确课程节次计算 (`calculateCourseSection`)

**优化前**:
```java
// 简单的时间判断
if (hour < 8) return 1L;
else if (hour < 10) return 2L;
// ...
```

**优化后**:
```java
// 查询课程时间配置表，精确匹配
List<TbAttendanceAdministrativeCourseTimeConfig> timeConfigs = 
    courseTimeConfigRepository.findByCstmIdOrderByCourseSort(customerId);

// 匹配预约时间到具体节次
for (TbAttendanceAdministrativeCourseTimeConfig config : timeConfigs) {
    if (isTimeInRange(timeStr, configStartTime, configEndTime)) {
        return config.getCourseSort().longValue();
    }
}
```

**改进效果**:
- ✅ 根据学校实际课程安排计算节次
- ✅ 支持不规则的课程时间段
- ✅ 提供默认兜底逻辑

### 2. 真实教学周查询 (`calculateWeekNumber`)

**优化前**:
```java
coursePlanning.setWeekNumber("临时预约");
```

**优化后**:
```java
// 查询SchoolCalendar2获取真实教学周信息
Optional<SchoolCalendar2> schoolCalendarOpt = schoolCalendar2Repository
    .findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(
        targetDate, targetDate, customerId);

// 格式化为: "2023-2024-上学期第5周"
String weekInfo = String.format("%s-%s学期第%d周", 
    schoolCalendar.getXn(),                    // 学年 
    schoolCalendar.getXq() == 1 ? "上" : "下",  // 学期
    schoolCalendar.getWeek());                 // 教学周
```

**改进效果**:
- ✅ 显示真实的学年学期信息
- ✅ 精确的教学周数据
- ✅ 区分教学周和非教学周

## 📊 数据映射对比

| 优化项目 | 优化前 | 优化后 |
|---------|--------|--------|
| **课程节次** | 简单时间判断<br>(8-10点=第2节) | 配置表精确匹配<br>(根据实际时间段) |
| **教学周** | 固定"临时预约" | 动态查询<br>"2023-2024-上学期第5周" |
| **容错性** | 基础容错 | 多层次容错<br>(配置查询→默认计算) |

## 🔄 集成触发时机

优化后的功能在以下时机自动创建CoursePlanning记录:

1. ✅ **多级审批最终通过**: 所有审批级别完成后
2. ✅ **无需审批直接通过**: 教室配置无需审批
3. ✅ **无审批级别配置**: 未配置具体审批级别  
4. ✅ **单级审批通过**: 只有一级审批且通过

## 🚀 使用示例

### 场景示例
- **预约时间**: 2024-01-15 14:30-16:00
- **教室**: A101多媒体教室
- **申请人**: 张老师

### 优化前生成结果
```json
{
    "courseSection": 5,           // 简单计算: 14点=第5节
    "weekNumber": "临时预约",      // 固定值
    "courseName": "学术会议",
    "teacherName": "张老师"
}
```

### 优化后生成结果  
```json
{
    "courseSection": 7,                    // 配置匹配: 14:30属于第7节
    "weekNumber": "2023-2024-上学期第3周",   // 真实教学周
    "courseName": "学术会议", 
    "teacherName": "张老师"
}
```

## 🛡️ 异常处理机制

1. **配置查询失败**: 自动降级到默认计算逻辑
2. **教学周查询失败**: 返回"非教学周"或"临时预约"
3. **主流程保护**: CoursePlanning创建失败不影响预约审批
4. **详细日志**: 便于问题排查和监控

## ✅ 部署状态

- [x] 代码优化完成
- [x] 编译测试通过
- [x] 异常处理完善
- [x] 日志记录详细
- [x] 向后兼容保证

## 📝 配置要求

使用优化功能需要确保以下数据完整:

1. **TbAttendanceAdministrativeCourseTimeConfig表**: 配置各课程节次的具体时间
2. **SchoolCalendar2表**: 维护教学周日历信息  
3. **相关Repository**: 确保注入成功

## 🎯 优化效果总结

通过结合学校现有的课程时间配置和校历信息，实现了:

- **🎯 精确性**: 课程节次和教学周信息更加准确
- **📚 规范性**: 符合学校实际教学安排
- **🔧 灵活性**: 支持不同学校的时间配置
- **🛡️ 稳定性**: 完善的异常处理和降级机制
- **📊 可追溯**: 详细的日志记录

---

*生成时间: 2024-01-15*  
*优化版本: v2.0*