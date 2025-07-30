package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 违规设置更新请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ViolationSettingUpdateRequest {

    /**
     * 超时分钟数
     */
    @NotNull(message = "超时分钟数不能为空")
    @Min(value = 1, message = "超时分钟数必须大于0")
    private Integer startTime;

    /**
     * 违规次数阈值
     */
    @NotNull(message = "违规次数阈值不能为空")
    @Min(value = 1, message = "违规次数阈值必须大于0")
    private Integer violationCount;
}