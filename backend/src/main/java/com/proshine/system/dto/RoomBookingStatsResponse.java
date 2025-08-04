package com.proshine.system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 教室预约统计响应数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingStatsResponse {
    
    /**
     * 教室ID
     */
    private String roomId;
    
    /**
     * 教室名称
     */
    private String roomName;
    
    /**
     * 预约次数
     */
    private Integer bookingCount;
    
    /**
     * 预约累计时长（分钟）
     */
    private Integer totalDuration;
    
    /**
     * 累计预约人数
     */
    private Integer totalPeople;
}