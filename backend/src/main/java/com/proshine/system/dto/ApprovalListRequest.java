package com.proshine.system.dto;

import lombok.Data;

/**
 * 审批列表查询请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ApprovalListRequest {
    
    /**
     * 页码，从1开始
     */
    private Integer pageNumber = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 预约名称（模糊搜索）
     */
    private String reservationName;
    
    /**
     * 申请人姓名（模糊搜索）
     */
    private String applicantName;
    
    /**
     * 审批状态（PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝）
     */
    private String approvalStatus;
    
    /**
     * 开始日期（格式：yyyy-MM-dd）
     */
    private String startDate;
    
    /**
     * 结束日期（格式：yyyy-MM-dd）
     */
    private String endDate;
}