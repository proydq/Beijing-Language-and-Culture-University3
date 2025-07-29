package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约时间验证相关DTO
 * 
 * @author system
 * @date 2024-01-01
 */
public class BookingTimeValidationDto {

    /**
     * 预约时间验证请求
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckTimeRequest {
        private String userId;
        private String startTime;
        private String endTime;
        private String bookingDate;
    }

    /**
     * 预约时间验证响应
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckTimeResponse {
        private Boolean valid;
        private String message;
        private String errorCode;
    }

    /**
     * 可预约时间范围
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTimeRange {
        private LocalTime startTime;
        private LocalTime endTime;
        private String description;
    }

    /**
     * 可预约时间响应
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTimesResponse {
        private String userId;
        private LocalDate date;
        private Integer maxAdvanceDays;
        private List<AvailableTimeRange> availableTimeRanges;
        private String message;
    }
}