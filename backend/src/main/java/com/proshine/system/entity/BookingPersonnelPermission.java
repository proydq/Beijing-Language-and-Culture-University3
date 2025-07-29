package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约人员权限配置实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "booking_personnel_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingPersonnelPermission {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "subject", columnDefinition = "VARCHAR(200) COMMENT '权限主题'")
    private String subject;

    @Column(name = "creator_id", columnDefinition = "VARCHAR(36) COMMENT '创建人ID'")
    private String creatorId;

    @Column(name = "creator_name", columnDefinition = "VARCHAR(50) COMMENT '创建人姓名'")
    private String creatorName;

    @Column(name = "customer_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String customerId;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;

    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）'")
    private Boolean deleted = false;

    // 关联的用户列表（一对多关系）
    @OneToMany(mappedBy = "permissionId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingPersonnelPermissionUser> authorizedUsers;

    // 关联的房间列表（一对多关系）
    @OneToMany(mappedBy = "permissionId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingPersonnelPermissionRoom> authorizedRooms;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}