package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预约审批记录实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_booking_approval")
public class BookingApproval {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "booking_id", columnDefinition = "VARCHAR(36) COMMENT '预约ID'")
    private String bookingId;

    @Column(name = "approver_id", columnDefinition = "VARCHAR(36) COMMENT '审批人ID'")
    private String approverId;

    @Column(name = "approver_name", columnDefinition = "VARCHAR(50) COMMENT '审批人姓名'")
    private String approverName;

    @Column(name = "approver_type", columnDefinition = "VARCHAR(20) COMMENT '审批人类型（管理员/部门负责人）'")
    private String approverType;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_action", columnDefinition = "VARCHAR(20) COMMENT '审批动作'")
    private ApprovalAction approvalAction;

    @Column(name = "approval_comment", columnDefinition = "TEXT COMMENT '审批意见'")
    private String approvalComment;

    @Column(name = "approval_time", columnDefinition = "DATETIME COMMENT '审批时间'")
    private LocalDateTime approvalTime;

    @Column(name = "approval_level", columnDefinition = "INT COMMENT '审批级别（1-一级审批，2-二级审批）'")
    private Integer approvalLevel;

    @Column(name = "is_final_approval", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否为最终审批'")
    private Boolean isFinalApproval = false;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "extend1", columnDefinition = "VARCHAR(255) COMMENT '扩展字段1'")
    private String extend1;

    @Column(name = "extend2", columnDefinition = "VARCHAR(255) COMMENT '扩展字段2'")
    private String extend2;

    /**
     * 审批动作枚举
     */
    public enum ApprovalAction {
        APPROVE("通过"),
        REJECT("拒绝"),
        RETURN("退回"),
        TRANSFER("转交");

        private final String displayName;

        ApprovalAction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        approvalTime = LocalDateTime.now();
    }
}