package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统权限实体类
 */
@Entity
@Table(name = "sys_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID'")
    private String id;
    
    @Column(name = "name", columnDefinition = "VARCHAR(50) COMMENT '权限名'")
    private String name;
    
    @Column(name = "code", columnDefinition = "VARCHAR(100) COMMENT '权限编码（前端按钮、菜单标识）'")
    private String code;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "VARCHAR(20) COMMENT '类型（menu / button）'")
    private Type type;
    
    @Column(name = "parent_id", columnDefinition = "VARCHAR(32) COMMENT '父级ID（支持菜单树结构）'")
    private String parentId;
    
    @Column(name = "url", columnDefinition = "VARCHAR(255) COMMENT '路由地址或权限路径'")
    private String url;
    
    @Column(name = "sort", columnDefinition = "INT COMMENT '排序值'")
    private Integer sort;
    
    @Column(name = "customer_id", columnDefinition = "VARCHAR(32) COMMENT '客户域ID'")
    private String customerId;
    
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;
    
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;
    
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否逻辑删除（0否，1是）'")
    private Boolean deleted = false;
    
    /**
     * 权限类型枚举
     */
    public enum Type {
        MENU, BUTTON
    }
    
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