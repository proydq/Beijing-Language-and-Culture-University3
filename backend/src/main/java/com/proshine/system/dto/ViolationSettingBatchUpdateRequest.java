package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 违规设置批量更新请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ViolationSettingBatchUpdateRequest {

    /**
     * 教室ID列表
     */
    @NotEmpty(message = "教室ID列表不能为空")
    private List<String> roomIds;

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