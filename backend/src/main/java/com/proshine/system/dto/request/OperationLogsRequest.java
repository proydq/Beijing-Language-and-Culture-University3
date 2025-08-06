package com.proshine.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志查询请求DTO
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogsRequest {

    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10
     */
    private Integer pageSize = 10;

    /**
     * 开始时间，可选
     */
    private String startTime;

    /**
     * 结束时间，可选
     */
    private String endTime;

    /**
     * 操作员ID，可选
     */
    private String operatorId;

    /**
     * 教室ID，可选
     */
    private String roomId;

    /**
     * 操作类型（开门/关门），可选
     */
    private String operationType;
}