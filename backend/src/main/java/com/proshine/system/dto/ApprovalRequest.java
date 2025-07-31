package com.proshine.system.dto;

import lombok.Data;

/**
 * 审批操作请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ApprovalRequest {
    
    /**
     * 审批动作（APPROVED-通过，REJECTED-拒绝）
     */
    private String action;
    
    /**
     * 审批意见
     */
    private String comment;
    
    /**
     * 审批人ID（可选，后端可从token获取）
     */
    private String approverId;
    
    /**
     * 审批人姓名（可选，后端可从token获取）
     */
    private String approverName;
}