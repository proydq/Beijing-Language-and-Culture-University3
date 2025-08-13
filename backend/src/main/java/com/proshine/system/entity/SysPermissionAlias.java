package com.proshine.system.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 权限别名实体类
 */
@Data
@Entity
@Table(name = "sys_permission_alias")
public class SysPermissionAlias {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(length = 36)
    private String id;
    
    @Column(name = "old_code", nullable = false, length = 100, unique = true)
    private String oldCode;
    
    @Column(name = "new_code", nullable = false, length = 100)
    private String newCode;
    
    @Column(length = 200)
    private String description;
    
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    
    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    
    @PrePersist
    protected void onCreate() {
        createdTime = new Date();
        updatedTime = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}