package com.proshine.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 远程开门记录查询请求DTO
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteAccessRecordsRequest {

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
     * 开门方式（管理员授权/应急开门/系统开门，可选）
     */
    private String openMethod;

    /**
     * 操作员类型（管理员/系统/应急，可选）
     */
    private String operatorType;
}