package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批列表响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ApprovalListResponse {
    
    /**
     * 预约ID
     */
    private String id;
    
    /**
     * 预约名称
     */
    private String bookingName;
    
    /**
     * 申请人姓名
     */
    private String applicantName;
    
    /**
     * 申请人类型
     */
    private String applicantType;
    
    /**
     * 房间名称
     */
    private String roomName;
    
    /**
     * 预约时段描述
     */
    private String bookingPeriod;
    
    /**
     * 预约开始时间
     */
    private LocalDateTime bookingStartTime;
    
    /**
     * 预约结束时间
     */
    private LocalDateTime bookingEndTime;
    
    /**
     * 预约描述
     */
    private String description;
    
    /**
     * 申请理由
     */
    private String reason;
    
    /**
     * 审批状态
     */
    private String approvalStatus;
    
    /**
     * 紧急程度
     */
    private String urgency;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 参与人列表
     */
    private String participants;
    
    /**
     * 审批人列表
     */
    private String approvers;
    
    /**
     * 申请时间（用于前端显示）
     */
    private LocalDateTime applyTime;
    
    /**
     * 预约时间（用于前端显示）
     */
    private String bookingTime;
    
    /**
     * 预约人（用于前端显示）
     */
    private String applicant;
    
    /**
     * 状态（用于前端显示）
     */
    private String status;
    
    /**
     * 名称（用于前端显示）
     */
    private String name;
    
    /**
     * 周期（用于前端显示）
     */
    private String cycle;
}