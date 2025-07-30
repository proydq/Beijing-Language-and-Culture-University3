package com.proshine.system.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 更新教室VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ClassroomUpdateVo {

    /**
     * 教室ID（必填）
     */
    @NotBlank(message = "教室ID不能为空")
    private String id;

    /**
     * 教室名称
     */
    private String classroomName;

    /**
     * 房间号
     */
    private String roomNumber;

    /**
     * 所属楼名称
     */
    private String roomAreaName;

    /**
     * 区域ID
     */
    private String roomAreaId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 容纳人数
     */
    private Integer roomVolume;

    /**
     * 房间面积
     */
    private String roomArea;

    /**
     * 是否允许预约人自选审批人
     * yes: 允许
     * no: 不允许
     */
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