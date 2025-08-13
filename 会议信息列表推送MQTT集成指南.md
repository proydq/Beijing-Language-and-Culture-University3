# 会议信息列表推送MQTT集成指南

## 1. 概述

本文档详细说明了BunnyTouch BS 3.0项目中会议信息列表推送功能的完整实现流程，包括MQTT配置、消息格式、业务逻辑等各个方面。

## 2. 技术架构

### 2.1 核心依赖
```xml
<!-- Spring整合MQTT -->
<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-mqtt</artifactId>
    <version>4.3.9.RELEASE</version>
</dependency>

<!-- Eclipse Paho MQTT客户端 -->
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.0.2</version>
</dependency>
```

### 2.2 项目结构
```
src/main/
├── java/
│   ├── com/proshine/mqtt/
│   │   ├── entity/
│   │   │   ├── MqttCmd.java              # MQTT命令实体
│   │   │   └── enumer/
│   │   │       ├── EnumService.java      # 服务类型枚举
│   │   │       ├── EnumParamKey.java     # 参数键枚举
│   │   │       └── EnumUniformID.java    # 统一ID定义
│   │   └── service/
│   │       ├── MqttSendService.java      # 消息发送服务
│   │       └── MqttReceiveService.java   # 消息接收服务
│   └── net/bunnytouch/touchsys/terminal/
│       └── service/
│           └── TerminalControlService.java # 终端控制接口
└── resources/
    └── spring/
        ├── mqtt-config.properties         # MQTT配置
        └── spring-mqtt.xml               # Spring MQTT配置

```

## 3. MQTT配置

### 3.1 配置文件 (mqtt-config.properties)
```properties
# MQTT服务器地址
mqtt.url=tcp://localhost:18880

# 自动启动
mqtt.autoStartup=true

# 服务质量等级
mqtt.qos=1

# 发送超时时间（秒）
mqtt.sendTimeout=12

# 客户端ID（接收消息）
mqtt.clientId=CID_BT_SERVER_V4

# 客户端ID（发送消息）
mqtt.sendClientId=CID_BT_SERVER_SEND_V4

# 订阅主题
mqtt.topics=$SYS/broker/log/#,CID_BT_SERVER_V4
```

### 3.2 Spring配置 (spring-mqtt.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:int-mqtt="http://www.springframework.org/schema/integration/mqtt">
    
    <!-- MQTT客户端工厂 -->
    <bean id="clientFactory" 
          class="org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory">
        <property name="serverURIs">
            <array>
                <value>${mqtt.url}</value>
            </array>
        </property>
    </bean>

    <!-- 消息接收适配器 -->
    <int-mqtt:message-driven-channel-adapter 
        id="mqttInbound"
        client-id="${mqtt.clientId}"
        url="${mqtt.url}"
        topics="${mqtt.topics}"
        qos="${mqtt.qos}"
        client-factory="clientFactory"
        auto-startup="${mqtt.autoStartup}"
        send-timeout="${mqtt.sendTimeout}"
        channel="receiveChannel"/>
    
    <int:channel id="receiveChannel"/>
    
    <!-- 接收消息处理 -->
    <int:service-activator 
        id="receiveService"
        input-channel="receiveChannel"
        ref="mqttReceiveService"
        method="handleMessage"/>
    
    <!-- Bean定义 -->
    <bean id="mqttReceiveService" 
          class="com.proshine.mqtt.service.MqttReceiveService"/>
    
    <bean id="mqttSendService" 
          class="com.proshine.mqtt.service.MqttSendService">
        <constructor-arg name="clientId" value="${mqtt.sendClientId}"/>
        <constructor-arg name="clientFactory" ref="clientFactory"/>
    </bean>
</beans>
```

## 4. 核心代码实现

### 4.1 MQTT命令实体 (MqttCmd.java)
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqttCmd {
    private String id;           // 消息ID
    private String service;      // 服务类型
    private String operation;    // 操作类型
    private Map params;          // 参数列表
    
    public static MqttCmd create(String service, String operation) {
        return new MqttCmd(Identities.uuid2(), service, operation, null);
    }
    
    public static MqttCmd create(String service, String operation, Map params) {
        return new MqttCmd(Identities.uuid2(), service, operation, params);
    }
}
```

### 4.2 服务枚举定义 (EnumService.java)
```java
public class EnumService {
    // 会议预约列表服务
    public static final String SERVICE_MEETING_INFO = "EnumMeetingInfos";
    
    // 会议预约相关操作
    public static class EnumMeetingInfos {
        // 获取会议信息列表
        public static final String GET_MEETING_INFO_LIST = "GET_MEETING_INFO_LIST";
    }
}
```

### 4.3 统一ID定义 (EnumUniformID.java)
```java
public class EnumUniformID {
    // 终端主题前缀
    public static final String TOPIC_PREFIX = "bt_client/";
    
    // 广播主题
    public static final String BROADCAST_TOPIC = "broadcast";
    
    // 服务器客户ID
    public static final String SERVER_CLIENT_ID = "CID_BT_SERVER";
}
```

