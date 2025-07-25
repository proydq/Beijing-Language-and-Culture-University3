package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 */
@Entity
@Table(name = "sys_role_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRolePermission {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID'")
    private String id;
    
    @Column(name = "role_id", columnDefinition = "VARCHAR(32) COMMENT '角色ID'")
    private String roleId;
    
    @Column(name = "permission_id", columnDefinition = "VARCHAR(32) COMMENT '权限ID'")
    private String permissionId;
    
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}