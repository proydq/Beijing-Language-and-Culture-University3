package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 审批级别配置实体类
 * 
 * @author system
 * @date 2024-01-01
 */
@Entity
@Table(name = "tb_classroom_approval_level")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomApprovalLevel {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "config_id", columnDefinition = "VARCHAR(36) COMMENT '审批配置ID'")
    private String configId;

    @Column(name = "level_number", columnDefinition = "INT COMMENT '审批级别（1,2,3）'")
    private Integer levelNumber;

    @Column(name = "approver_ids", columnDefinition = "TEXT COMMENT '审批人ID列表（JSON格式）'")
    private String approverIds;

    @Column(name = "approver_names", columnDefinition = "TEXT COMMENT '审批人姓名列表（逗号分隔）'")
    private String approverNames;

    @Column(name = "create_time", columnDefinition = "BIGINT COMMENT '创建时间'")
    private Long createTime;

    @PrePersist
    protected void onCreate() {
        createTime = System.currentTimeMillis();
    }
}