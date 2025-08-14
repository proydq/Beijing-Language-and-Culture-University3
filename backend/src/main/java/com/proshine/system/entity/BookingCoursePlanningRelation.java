package com.proshine.system.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预约与排课关联关系实体
 * 用于记录预约通过后生成的排课记录
 */
@Data
@Entity
@Table(name = "tb_booking_course_planning_relation", 
    indexes = {
        @Index(name = "idx_booking_id", columnList = "booking_id"),
        @Index(name = "idx_course_planning_id", columnList = "course_planning_id")
    })
public class BookingCoursePlanningRelation {

    @Id
    @Column(name = "id", columnDefinition = "varchar(40) comment '主键id'")
    private String id;

    @Column(name = "booking_id", nullable = false, columnDefinition = "varchar(40) comment '预约ID'")
    private String bookingId;

    @Column(name = "course_planning_id", nullable = false, columnDefinition = "varchar(40) comment '排课记录ID'")
    private String coursePlanningId;

    @Column(name = "cstm_id", columnDefinition = "varchar(40) comment '客户域ID'")
    private String cstmId;

    @CreationTimestamp
    @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
    private LocalDateTime createTime;
}