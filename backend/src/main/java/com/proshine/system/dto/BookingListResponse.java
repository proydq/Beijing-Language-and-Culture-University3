package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约列表响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BookingListResponse {
    
    /**
     * 预约ID
     */
    private String id;
    
    /**
     * 预约名称
     */
    private String reservationName;
    
    /**
     * 预约周期
     */
    private String reservationPeriod;
    
    /**
     * 房间名称
     */
    private String roomName;
    
    /**
     * 申请人姓名
     */
    private String applicantName;
    
    /**
     * 审核状态（中文显示）
     */
    private String approvalStatus;
    
    /**
     * 使用状态（中文显示）
     */
    private String usageStatus;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 预约描述
     */
    private String description;
    
    /**
     * 借用时间（与reservationPeriod相同）
     */
    private String borrowTime;
    
    /**
     * 参与人员（逗号分隔）
     */
    private String participants;
    
    /**
     * 审批人员（逗号分隔）
     */
    private String approvers;
}