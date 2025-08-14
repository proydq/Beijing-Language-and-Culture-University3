package com.proshine.system.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * MQTT命令实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqttCmd {
    
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 服务类型
     */
    private String service;
    
    /**
     * 操作类型
     */
    private String operation;
    
    /**
     * 参数列表
     */
    private Map<String, Object> params;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 创建MQTT命令
     */
    public static MqttCmd create(String service, String operation) {
        return new MqttCmd(
                UUID.randomUUID().toString(),
                service,
                operation,
                null,
                System.currentTimeMillis()
        );
    }
    
    /**
     * 创建带参数的MQTT命令
     */
    public static MqttCmd create(String service, String operation, Map<String, Object> params) {
        return new MqttCmd(
                UUID.randomUUID().toString(),
                service,
                operation,
                params,
                System.currentTimeMillis()
        );
    }
}