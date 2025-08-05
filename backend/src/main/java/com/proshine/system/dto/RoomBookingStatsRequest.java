package com.proshine.system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 教室预约统计列表请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingStatsRequest {
    
    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;
    
    /**
     * 每页条数，默认10
     */
    private Integer pageSize = 10;
    
    /**
     * 教室名称（可选，支持模糊搜索）
     */
    private String roomName;
    
    /**
     * 区域ID（可选，用于楼栋筛选）
     */
    private String areaId;
    
    /**
     * 排序字段（可选）
     */
    private String sortBy;
    
    /**
     * 排序方向（可选：asc-升序，desc-降序）
     */
    private String sortOrder;
    
    /**
     * 导出类型（可选：current-当前页，all-全部数据）
     */
    private String exportType;
}