package com.proshine.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预约时间验证请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeValidationRequest {
    private String roomId;
    private String bookingStartTime;
    private String bookingEndTime;
}