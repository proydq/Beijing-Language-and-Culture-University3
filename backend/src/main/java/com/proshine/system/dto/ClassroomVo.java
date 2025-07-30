package com.proshine.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 教室信息VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ClassroomVo {

    /**
     * 教室ID
     */
    private String id;

    /**
     * 教室名称
     */
    private String roomName;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 所属楼
     */
    private String roomAreaName;

    /**
     * 楼栋ID
     */
    private String roomAreaId;

    /**
     * 是否可调换
     * yes: 可调换
     * no: 不可调换
     */
    private String isExchange;

    /**
     * 是否需要审批
     * yes: 需要审批
     * no: 不需要审批
     */
    private String needApproval;

    /**
     * 第一级审批人
     */
    private String firstApprover;

    /**
     * 第二级审批人
     */
    private String secondApprover;

    /**
     * 第三级审批人
     */
    private String thirdApprover;

    /**
     * 教室编号
     */
    private String roomCode;

    /**
     * 容纳人数
     */
    private Integer roomVolume;

    /**
     * 备注
     */
    private String remark;

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
     * 最后更新时间
     */
    private String lastUpdateTime;

    /**
     * 审批级别详情列表（详情接口专用）
     */
    private List<ApprovalLevelDetail> approvalLevels;

    /**
     * 审批级别详情
     */
    @Data
    public static class ApprovalLevelDetail {
        /**
         * 审批级别
         */
        private Integer level;

        /**
         * 审批人姓名列表（逗号分隔）
         */
        private String approvers;

        /**
         * 审批人ID列表（逗号分隔）
         */
        private String approverIds;
    }
}