### 4.4 终端控制服务实现
```java
@Service
public class TerminalControlServiceImpl implements TerminalControlService {
    
    @Autowired
    private MqttSendService mqttSendService;
    
    /**
     * 推送会议信息列表到指定终端
     * @param terminalIds 终端ID列表，多个用逗号分隔
     */
    @Override
    public void getMeetingInfoList(String terminalIds) {
        String[] arr = terminalIds.split(",");
        for (String terminalId : arr) {
            // 创建MQTT命令
            MqttCmd cmd = MqttCmd.create(
                EnumService.SERVICE_MEETING_INFO, 
                EnumService.EnumMeetingInfos.GET_MEETING_INFO_LIST
            );
            
            // 发送单播消息
            unicastCmd(cmd, terminalId, EnumCmdQuality.QOS_1);
        }
    }
    
    /**
     * 单播发送MQTT命令
     * @param cmd MQTT命令对象
     * @param terminalId 目标终端ID
     * @param qos 服务质量等级
     */
    public int unicastCmd(MqttCmd cmd, String terminalId, int qos) {
        // 构建消息
        Message<String> msg = MessageBuilder.withPayload(JSON.toJSONString(cmd))
            .setHeader(MqttHeaders.QOS, qos)
            .setHeader(MqttHeaders.TOPIC, EnumUniformID.TOPIC_PREFIX + terminalId)
            .build();
            
        // 发送消息
        mqttSendService.handleMessage(msg);
        return 0;
    }
}
```

## 5. 消息格式

### 5.1 发送消息格式
```json
{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "service": "EnumMeetingInfos",
    "operation": "GET_MEETING_INFO_LIST",
    "params": null
}
```

### 5.2 MQTT主题格式
- **单播主题**: `bt_client/{terminalId}`
- **广播主题**: `broadcast`

### 5.3 QoS级别说明
- **QoS 0**: 最多一次传递（不保证送达）
- **QoS 1**: 至少一次传递（确保送达，可能重复）
- **QoS 2**: 恰好一次传递（确保送达且不重复）

## 6. 业务流程

### 6.1 会议信息推送流程
```
1. 业务层调用 getMeetingInfoList(terminalIds)
2. 解析终端ID列表
3. 遍历每个终端ID：
   a. 创建MQTT命令对象
   b. 设置服务类型为 SERVICE_MEETING_INFO
   c. 设置操作类型为 GET_MEETING_INFO_LIST
   d. 通过unicastCmd发送到指定终端
4. 终端接收到消息后执行相应操作
```

### 6.2 终端处理流程
```
1. 终端订阅主题: bt_client/{自身ID}
2. 接收到会议信息列表获取命令
3. 向服务器请求会议数据
4. 更新本地显示
```

## 7. 集成步骤

### 7.1 添加Maven依赖
在pom.xml中添加MQTT相关依赖

### 7.2 配置MQTT连接
1. 创建mqtt-config.properties配置文件
2. 配置MQTT服务器地址、客户端ID等参数

### 7.3 配置Spring MQTT
1. 创建spring-mqtt.xml配置文件
2. 配置客户端工厂、消息适配器等

### 7.4 实现业务代码
1. 创建MQTT命令实体类
2. 定义服务枚举和操作枚举
3. 实现消息发送服务
4. 实现业务控制接口

### 7.5 测试验证
1. 启动MQTT服务器
2. 启动应用程序
3. 调用getMeetingInfoList接口
4. 验证终端是否收到消息

## 8. 注意事项

### 8.1 连接管理
- 确保MQTT服务器正常运行
- 配置合适的超时时间
- 实现断线重连机制

### 8.2 消息可靠性
- 根据业务需求选择合适的QoS级别
- 实现消息确认机制
- 处理消息重复问题

### 8.3 性能优化
- 批量发送时考虑使用组播
- 控制消息发送频率
- 优化消息体大小

### 8.4 安全考虑
- 使用SSL/TLS加密传输
- 实现客户端认证
- 限制订阅权限

## 9. 扩展功能

### 9.1 消息持久化
可以将重要消息持久化到数据库，确保消息不丢失

### 9.2 消息追踪
实现消息ID追踪机制，方便问题排查

### 9.3 批量操作
支持批量推送到多个终端，提高效率

## 10. 故障排查

### 10.1 常见问题
1. **连接失败**: 检查MQTT服务器地址和端口
2. **消息未送达**: 检查主题格式和QoS设置
3. **消息重复**: 检查消息ID去重机制

### 10.2 日志配置
```properties
# log4j.properties
log4j.logger.com.proshine.mqtt=DEBUG
log4j.logger.org.springframework.integration.mqtt=DEBUG
```

### 10.3 调试技巧
1. 使用MQTT客户端工具订阅相关主题
2. 查看MQTT服务器日志
3. 开启Spring Integration调试日志

## 11. 参考资料

- [Spring Integration MQTT文档](https://docs.spring.io/spring-integration/reference/html/mqtt.html)
- [Eclipse Paho MQTT客户端文档](https://www.eclipse.org/paho/index.php?page=clients/java/index.php)
- [MQTT协议规范](http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html)