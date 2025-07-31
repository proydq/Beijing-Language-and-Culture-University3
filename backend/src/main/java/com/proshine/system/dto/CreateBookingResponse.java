package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 创建房间预约响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class CreateBookingResponse {
    
    /**
     * 生成的预约ID
     */
    private String bookingId;
    
    /**
     * 预约名称
     */
    private String bookingName;
    
    /**
     * 初始状态（审核中）
     */
    private String approvalStatus;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}