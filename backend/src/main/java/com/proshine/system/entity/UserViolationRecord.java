package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 用户违规记录实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "tb_user_violation_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserViolationRecord {

    @Id
    @GeneratedValue(generator = "user-violation-record-uuid")
    @GenericGenerator(name = "user-violation-record-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36) COMMENT '违规用户ID'")
    private String userId;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '违规教室ID'")
    private String roomId;

    @Column(name = "booking_id", columnDefinition = "VARCHAR(36) COMMENT '相关预约ID'")
    private String bookingId;

    @Column(name = "violation_type", columnDefinition = "VARCHAR(50) DEFAULT 'OVERTIME_NO_CHECKIN' COMMENT '违规类型'")
    private String violationType = "OVERTIME_NO_CHECKIN";

    @Column(name = "overtime_minutes", columnDefinition = "INT COMMENT '超时分钟数'")
    private Integer overtimeMinutes;

    @Column(name = "violation_time", columnDefinition = "BIGINT COMMENT '违规时间'")
    private Long violationTime;

    @Column(name = "is_processed", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否已处理'")
    private Boolean isProcessed = false;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间'")
    private Long createTime;

    @PrePersist
    protected void onCreate() {
        createTime = System.currentTimeMillis();
    }

    /**
     * 违规类型枚举
     */
    public enum ViolationType {
        OVERTIME_NO_CHECKIN("OVERTIME_NO_CHECKIN", "超时未签到"),
        NO_SHOW("NO_SHOW", "未到场使用"),
        EARLY_LEAVE("EARLY_LEAVE", "提前离开");

        private final String code;
        private final String description;

        ViolationType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}