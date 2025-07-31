package com.proshine.system.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 考勤课程节次时间配置实体类
 */
@Data
@Table(name = "tb_attendance_administrative_course_time_config")
@Entity
public class TbAttendanceAdministrativeCourseTimeConfig {


    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id")
    private String id;

    /**
     * 客户域ID
     */
    @Column(name = "cstm_id")
    private String cstmId;

    /**
     * 上课节次
     */
    @Column(name = "course_sort")
    private Integer courseSort;

    /**
     * 课程开始时间字符串
     */
    @Column(name = "course_start_time")
    private String courseStartTime;

    /**
     * 课程结束时间字符串
     */
    @Column(name = "course_end_time")
    private String courseEndTime;

    /**
     * 记录创建时间
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 最后更新时间
     */
    @Column(name = "last_update_time")
    private Long lastUpdateTime;

    /**
     * 上午、中午、晚上 分别是1,2,3
     */
    @Column(name = "time_type")
    private Integer timeType;

    @Transient
    private Boolean flag;
}
