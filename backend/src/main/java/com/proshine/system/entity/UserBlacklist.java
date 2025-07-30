package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 用户黑名单实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "tb_user_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBlacklist {

    @Id
    @GeneratedValue(generator = "user-blacklist-uuid")
    @GenericGenerator(name = "user-blacklist-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36) COMMENT '黑名单用户ID'")
    private String userId;

    @Column(name = "reason", columnDefinition = "VARCHAR(500) COMMENT '加入黑名单原因'")
    private String reason;

    @Column(name = "blacklist_type", columnDefinition = "VARCHAR(50) COMMENT '黑名单类型'")
    private String blacklistType;

    @Column(name = "violation_count", columnDefinition = "INT DEFAULT 0 COMMENT '违规次数'")
    private Integer violationCount = 0;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间'")
    private Long createTime;

    @Column(name = "create_user_id", columnDefinition = "VARCHAR(36) COMMENT '创建人ID'")
    private String createUserId;

    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否生效'")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createTime = System.currentTimeMillis();
    }

    /**
     * 黑名单类型枚举
     */
    public enum BlacklistType {
        AUTO_VIOLATION("AUTO_VIOLATION", "自动违规"),
        MANUAL("MANUAL", "手动添加");

        private final String code;
        private final String description;

        BlacklistType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}