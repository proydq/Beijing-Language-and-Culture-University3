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
    
    @Column(name = "name", columnDefinition = "VARCHAR(50) COMMENT '角色名称'")
    private String name;
    
    @Column(name = "code", columnDefinition = "VARCHAR(50) COMMENT '角色编码（用于权限控制）'")
    private String code;
    
    @Column(name = "remark", columnDefinition = "TEXT COMMENT '描述'")
    private String remark;
    
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