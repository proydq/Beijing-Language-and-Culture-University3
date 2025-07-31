package com.proshine.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 批量审批响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BatchApprovalResponse {
    
    /**
     * 成功数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failureCount;
    
    /**
     * 详细结果
     */
    private List<BatchApprovalDetail> details;
    
    /**
     * 批量审批详细结果
     */
    @Data
    public static class BatchApprovalDetail {
        
        /**
         * 预约ID
         */
        private String bookingId;
        
        /**
         * 状态（success/failure）
         */
        private String status;
        
        /**
         * 消息
         */
        private String message;
    }
}