package com.proshine.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 批量审批请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BatchApprovalRequest {
    
    /**
     * 预约ID列表
     */
    private List<String> bookingIds;
    
    /**
     * 审批动作（APPROVED-通过，REJECTED-拒绝）
     */
    private String action;
    
    /**
     * 批量审批意见
     */
    private String comment;
}