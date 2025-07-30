package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 违规设置实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "tb_violation_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationSetting {

    @Id
    @GeneratedValue(generator = "violation-setting-uuid")
    @GenericGenerator(name = "violation-setting-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '教室ID'")
    private String roomId;

    @Column(name = "start_time_minutes", columnDefinition = "INT DEFAULT 30 COMMENT '超时分钟数'")
    private Integer startTimeMinutes = 30;

    @Column(name = "violation_count_threshold", columnDefinition = "INT DEFAULT 3 COMMENT '违规次数阈值'")
    private Integer violationCountThreshold = 3;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间'")
    private Long createTime;

    @Column(name = "last_update_time", columnDefinition = "BIGINT COMMENT '最后更新时间'")
    private Long lastUpdateTime;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否删除'")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        long currentTime = System.currentTimeMillis();
        createTime = currentTime;
        lastUpdateTime = currentTime;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = System.currentTimeMillis();
    }
}