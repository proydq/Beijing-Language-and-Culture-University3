package com.proshine.system.service;


import com.proshine.system.mqtt.MqttCmd;

import java.util.List;

/**
 * MQTT服务接口
 */
public interface MqttService {
    
    /**
     * 发送MQTT消息到指定终端
     * @param terminalId 终端ID
     * @param cmd MQTT命令
     */
    void sendToTerminal(String terminalId, MqttCmd cmd);
    
    /**
     * 发送MQTT消息到多个终端
     * @param terminalIds 终端ID列表
     * @param cmd MQTT命令
     */
    void sendToTerminals(List<String> terminalIds, MqttCmd cmd);

    
    /**
     * 根据房间ID通知相关终端刷新
     * @param roomId 房间ID
     * @param cstmId 客户域ID
     */
    void notifyTerminalsByRoomId(String roomId, String cstmId);
}