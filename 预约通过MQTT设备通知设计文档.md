# 预约通过MQTT设备通知设计文档

## 1. 需求背景

当房间预约申请审批通过后，系统需要自动向与该房间绑定的终端设备发送通知，告知设备有新的预约安排。这可以让终端设备及时更新显示信息，展示最新的房间使用安排。

## 2. 当前系统分析

### 2.1 现有系统架构
- **预约系统**: 已实现完整的房间预约和审批流程
- **终端管理**: 已实现Terminal实体，支持终端与房间的绑定关系（extra_id2字段）
- **触发点**: RoomBookingServiceImpl.createCoursePlanningFromBooking()方法，在预约审批通过后调用
- **MQTT基础**: 项目中有完整的MQTT集成指南，但尚未实际实现

### 2.2 数据关联
```
RoomBooking (预约) → Room (房间) ← Terminal (终端设备)
     ↓                              ↑
  roomId                        extra_id2
```

### 2.3 关键业务流程
1. 预约审批通过
2. 创建CoursePlanning记录
3. **新增**: 查询房间绑定的终端设备
4. **新增**: 向终端设备发送MQTT通知

## 3. MQTT消息设计

### 3.1 服务和操作定义
```java
public class EnumService {
    // 新增：预约通知服务
    public static final String SERVICE_BOOKING_NOTIFICATION = "EnumBookingNotification";
    
    public static class EnumBookingNotification {
        // 预约审批通过通知
        public static final String BOOKING_APPROVED = "BOOKING_APPROVED";
        // 预约取消通知
        public static final String BOOKING_CANCELLED = "BOOKING_CANCELLED";
        // 预约更新通知
        public static final String BOOKING_UPDATED = "BOOKING_UPDATED";
    }
}
```

### 3.2 消息格式设计

#### 3.2.1 预约通过通知消息
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "service": "EnumBookingNotification",
    "operation": "BOOKING_APPROVED",
    "params": {
        "bookingId": "预约ID",
        "bookingName": "2024年度总结会议",
        "roomId": "房间ID",
        "roomName": "第一会议室",
        "applicantName": "张三",
        "applicantPhone": "13800138000",
        "department": "研发部",
        "timeSlots": [
            {
                "date": "2025-03-08",
                "startTime": "10:00:00",
                "endTime": "10:45:00",
                "weekDay": 6,
                "courseSection": 3
            },
            {
                "date": "2025-03-09",
                "startTime": "10:00:00",
                "endTime": "10:45:00",
                "weekDay": 7,
                "courseSection": 3
            }
        ],
        "description": "年度总结会议，请准时参加",
        "approvalTime": "2025-01-15 14:30:00",
        "createTime": "2025-01-15 10:00:00"
    }
}
```

#### 3.2.2 消息主题
- **单播主题**: `bt_client/{terminalId}`
- **QoS级别**: 1（至少一次传递，确保设备收到通知）

### 3.3 终端响应设计
终端设备收到通知后应该：
1. 解析消息内容
2. 更新本地预约信息缓存
3. 刷新显示界面
4. 可选：发送接收确认

## 4. 技术实现方案

### 4.1 依赖添加
```xml
<!-- Spring Integration MQTT -->
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-mqtt</artifactId>
    <version>5.5.15</version>
</dependency>

<!-- Eclipse Paho MQTT客户端 -->
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.2.5</version>
</dependency>
```

### 4.2 配置文件
```properties
# application.properties
mqtt.broker.url=tcp://localhost:1883
mqtt.client.id=room-booking-server
mqtt.username=admin
mqtt.password=admin123
mqtt.qos=1
mqtt.timeout=30
mqtt.keepalive=60
mqtt.automatic-reconnect=true
```

### 4.3 核心服务设计

#### 4.3.1 MQTT配置类
```java
@Configuration
@EnableIntegration
public class MqttConfig {
    // MQTT客户端工厂配置
    // 消息发送通道配置
    // 消息处理器配置
}
```

#### 4.3.2 设备通知服务
```java
@Service
public class DeviceNotificationService {
    
    @Autowired
    private TerminalService terminalService;
    
    @Autowired
    private MqttSendService mqttSendService;
    
