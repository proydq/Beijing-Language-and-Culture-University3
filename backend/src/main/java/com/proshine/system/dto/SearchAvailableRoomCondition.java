package com.proshine.system.dto;

import lombok.Data;

/**
 * 可选房间查询条件
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class SearchAvailableRoomCondition {

    private String customerId;
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String roomName;
    
    private String roomAreaId;
}