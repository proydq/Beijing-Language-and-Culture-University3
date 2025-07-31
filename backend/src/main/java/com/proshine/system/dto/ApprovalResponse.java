package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批操作响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ApprovalResponse {
    
    /**
     * 预约ID
     */
    private String bookingId;
    
    /**
     * 审批状态
     */
    private String approvalStatus;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批意见
     */
    private String approvalComment;
}