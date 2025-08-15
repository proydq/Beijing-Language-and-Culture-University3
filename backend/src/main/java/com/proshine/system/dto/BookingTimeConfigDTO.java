package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约时间配置DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeConfigDTO {
    
    /**
     * 服务器当前时间
     */
    private ServerTime serverTime;
    
    /**
     * 当前教学周信息
     */
    private CurrentWeekInfo currentWeekInfo;
    
    /**
     * 预约时间限制
     */
    private BookingLimits bookingLimits;
    
    /**
     * 教学周列表
     */
    private List<TeachingWeek> teachingWeeks;
    
    /**
     * 学期信息
     */
    private SemesterInfo semesterInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerTime {
        private String currentDateTime;
        private String currentDate;
        private Long timestamp;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentWeekInfo {
        private String academicYear;
        private Integer semester;
        private String semesterName;
        private Integer weekNumber;
        private String weekName;
        private String weekStartDate;
        private String weekEndDate;
        private Integer currentDayOfWeek;
        private String currentDayName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingLimits {
        private Integer advanceBookingDays;
        private Integer continuousBookingDays;
        private Boolean canContinuousBooking;
        private String minBookingDate;
        private String maxBookingDate;
        private String minBookingTime;
        private List<BookableDate> availableDates;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookableDate {
        private String date;
        private Integer dayOfWeek;
        private String dayName;
        private Integer weekNumber;
        private Boolean isToday;
        private String minTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeachingWeek {
        private Integer weekNumber;
        private String weekName;
        private String startDate;
        private String endDate;
        private Boolean isCurrentWeek;
        private Boolean isBookable;
        private Integer bookableDays;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterInfo {
        private String semesterName;
        private Integer totalWeeks;
        private Integer currentWeekIndex;
        private String semesterStartDate;
        private String semesterEndDate;
    }
}