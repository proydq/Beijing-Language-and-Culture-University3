package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * 区域实体类
 */
@Entity
@Table(name = "tb_area")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {

    public static final String DEFAULT_CSTM_ID = "demo";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(40) COMMENT '主键UUID'")
    private String id;

    @Column(name = "area_name", columnDefinition = "VARCHAR(100) COMMENT '区域名称'")
    private String areaName;

    @Column(name = "parent_id", columnDefinition = "VARCHAR(32) COMMENT '父节点ID'")
    private String parentId;

    @Column(name = "path_deep", columnDefinition = "INT COMMENT '路径深度'")
    private Integer pathDeep;

    @Column(name = "type", columnDefinition = "VARCHAR(50) COMMENT '类型（building/floor/room）'")
    private String type;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(32) DEFAULT 'demo' COMMENT '客户域'")
    private String cstmId = DEFAULT_CSTM_ID;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间（时间戳）'")
    private Long createTime;

    @Column(name = "last_update_time", columnDefinition = "BIGINT COMMENT '最后更新时间（时间戳）'")
    private Long lastUpdateTime;

    @Column(name = "extend1", columnDefinition = "VARCHAR(255) COMMENT '扩展字段1'")
    private String extend1;

    @Column(name = "extend2", columnDefinition = "VARCHAR(255) COMMENT '扩展字段2'")
    private String extend2;

    @Transient
    private List<Area> children;

    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis();
        createTime = now;
        lastUpdateTime = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = System.currentTimeMillis();
    }
}