package com.proshine.system.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量审批设置VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BatchApprovalVo {

    /**
     * 教室ID列表（必填）
     */
    @NotEmpty(message = "教室ID列表不能为空")
    private List<String> roomIds;

    /**
     * 是否允许预约人自选审批人（必填）
     * yes: 允许
     * no: 不允许
     */
    @NotNull(message = "是否允许预约人自选审批人不能为空")
    private String allowBookerSelectApprover;

    /**
     * 审批级别配置
     */
    @Valid
    private List<ApprovalLevelVo> approvalLevels;

    /**
     * 审批级别VO
     */
    @Data
    public static class ApprovalLevelVo {
        
        /**
         * 审批级别（1,2,3）
         */
        private Integer level;
        
        /**
         * 审批人列表（逗号分隔的姓名）
         */
        private String approvers;
        
        /**
         * 审批人ID列表（逗号分隔的ID）
         */
        private String approverIds;
    }
}