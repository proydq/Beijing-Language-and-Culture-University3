package com.proshine.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "tb_course_planning")
public class CoursePlanning {

    @Id
    @Column(name = "id", columnDefinition = "varchar(40) comment '主键id'")
    private String id;

    @Column(name = "cstm_id", columnDefinition = "varchar(40) comment '客户域ID，外键'")
    private String cstmId;

    @Column(name = "course_name", columnDefinition = "varchar(255) comment '课程名称，冗余字段'")
    private String courseName;

    @Column(name = "course_section", columnDefinition = "bigint comment '上课节次'")
    private Long courseSection;

    @Column(name = "teacher_name", columnDefinition = "varchar(255) comment '教师名称，冗余字段'")
    private String teacherName;

    @Column(name = "class_room_id", columnDefinition = "varchar(40) comment '教室ID，外键'")
    private String classRoomId;

    @Column(name = "class_room_name", columnDefinition = "varchar(255) comment '教室名称，冗余字段'")
    private String classRoomName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "course_start_time", columnDefinition = "datetime comment '上课开始时间'")
    private Date courseStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "course_end_time", columnDefinition = "datetime comment '课程结束时间'")
    private Date courseEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "course_date", columnDefinition = "date comment '课程日期'")
    private Date courseDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time", columnDefinition = "datetime comment '课程计划创建时间'")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "last_update_time", columnDefinition = "datetime comment '最近更新时间'")
    private Date lastUpdateTime;

    @Column(name = "week_day", columnDefinition = "bigint comment '星期几（1~7）'")
    private Long weekDay;

    @Column(name = "week_number", columnDefinition = "varchar(20) comment '教学周（第几周）'")
    private String weekNumber;
}
