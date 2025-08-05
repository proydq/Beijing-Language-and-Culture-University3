package com.proshine.system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 教室预约详情请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingDetailsRequest {
    
    /**
     * 教室ID
     */
    private String roomId;
    
    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数，默认10
     */
    private Integer pageSize = 10;
    
    /**
     * 借用/预约名称（可选，支持模糊搜索）
     */
    private String bookingName;
    
    /**
     * 审核状态（通过/审核中/拒绝，可选）
     */
    private String auditStatus;
    
    /**
     * 使用状态（未开始/进行中/已结束，可选）
     */
    private String usageStatus;
    
    /**
     * 预约人（可选）
     */
    private String applicantName;
    
    /**
     * 预约开始日期（可选）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    
    /**
     * 预约结束日期（可选）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;
    
    /**
     * 导出类型（可选：current-当前页，all-全部数据）
     */
    private String exportType;
}