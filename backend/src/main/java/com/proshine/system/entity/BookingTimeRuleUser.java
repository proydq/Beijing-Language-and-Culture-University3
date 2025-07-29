package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预约时间规则用户关联实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "booking_time_rule_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeRuleUser {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "rule_id", columnDefinition = "VARCHAR(36) NOT NULL COMMENT '规则ID'")
    private String ruleId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36) NOT NULL COMMENT '用户ID'")
    private String userId;

    @Column(name = "user_name", columnDefinition = "VARCHAR(50) COMMENT '用户姓名'")
    private String userName;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）'")
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}