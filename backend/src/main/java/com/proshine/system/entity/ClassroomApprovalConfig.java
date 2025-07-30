package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 教室审批配置实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "tb_classroom_approval_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomApprovalConfig {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '房间ID'")
    private String roomId;

    @Column(name = "allow_booker_select_approver", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否允许预约人自选审批人'")
    private Boolean allowBookerSelectApprover = true;

    @Column(name = "need_approval", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否需要审批'")
    private Boolean needApproval = false;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间'")
    private Long createTime;

    @Column(name = "last_update_time", columnDefinition = "BIGINT COMMENT '最后更新时间'")
    private Long lastUpdateTime;

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