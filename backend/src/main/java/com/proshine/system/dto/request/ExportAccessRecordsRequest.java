package com.proshine.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教室借用记录导出请求DTO
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportAccessRecordsRequest {

    /**
     * 导出类型：current-当前页，all-全部页
     */
    private String exportType = "current";

    /**
     * 页码（exportType为current时必填）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数（exportType为current时必填）
     */
    private Integer pageSize = 10;

    /**
     * 区域ID（可选）
     */
    private String areaId;

    /**
     * 教室ID（可选）
     */
    private String roomId;

    /**
     * 基础信息（可选）
     */
    private String basicInfo;

    /**
     * 开始时间（可选）
     */
    private String startTime;

    /**
     * 结束时间（可选）
     */
    private String endTime;

    /**
     * 开门方式（可选）
     */
    private String openMethod;

    /**
     * 通行类型（可选）
     */
    private String accessType;
}