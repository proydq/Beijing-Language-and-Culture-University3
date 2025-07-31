package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 房间审批人配置实体
 */
@Entity
@Table(name = "tb_approval_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalConfig {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36) COMMENT '主键ID'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "VARCHAR(36) COMMENT '客户域ID'")
    private String cstmId;

    @Column(name = "room_id", columnDefinition = "VARCHAR(36) COMMENT '房间ID'")
    private String roomId;

    @Column(name = "approval_level", columnDefinition = "INT COMMENT '审批级别'")
    private Integer approvalLevel;

    @Column(name = "approver_id", columnDefinition = "VARCHAR(36) COMMENT '审批人ID'")
    private String approverId;

    @Column(name = "approver_name", columnDefinition = "VARCHAR(50) COMMENT '审批人姓名'")
    private String approverName;

    @Column(name = "approver_type", columnDefinition = "VARCHAR(20) COMMENT '审批人类型'")
    private String approverType;
}
