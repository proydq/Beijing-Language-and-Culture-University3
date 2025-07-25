package com.proshine.system.entity;

import com.proshine.common.enums.GlobalEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Position entity.
 */
@Entity
@Table(name = "sys_position")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPosition {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID'")
    private String id;

    @Column(name = "name", columnDefinition = "VARCHAR(100) COMMENT '职务名称'")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT COMMENT '描述'")
    private String description;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(32) DEFAULT 'demo' COMMENT '客户域'")
    private String cstmId = GlobalEnum.DEFAULT_CUSTOMER_ID;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;

    @PrePersist
    private void onCreate() {
        this.id = UUID.randomUUID().toString().replace("-", "");
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
