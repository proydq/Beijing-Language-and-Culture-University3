package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 预约时间验证响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeValidationResponse {
    private Boolean isValid;
    private String errorCode;
    private String errorMessage;
    private ValidationDetails validationDetails;
    private BookingInfo bookingInfo;
    private String suggestion;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationDetails {
        private Boolean passCurrentTime;
        private Boolean passAdvanceDays;
        private Boolean passContinuousDays;
        private Boolean passTeachingWeek;
        private Boolean passConflictCheck;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long duration;
        private Boolean crossWeeks;
        private List<Integer> weekNumbers;
        private Double totalHours;
    }
}