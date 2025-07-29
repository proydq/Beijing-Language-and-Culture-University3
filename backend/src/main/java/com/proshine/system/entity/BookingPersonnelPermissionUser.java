package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预约人员权限配置与用户关联实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "booking_personnel_permission_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingPersonnelPermissionUser {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "permission_id", columnDefinition = "VARCHAR(36) COMMENT '权限配置ID'")
    private String permissionId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36) COMMENT '用户ID'")
    private String userId;

    @Column(name = "user_name", columnDefinition = "VARCHAR(50) COMMENT '用户姓名'")
    private String userName;

    @Column(name = "job_number", columnDefinition = "VARCHAR(50) COMMENT '工号'")
    private String jobNumber;

    @Column(name = "department_name", columnDefinition = "VARCHAR(100) COMMENT '部门名称'")
    private String departmentName;

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