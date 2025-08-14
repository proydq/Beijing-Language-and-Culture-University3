package com.proshine.system.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * MQTT配置类
 */
@Slf4j
@Data
@Configuration
public class MqttConfig {
    
    @Value("${mqtt.url:tcp://localhost:1883}")
    private String mqttUrl;
    
    @Value("${mqtt.username:}")
    private String username;
    
    @Value("${mqtt.password:}")
    private String password;
    
    @Value("${mqtt.client-id:dingtalk-meeting-server}")
    private String clientId;
    
    @Value("${mqtt.default-topic:bt_client/}")
    private String defaultTopic;
    
    @Value("${mqtt.completion-timeout:30000}")
    private int completionTimeout;
    
    @Value("${mqtt.keep-alive-interval:60}")
    private int keepAliveInterval;
    
    @Value("${mqtt.qos:1}")
    private int defaultQos;
    
    @Value("${mqtt.enabled:true}")
    private boolean enabled;
    
    /**
     * MQTT客户端工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        options.setServerURIs(new String[]{mqttUrl});
        options.setKeepAliveInterval(keepAliveInterval);
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        
        if (username != null && !username.isEmpty()) {
            options.setUserName(username);
        }
        if (password != null && !password.isEmpty()) {
            options.setPassword(password.toCharArray());
        }
        
        factory.setConnectionOptions(options);
        
        log.info("MQTT客户端工厂配置完成: url={}, clientId={}", mqttUrl, clientId);
        
        return factory;
    }
    
    /**
     * MQTT发送通道
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
    
    /**
     * MQTT消息处理器
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId + "_sender", 
                mqttClientFactory()
        );
        
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        messageHandler.setDefaultQos(defaultQos);
        messageHandler.setCompletionTimeout(completionTimeout);
        
        log.info("MQTT消息处理器配置完成: defaultTopic={}, defaultQos={}", defaultTopic, defaultQos);
        
        return messageHandler;
    }
}