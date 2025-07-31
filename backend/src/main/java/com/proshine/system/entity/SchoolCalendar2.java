package com.proshine.system.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 教学周信息实体类
 * @author lenovo
 */
@Data
@Table(name = "tb_school_calendar2")
@Entity
public class SchoolCalendar2 {

        @Id
        @Column(name = "id")
        private String id;

	@Column(name="cstm_id")
	private String customerId;

	@Column(columnDefinition = "varchar(10) comment '学年名称'")
	private String xn;

	@Column(columnDefinition = "int(1) comment '学期代码'")
	private Integer xq;

	@Column(columnDefinition = "int(2) comment '教学周周次'")
	private Integer week;

        @Column(name = "start_date")
        private Date startDate;

        @Column(name = "end_date")
        private Date endDate;

	@Column(columnDefinition = "varchar(20) comment '教学周开始日期字符串'")
	private String startDateStr;

	@Column(columnDefinition = "varchar(20) comment '教学周结束日期字符串'")
	private String endDateStr;
}
