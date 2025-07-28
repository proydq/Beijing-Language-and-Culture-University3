package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * 借用趋势数据DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingTrendDto {

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 教师借用次数
     */
    private Long teacherCount;

    /**
     * 学生借用次数
     */
    private Long studentCount;

    /**
     * 总借用次数
     */
    private Long totalCount;

    /**
     * 教师占比
     */
    private Double teacherPercentage;

    /**
     * 学生占比
     */
    private Double studentPercentage;

    /**
     * 构造函数（基础数据）
     * 
     * @param date 日期
     * @param teacherCount 教师借用次数
     * @param studentCount 学生借用次数
     */
    public BookingTrendDto(LocalDate date, Long teacherCount, Long studentCount) {
        this.date = date;
        this.teacherCount = teacherCount != null ? teacherCount : 0L;
        this.studentCount = studentCount != null ? studentCount : 0L;
        this.totalCount = this.teacherCount + this.studentCount;
        
        // 计算占比
        if (this.totalCount > 0) {
            this.teacherPercentage = (double) this.teacherCount / this.totalCount * 100;
            this.studentPercentage = (double) this.studentCount / this.totalCount * 100;
        } else {
            this.teacherPercentage = 0.0;
            this.studentPercentage = 0.0;
        }
    }
}