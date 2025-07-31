package com.proshine.system.dto;

import com.proshine.system.entity.TbAttendanceAdministrativeCourseTimeConfig;
import com.proshine.system.entity.CoursePlanning;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

/**
 * 教室周课表信息响应VO
 *
 * @author proshine
 */
public class RoomWeeklyScheduleVO {

    /**
     * 教学周数
     */
    private Integer week;

    /**
     * 教学周名称
     */
    private String weekName;

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

    /**
     * 课程时间配置列表
     */
    private List<TbAttendanceAdministrativeCourseTimeConfig> courseTimeConfigList;

    /**
     * 课程排课列表
     */
    private List<CoursePlanning> coursePlanningList;

    public RoomWeeklyScheduleVO() {
    }

    public RoomWeeklyScheduleVO(Integer week, String weekName, String startDate, String endDate,
                                List<TbAttendanceAdministrativeCourseTimeConfig> courseTimeConfigList,
                                List<CoursePlanning> coursePlanningList) {
        this.week = week;
        this.weekName = weekName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseTimeConfigList = courseTimeConfigList;
        this.coursePlanningList = coursePlanningList;
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

    public List<TbAttendanceAdministrativeCourseTimeConfig> getCourseTimeConfigList() {
        return courseTimeConfigList;
    }

    public void setCourseTimeConfigList(List<TbAttendanceAdministrativeCourseTimeConfig> courseTimeConfigList) {
        this.courseTimeConfigList = courseTimeConfigList;
    }

    public List<CoursePlanning> getCoursePlanningList() {
        return coursePlanningList;
    }

    public void setCoursePlanningList(List<CoursePlanning> coursePlanningList) {
        this.coursePlanningList = coursePlanningList;
    }

    @Override
    public String toString() {
        return "RoomWeeklyScheduleVO{" +
                "week=" + week +
                ", weekName='" + weekName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", courseTimeConfigList=" + courseTimeConfigList +
                ", coursePlanningList=" + coursePlanningList +
                '}';
    }
}