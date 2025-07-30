package com.proshine.system.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 添加教室VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ClassroomAddVo {

    /**
     * 教室名称（必填）
     */
    @NotBlank(message = "教室名称不能为空")
    private String classroomName;

    /**
     * 房间号（必填）
     */
    @NotBlank(message = "房间号不能为空")
    private String roomNumber;

    /**
     * 教室编号（对应Room表的id，新增后自动生成）
     */
    private String classroomCode;

    /**
     * 所属楼名称
     */
    private String roomAreaName;

    /**
     * 区域ID（必填）
     */
    @NotBlank(message = "区域ID不能为空")
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
    @NotBlank(message = "是否允许预约人自选审批人不能为空")
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
        @NotNull(message = "审批级别不能为空")
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