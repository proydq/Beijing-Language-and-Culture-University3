package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审批历史响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ApprovalHistoryResponse {
    
    /**
     * 审批记录ID
     */
    private String id;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批人类型
     */
    private String approverType;
    
    /**
     * 审批动作
     */
    private String approvalAction;
    
    /**
     * 审批意见
     */
    private String approvalComment;
    
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 审批级别
     */
    private Integer approvalLevel;
    
    /**
     * 是否为最终审批
     */
    private Boolean isFinalApproval;
    
    /**
     * 级别名称（前端显示用）
     */
    private String levelName;
    
    /**
     * 审批人列表（前端显示用）
     */
    private String[] approvers;
    
    /**
     * 确认审批人（前端显示用）
     */
    private String confirmedApprover;
}