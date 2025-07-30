package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量删除教室VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BatchDeleteClassroomVo {

    /**
     * 教室ID列表（必填）
     */
    @NotEmpty(message = "教室ID列表不能为空")
    private List<String> ids;
}