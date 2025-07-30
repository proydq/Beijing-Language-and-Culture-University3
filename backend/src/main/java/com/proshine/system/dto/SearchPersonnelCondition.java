package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 人员查询条件
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class SearchPersonnelCondition {

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
     * 姓名（模糊查询）
     */
    private String name;

    /**
     * 部门（模糊查询）
     */
    private String department;

    /**
     * 职位（模糊查询）
     */
    private String position;
}