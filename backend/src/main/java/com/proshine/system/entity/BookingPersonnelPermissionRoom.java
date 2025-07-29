package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预约人员权限配置与房间关联实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "booking_personnel_permission_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingPersonnelPermissionRoom {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "permission_id", columnDefinition = "VARCHAR(36) COMMENT '权限配置ID'")
    private String permissionId;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '房间ID'")
    private String roomId;

    @Column(name = "room_name", columnDefinition = "VARCHAR(100) COMMENT '房间名称'")
    private String roomName;

    @Column(name = "room_code", columnDefinition = "VARCHAR(50) COMMENT '房间号'")
    private String roomCode;

    @Column(name = "room_area_name", columnDefinition = "VARCHAR(100) COMMENT '房间区域名称'")
    private String roomAreaName;

    @Column(name = "customer_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String customerId;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）'")
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
    }
}