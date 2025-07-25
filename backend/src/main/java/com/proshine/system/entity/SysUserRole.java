package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 */
@Entity
@Table(name = "sys_user_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRole {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID'")
    private String id;
    
    @Column(name = "user_id", columnDefinition = "VARCHAR(32) COMMENT '用户ID'")
    private String userId;
    
    @Column(name = "role_id", columnDefinition = "VARCHAR(32) COMMENT '角色ID'")
    private String roleId;
    
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}