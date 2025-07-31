package com.proshine.system.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 校历信息实体类
 * @author lenovo
 */
@Data
@Table(name = "tb_school_calendar")
@Entity
public class SchoolCalendar {

        @Id
        @Column(name = "id")
        private String id;

        @Column(name = "cstm_id")
        private String cstmId;

        @Column(name = "xn")
        private String xn;

        @Column(name = "xq")
        private Integer xq;

        @Column(name = "year")
        private Integer year;

        @Column(name = "month")
        private Integer month;

        @Column(name = "week")
        private Integer week;

        @Column(name = "week_day")
        private Integer weekDay;

        @Column(name = "day")
        private Integer day;

        @Column(name = "time")
        private Date time;

        @Transient
        private String dateStr;

        @Column(name = "holiday")
        private Integer holiday;

}
