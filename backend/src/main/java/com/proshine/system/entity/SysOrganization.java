package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织机构实体类
 */
@Entity
@Table(name = "sys_organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOrganization {

    public static final String DEFAULT_CSTM_ID = "demo";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID'")
    private String id;

    @Column(name = "name", columnDefinition = "VARCHAR(100) COMMENT '组织名称'")
    private String name;

    @Column(name = "code", columnDefinition = "VARCHAR(100) COMMENT '组织编码'")
    private String code;

    @Column(name = "description", columnDefinition = "TEXT COMMENT '组织描述'")
    private String description;

    @Column(name = "parent_id", columnDefinition = "VARCHAR(32) COMMENT '上级组织ID'")
    private String parentId;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(32) DEFAULT 'demo' COMMENT '客户域'")
    private String cstmId = DEFAULT_CSTM_ID;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;

    @Transient
    private List<SysOrganization> children;

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
