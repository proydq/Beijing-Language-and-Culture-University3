package com.proshine.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教室借用记录查询请求DTO
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRecordsRequest {

    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10
     */
    private Integer pageSize = 10;

    /**
     * 区域ID（可选，用于楼栋/楼层筛选）
     */
    private String areaId;

    /**
     * 教室ID（可选）
     */
    private String roomId;

    /**
     * 基础信息（姓名/工号/联系方式，可选）
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
     * 开门方式（刷卡/人脸识别/按钮，可选）
     */
    private String openMethod;

    /**
     * 通行类型（预约权限/永久权限，可选）
     */
    private String accessType;
}