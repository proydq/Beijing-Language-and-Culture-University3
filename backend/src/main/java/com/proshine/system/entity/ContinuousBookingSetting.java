package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 教室连续预约设置实体类
 */
@Entity
@Table(name = "tb_continuous_booking_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContinuousBookingSetting {

    public static final String DEFAULT_CSTM_ID = "demo";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(40) COMMENT '主键UUID'")
    private String id;

    @Column(name = "room_id", columnDefinition = "VARCHAR(40) NOT NULL COMMENT '房间ID'")
    private String roomId;

    @Column(name = "continuous_days", columnDefinition = "INT DEFAULT 7 COMMENT '可连续预约天数'")
    private Integer continuousDays = 7;

    @Enumerated(EnumType.STRING)
    @Column(name = "continuous_type", columnDefinition = "VARCHAR(20) DEFAULT 'DAYS' COMMENT '连续类型（DAYS-天数，WEEKS-周数，MONTHS-月数）'")
    private ContinuousType continuousType = ContinuousType.DAYS;

    @Column(name = "is_unlimited", columnDefinition = "BOOLEAN DEFAULT FALSE COMMENT '是否无限制'")
    private Boolean isUnlimited = false;

    @Column(name = "can_continuous", columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '是否允许连续预约'")
    private Boolean canContinuous = true;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(32) DEFAULT 'demo' COMMENT '客户域'")
    private String cstmId = DEFAULT_CSTM_ID;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间（时间戳）'")
    private Long createTime;

    @Column(name = "last_update_time", columnDefinition = "BIGINT COMMENT '最后更新时间（时间戳）'")
    private Long lastUpdateTime;

    /**
     * 连续类型枚举
     */
    public enum ContinuousType {
        DAYS("天数"),
        WEEKS("周数"),
        MONTHS("月数");

        private final String label;

        ContinuousType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

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