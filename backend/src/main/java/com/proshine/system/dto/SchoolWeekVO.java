package com.proshine.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 教学周信息VO
 *
 * @author proshine
 */
public class SchoolWeekVO {

    /**
     * 教学周ID
     */
    private String id;

    /**
     * 教学周数
     */
    private Integer week;

    /**
     * 教学周名称
     */
    private String weekName;

    /**
     * 学年名称
     */
    private String xn;

    /**
     * 学期代码
     */
    private Integer xq;

    /**
     * 教学周开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    /**
     * 教学周结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    public SchoolWeekVO() {
    }

    public SchoolWeekVO(String id, Integer week, String weekName, String xn, Integer xq, String startDate, String endDate) {
        this.id = id;
        this.week = week;
        this.weekName = weekName;
        this.xn = xn;
        this.xq = xq;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    public String getXn() {
        return xn;
    }

    public void setXn(String xn) {
        this.xn = xn;
    }

    public Integer getXq() {
        return xq;
    }

    public void setXq(Integer xq) {
        this.xq = xq;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "SchoolWeekVO{" +
                "id='" + id + '\'' +
                ", week=" + week +
                ", weekName='" + weekName + '\'' +
                ", xn='" + xn + '\'' +
                ", xq=" + xq +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}