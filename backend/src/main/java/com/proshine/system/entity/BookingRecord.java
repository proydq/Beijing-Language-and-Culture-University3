package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预约记录实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_booking_record")
public class BookingRecord {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "booking_id", columnDefinition = "VARCHAR(36) COMMENT '预约ID'")
    private String bookingId;

    @Column(name = "title", columnDefinition = "VARCHAR(200) COMMENT '记录标题'")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", columnDefinition = "VARCHAR(30) COMMENT '记录类型'")
    private RecordType recordType;

    @Column(name = "operator_id", columnDefinition = "VARCHAR(36) COMMENT '操作人ID'")
    private String operatorId;

    @Column(name = "operator_name", columnDefinition = "VARCHAR(50) COMMENT '操作人姓名'")
    private String operatorName;

    @Column(name = "operator_type", columnDefinition = "VARCHAR(20) COMMENT '操作人类型'")
    private String operatorType;

    @Column(name = "content", columnDefinition = "TEXT COMMENT '记录内容'")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20) COMMENT '操作状态'")
    private OperationStatus status;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '房间ID'")
    private String roomId;

    @Column(name = "room_name", columnDefinition = "VARCHAR(100) COMMENT '房间名称'")
    private String roomName;

    @Column(name = "operation_time", columnDefinition = "DATETIME COMMENT '操作时间'")
    private LocalDateTime operationTime;

    @Column(name = "ip_address", columnDefinition = "VARCHAR(50) COMMENT 'IP地址'")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT COMMENT '用户代理'")
    private String userAgent;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "extend1", columnDefinition = "VARCHAR(255) COMMENT '扩展字段1'")
    private String extend1;

    @Column(name = "extend2", columnDefinition = "VARCHAR(255) COMMENT '扩展字段2'")
    private String extend2;

    /**
     * 记录类型枚举
     */
    public enum RecordType {
        BOOKING_CREATED("预约创建"),
        BOOKING_APPROVED("预约通过"),
        BOOKING_REJECTED("预约拒绝"),
        BOOKING_CANCELLED("预约取消"),
        BOOKING_MODIFIED("预约修改"),
        ROOM_ACCESSED("房间进入"),
        ROOM_EXITED("房间离开"),
        REMOTE_UNLOCK("远程开门"),
        SYSTEM_OPERATION("系统操作"),
        VIOLATION_RECORD("违规记录");

        private final String displayName;

        RecordType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 操作状态枚举
     */
    public enum OperationStatus {
        SUCCESS("成功"),
        FAILED("失败"),
        PENDING("处理中"),
        TIMEOUT("超时");

        private final String displayName;

        OperationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (operationTime == null) {
            operationTime = LocalDateTime.now();
        }
    }
}