package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 教室借用记录实体类
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_room_access_record")
public class RoomAccessRecord {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '教室ID'")
    private String roomId;

    @Column(name = "room_name", columnDefinition = "VARCHAR(100) COMMENT '教室名称（冗余）'")
    private String roomName;

    @Column(name = "building_name", columnDefinition = "VARCHAR(100) COMMENT '楼栋名称（冗余）'")
    private String buildingName;

    @Column(name = "floor", columnDefinition = "VARCHAR(20) COMMENT '楼层（冗余）'")
    private String floor;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36) COMMENT '用户ID'")
    private String userId;

    @Column(name = "name", columnDefinition = "VARCHAR(50) COMMENT '姓名（冗余）'")
    private String name;

    @Column(name = "gender", columnDefinition = "VARCHAR(10) COMMENT '性别（冗余）'")
    private String gender;

    @Column(name = "employee_id", columnDefinition = "VARCHAR(50) COMMENT '工号（冗余）'")
    private String employeeId;

    @Column(name = "phone", columnDefinition = "VARCHAR(20) COMMENT '联系方式（冗余）'")
    private String phone;

    @Column(name = "department", columnDefinition = "VARCHAR(100) COMMENT '部门（冗余）'")
    private String department;

    @Column(name = "user_type", columnDefinition = "VARCHAR(20) COMMENT '用户类型'")
    private String userType;

    @Column(name = "open_method", columnDefinition = "VARCHAR(20) COMMENT '开门方式'")
    private String openMethod;

    @Column(name = "access_time", columnDefinition = "DATETIME COMMENT '通行时间'")
    private LocalDateTime accessTime;

    @Column(name = "access_type", columnDefinition = "VARCHAR(20) COMMENT '通行类型'")
    private String accessType;

    @Column(name = "booking_id", columnDefinition = "VARCHAR(36) COMMENT '预约ID'")
    private String bookingId;

    @Column(name = "booking_name", columnDefinition = "VARCHAR(200) COMMENT '预约名称（冗余）'")
    private String bookingName;

    @Column(name = "booking_period", columnDefinition = "VARCHAR(100) COMMENT '预约时段（冗余）'")
    private String bookingPeriod;

    @Column(name = "device_id", columnDefinition = "VARCHAR(36) COMMENT '门禁设备ID'")
    private String deviceId;

    @Column(name = "device_name", columnDefinition = "VARCHAR(100) COMMENT '门禁设备名称（冗余）'")
    private String deviceName;

    @Column(name = "create_time", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}