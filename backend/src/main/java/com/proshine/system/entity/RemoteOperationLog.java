package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 远程操作日志实体类
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_remote_operation_log")
public class RemoteOperationLog {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "operator_id", columnDefinition = "VARCHAR(36) COMMENT '操作员ID'")
    private String operatorId;

    @Column(name = "operator_name", columnDefinition = "VARCHAR(50) COMMENT '操作员姓名'")
    private String operatorName;

    @Column(name = "operation_type", columnDefinition = "VARCHAR(20) COMMENT '操作类型'")
    private String operationType;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '教室ID'")
    private String roomId;

    @Column(name = "room_name", columnDefinition = "VARCHAR(100) COMMENT '教室名称'")
    private String roomName;

    @Column(name = "device_id", columnDefinition = "VARCHAR(36) COMMENT '设备ID'")
    private String deviceId;

    @Column(name = "reason", columnDefinition = "VARCHAR(500) COMMENT '操作原因'")
    private String reason;

    @Column(name = "result", columnDefinition = "VARCHAR(20) COMMENT '操作结果'")
    private String result;

    @Column(name = "operation_time", columnDefinition = "DATETIME COMMENT '操作时间'")
    private LocalDateTime operationTime;

    @Column(name = "ip_address", columnDefinition = "VARCHAR(50) COMMENT '操作IP地址'")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "VARCHAR(500) COMMENT '用户代理'")
    private String userAgent;

    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (operationTime == null) {
            operationTime = LocalDateTime.now();
        }
    }
}