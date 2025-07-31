package com.proshine.system.dto;

import lombok.Data;

/**
 * 我的预约列表查询请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class MyBookingsRequest {
    
    /**
     * 页码
     */
    private Integer pageNumber = 1;
    
    /**
     * 页大小
     */
    private Integer pageSize = 20;
    
    /**
     * 预约名称（模糊搜索）
     */
    private String reservationName;
    
    /**
     * 审核状态：审核中|通过|拒绝
     */
    private String approvalStatus;
    
    /**
     * 使用状态：未开始|进行中|已结束
     */
    private String usageStatus;
    
    /**
     * 开始日期（格式：YYYY-MM-DD）
     */
    private String startDate;
    
    /**
     * 结束日期（格式：YYYY-MM-DD）
     */
    private String endDate;
}