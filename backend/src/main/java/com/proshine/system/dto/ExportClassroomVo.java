package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 导出教室VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ExportClassroomVo {

    /**
     * 教室ID列表（必填）
     */
    @NotEmpty(message = "教室ID列表不能为空")
    private List<String> roomIds;
}