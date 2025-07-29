package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约时间规则实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "booking_time_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeRule {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(40) COMMENT '主键ID'")
    private String id;

    @Column(name = "rule_name", columnDefinition = "VARCHAR(200) NOT NULL COMMENT '规则名称'")
    private String ruleName;

    @Column(name = "advance_booking_days", columnDefinition = "INT NOT NULL COMMENT '提前预约天数'")
    private Integer advanceBookingDays;

    @Column(name = "creator_id", columnDefinition = "VARCHAR(36) COMMENT '创建人ID'")
    private String creatorId;

    @Column(name = "creator_name", columnDefinition = "VARCHAR(50) COMMENT '创建人姓名'")
    private String creatorName;

    @Column(name = "customer_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String customerId;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;

    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）'")
    private Boolean deleted = false;

    // 关联的用户列表
    @OneToMany(mappedBy = "ruleId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingTimeRuleUser> applicableUsers;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (updateTime == null) {
            updateTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}