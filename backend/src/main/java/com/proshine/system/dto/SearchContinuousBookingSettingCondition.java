package com.proshine.system.dto;

import lombok.Data;

/**
 * 连续预约设置查询条件
 */
@Data
public class SearchContinuousBookingSettingCondition {
    
    /**
     * 页码（从1开始）
     */
    private Integer pageNumber = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 区域ID（支持递归查询子区域）
     */
    private String areaId;
    
    /**
     * 房间名称（模糊查询）
     */
    private String roomName;
    
    /**
     * 房间号码（模糊查询）
     */
    private String roomNo;
    
    /**
     * 房屋类型名称
     */
    private String roomTypeName;
    
    /**
     * 区域名称（模糊查询）
     */
    private String roomAreaName;
    
    /**
     * 房间编码（模糊查询）
     */
    private String roomCode;
}