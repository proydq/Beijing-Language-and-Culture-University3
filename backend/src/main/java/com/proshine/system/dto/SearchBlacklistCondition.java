package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 黑名单查询条件
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class SearchBlacklistCondition {

    /**
     * 页码（必填）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNumber;

    /**
     * 每页大小（必填）
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 工号
     */
    private String employeeId;

    /**
     * 部门
     */
    private String department;
}