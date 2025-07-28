package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 房屋借用预约实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_room_booking")
public class RoomBooking {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "booking_name", columnDefinition = "VARCHAR(200) COMMENT '预约名称'")
    private String bookingName;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '房间ID'")
    private String roomId;

    @Column(name = "room_name", columnDefinition = "VARCHAR(100) COMMENT '房间名称（冗余）'")
    private String roomName;

    @Column(name = "applicant_id", columnDefinition = "VARCHAR(36) COMMENT '申请人ID'")
    private String applicantId;

    @Column(name = "applicant_name", columnDefinition = "VARCHAR(50) COMMENT '申请人姓名（冗余）'")
    private String applicantName;

    @Column(name = "applicant_type", columnDefinition = "VARCHAR(20) COMMENT '申请人类型（教师/学生/管理员）'")
    private String applicantType;

    @Column(name = "applicant_phone", columnDefinition = "VARCHAR(20) COMMENT '申请人手机号'")
    private String applicantPhone;

    @Column(name = "applicant_department", columnDefinition = "VARCHAR(100) COMMENT '申请人部门'")
    private String applicantDepartment;

    @Column(name = "booking_start_time", columnDefinition = "DATETIME COMMENT '预约开始时间'")
    private LocalDateTime bookingStartTime;

    @Column(name = "booking_end_time", columnDefinition = "DATETIME COMMENT '预约结束时间'")
    private LocalDateTime bookingEndTime;

    @Column(name = "booking_period", columnDefinition = "VARCHAR(200) COMMENT '预约时段描述'")
    private String bookingPeriod;

    @Column(name = "description", columnDefinition = "TEXT COMMENT '预约用途描述'")
    private String description;

    @Column(name = "reason", columnDefinition = "TEXT COMMENT '申请理由'")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", columnDefinition = "VARCHAR(20) COMMENT '审批状态'")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_status", columnDefinition = "VARCHAR(20) COMMENT '使用状态'")
    private UsageStatus usageStatus = UsageStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", columnDefinition = "VARCHAR(20) COMMENT '紧急程度'")
    private Urgency urgency = Urgency.NORMAL;

    @Column(name = "approval_time", columnDefinition = "DATETIME COMMENT '审批时间'")
    private LocalDateTime approvalTime;

    @Column(name = "approver_id", columnDefinition = "VARCHAR(36) COMMENT '审批人ID'")
    private String approverId;

    @Column(name = "approver_name", columnDefinition = "VARCHAR(50) COMMENT '审批人姓名'")
    private String approverName;

    @Column(name = "approval_comment", columnDefinition = "TEXT COMMENT '审批意见'")
    private String approvalComment;

    @Column(name = "reject_reason", columnDefinition = "TEXT COMMENT '拒绝原因'")
    private String rejectReason;

    @Column(name = "cancel_time", columnDefinition = "DATETIME COMMENT '取消时间'")
    private LocalDateTime cancelTime;

    @Column(name = "cancel_reason", columnDefinition = "TEXT COMMENT '取消原因'")
    private String cancelReason;

    @Column(name = "actual_start_time", columnDefinition = "DATETIME COMMENT '实际开始使用时间'")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time", columnDefinition = "DATETIME COMMENT '实际结束使用时间'")
    private LocalDateTime actualEndTime;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "last_update_time", columnDefinition = "DATETIME COMMENT '最后更新时间'")
    private LocalDateTime lastUpdateTime;

    @Column(name = "extend1", columnDefinition = "VARCHAR(255) COMMENT '扩展字段1'")
    private String extend1;

    @Column(name = "extend2", columnDefinition = "VARCHAR(255) COMMENT '扩展字段2'")
    private String extend2;

    /**
     * 审批状态枚举
     */
    public enum ApprovalStatus {
        PENDING("审核中"),
        APPROVED("通过"),
        REJECTED("拒绝"),
        CANCELLED("已取消");

        private final String displayName;

        ApprovalStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 使用状态枚举
     */
    public enum UsageStatus {
        NOT_STARTED("未开始"),
        IN_PROGRESS("进行中"),
        COMPLETED("已结束"),
        CANCELLED("已取消");

        private final String displayName;

        UsageStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 紧急程度枚举
     */
    public enum Urgency {
        NORMAL("普通"),
        URGENT("紧急"),
        VERY_URGENT("非常紧急");

        private final String displayName;

        Urgency(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        lastUpdateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = LocalDateTime.now();
    }
}