    /**
     * 发送预约通过通知到房间绑定的设备
     */
    public void notifyBookingApproved(RoomBooking booking, List<BookingTimeSlot> timeSlots) {
        // 1. 查询房间绑定的终端
        List<Terminal> terminals = terminalService.findByRoomId(
            booking.getRoomId(), booking.getCstmId()
        );
        
        // 2. 构建通知消息
        MqttCmd cmd = buildBookingApprovedCommand(booking, timeSlots);
        
        // 3. 向每个在线终端发送通知
        for (Terminal terminal : terminals) {
            if (terminal.isOnline()) {
                sendToTerminal(cmd, terminal.getTerminalId());
            }
        }
    }
}
```

### 4.4 集成点修改
在RoomBookingServiceImpl.createCoursePlanningFromBooking()方法中添加：
```java
private void createCoursePlanningFromBooking(RoomBooking booking) {
    try {
        // ... 现有的CoursePlanning创建逻辑 ...
        
        // 新增：发送设备通知
        if (deviceNotificationService != null) {
            try {
                deviceNotificationService.notifyBookingApproved(booking, timeSlots);
                log.info("已发送预约通过通知到终端设备");
            } catch (Exception e) {
                log.error("发送设备通知失败，但不影响主流程", e);
            }
        }
        
    } catch (Exception e) {
        // ... 现有异常处理 ...
    }
}
```

## 5. 可行性分析

### 5.1 技术可行性
- ✅ **MQTT协议成熟**: Spring Integration MQTT提供完善的支持
- ✅ **数据模型就绪**: Terminal实体已支持房间绑定
- ✅ **集成点明确**: 在createCoursePlanningFromBooking中集成
- ✅ **参考实现完整**: 有详细的MQTT集成指南可参考

### 5.2 业务可行性
- ✅ **需求明确**: 预约通过后通知设备是合理需求
- ✅ **流程顺畅**: 不影响现有预约审批流程
- ✅ **容错设计**: MQTT发送失败不影响主业务
- ✅ **扩展性好**: 后续可支持更多通知类型

### 5.3 潜在风险
- ⚠️ **MQTT服务器依赖**: 需要部署和维护MQTT Broker
- ⚠️ **网络稳定性**: 设备需要稳定的网络连接
- ⚠️ **消息堆积**: 离线设备可能造成消息堆积
- ⚠️ **性能影响**: 大量设备时的并发发送

### 5.4 风险缓解措施
1. **异步发送**: 使用消息队列异步发送，避免阻塞主流程
2. **重试机制**: 实现智能重试，避免频繁重发
3. **离线消息**: 配置MQTT离线消息存储
4. **监控告警**: 添加MQTT连接和发送监控

## 6. 实施步骤

### 第一阶段：基础设施
1. 添加MQTT依赖
2. 部署MQTT Broker（推荐使用EMQX或Mosquitto）
3. 配置MQTT连接参数
4. 实现MqttConfig配置类

### 第二阶段：核心功能
1. 实现MqttCmd命令实体
2. 实现MqttSendService发送服务
3. 实现DeviceNotificationService通知服务
4. 定义消息格式和枚举

### 第三阶段：业务集成
1. 修改RoomBookingServiceImpl
2. 添加通知发送逻辑
3. 实现异常处理和日志记录
4. 添加配置开关

### 第四阶段：测试验证
1. 单元测试MQTT发送功能
2. 集成测试预约通过流程
3. 端到端测试设备接收
4. 性能测试和压力测试

### 第五阶段：监控运维
1. 添加MQTT连接监控
2. 实现消息发送统计
3. 配置告警规则
4. 编写运维文档

## 7. 扩展考虑

### 7.1 未来扩展功能
- 预约取消通知
- 预约变更通知
- 预约提醒（提前15分钟）
- 设备状态上报
- 双向通信（设备确认）

### 7.2 高级特性
- 消息优先级
- 批量发送优化
- 离线消息缓存
- 消息去重机制
- 加密传输

## 8. 总结

### 8.1 实现价值
1. **提升用户体验**: 设备实时显示最新预约信息
2. **自动化程度高**: 无需人工干预，自动通知
3. **信息同步及时**: 审批通过立即通知
4. **系统集成度好**: 与现有系统无缝集成

### 8.2 建议
1. **分阶段实施**: 先实现基础功能，再逐步完善
2. **充分测试**: 特别注意异常情况处理
3. **监控先行**: 部署前先建立监控体系
4. **文档完善**: 维护详细的技术和运维文档

### 8.3 结论
**该功能实现具有很高的可行性**，技术方案成熟，业务价值明确，风险可控。建议按照分阶段计划逐步实施，确保系统稳定性和可维护性。

---
*文档版本: v1.0*  
*创建日期: 2024-01-15*  
*作者: System Architect*