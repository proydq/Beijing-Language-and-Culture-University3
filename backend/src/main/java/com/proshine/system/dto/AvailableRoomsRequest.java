package com.proshine.system.dto;

import lombok.Data;

/**
 * 可预约房间列表查询请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class AvailableRoomsRequest {
    
    /**
     * 房间区域ID
     */
    private String roomAreaId;
    
    /**
     * 房间名称关键词
     */
    private String roomName;
    
    /**
     * 是否只显示可用房间
     */
    private Boolean available = true;
}