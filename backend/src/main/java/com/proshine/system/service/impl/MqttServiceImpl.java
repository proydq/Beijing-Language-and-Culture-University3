package com.proshine.system.service.impl;


import com.alibaba.fastjson2.JSON;
import com.proshine.system.config.MqttConfig;
import com.proshine.system.entity.Terminal;
import com.proshine.system.mqtt.MqttCmd;
import com.proshine.system.mqtt.MqttServiceEnum;
import com.proshine.system.service.MqttService;
import com.proshine.system.service.TerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MQTT服务实现
 */
@Slf4j
@Service
public class MqttServiceImpl implements MqttService {
    
    @Autowired
    private MessageChannel mqttOutboundChannel;
    
    @Autowired
    private MqttConfig mqttConfig;
    
    @Autowired
    private TerminalService terminalService;
    
    @Override
    public void sendToTerminal(String terminalId, MqttCmd cmd) {
        if (!mqttConfig.isEnabled()) {
            log.debug("MQTT未启用，跳过发送消息");
            return;
        }
        
        try {
            String topic = MqttServiceEnum.TOPIC_PREFIX + terminalId;
            String payload = JSON.toJSONString(cmd);
            
            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .setHeader(MqttHeaders.QOS, mqttConfig.getDefaultQos())
                    .build();
            
            mqttOutboundChannel.send(message);
            
            log.info("发送MQTT消息到终端: terminalId={}, topic={}, service={}, operation={}", 
                    terminalId, topic, cmd.getService(), cmd.getOperation());
                    
        } catch (Exception e) {
            log.error("发送MQTT消息失败: terminalId={}, error={}", terminalId, e.getMessage(), e);
        }
    }
    
    @Override
    public void sendToTerminals(List<String> terminalIds, MqttCmd cmd) {
        if (terminalIds == null || terminalIds.isEmpty()) {
            return;
        }
        
        for (String terminalId : terminalIds) {
            sendToTerminal(terminalId, cmd);
        }
    }

    

    
    @Override
    public void notifyTerminalsByRoomId(String roomId, String cstmId) {
        try {
            // 查询绑定了该房间的所有终端
            List<Terminal> terminals = terminalService.findByCstmIdAndExtraId2(cstmId, roomId);
            
            // 过滤出绑定了指定房间的终端
            List<String> terminalIds = terminals.stream()
                    .filter(terminal -> roomId.equals(terminal.getExtraId2()))
                    .map(Terminal::getTerminalId)
                    .filter(id -> id != null && !id.isEmpty())
                    .collect(Collectors.toList());
            
            if (!terminalIds.isEmpty()) {
                log.info("通知房间{}的{}个终端刷新", roomId, terminalIds.size());
                
                // 创建带房间信息的命令
                Map<String, Object> params = new HashMap<>();
                params.put("roomId", roomId);
                
                MqttCmd cmd = MqttCmd.create(
                        MqttServiceEnum.SERVICE_PROGRAM,
                        MqttServiceEnum.EnumProgramOps.UPDATE_COURSE_PLANNING,
                        params
                );
                
                sendToTerminals(terminalIds, cmd);
            } else {
                log.debug("房间{}没有绑定的终端", roomId);
            }
            
        } catch (Exception e) {
            log.error("根据房间ID通知终端失败: roomId={}, cstmId={}", roomId, cstmId, e);
        }
    }
}