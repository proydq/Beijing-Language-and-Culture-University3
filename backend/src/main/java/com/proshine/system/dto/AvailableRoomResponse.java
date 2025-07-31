package com.proshine.system.dto;

import lombok.Data;

/**
 * 可预约房间响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class AvailableRoomResponse {
    
    /**
     * 房间ID
     */
    private String id;
    
    /**
     * 房间名称
     */
    private String name;
    
    /**
     * 容纳人数
     */
    private Integer capacity;
    
    /**
     * 楼宇名称
     */
    private String building;
    
    /**
     * 楼层
     */
    private String floor;
    
    /**
     * 是否可用
     */
    private Boolean available;
    
    /**
     * 所属区域ID
     */
    private String roomAreaId;
    
    /**
     * 房间类型
     */
    private String roomType;
    
    /**
     * 设备信息
     */
    private String equipment;
    
    /**
     * 房间描述
     */
    private String description;
}