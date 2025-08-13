package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统角色实体类
 */
@Entity
@Table(name = "sys_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID'")
    private String id;
    
    @Column(name = "role_name", columnDefinition = "VARCHAR(50) COMMENT '角色名称'")
    private String roleName;
    
    @Column(name = "code", columnDefinition = "VARCHAR(50) COMMENT '角色编码（用于权限控制）'")
    private String code;
    
    @Column(name = "role_description", columnDefinition = "TEXT COMMENT '角色描述'")
    private String roleDescription;
    
    @Column(name = "role_type", columnDefinition = "VARCHAR(20) COMMENT '角色类型'")
    private String roleType;
    
    @Column(name = "is_default", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否默认角色'")
    private Boolean isDefault = false;
    
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '角色状态'")
    private String status = "ACTIVE";
    
    @Column(name = "customer_id", columnDefinition = "VARCHAR(32) COMMENT '客户域ID'")
    private String customerId;
    
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;
    
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;
    
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否逻辑删除（0否，1是）'")
    private Boolean deleted = false;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}