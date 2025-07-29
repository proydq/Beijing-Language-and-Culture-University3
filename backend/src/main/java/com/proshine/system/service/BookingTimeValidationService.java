package com.proshine.system.service;

import com.proshine.system.dto.BookingTimeValidationDto;

import java.time.LocalDate;

/**
 * 预约时间验证Service接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface BookingTimeValidationService {

    /**
     * 验证预约时间是否有效
     * 
     * @param request 验证请求
     * @return 验证结果
     */
    BookingTimeValidationDto.CheckTimeResponse checkBookingTime(BookingTimeValidationDto.CheckTimeRequest request);

    /**
     * 获取用户可预约的时间范围
     * 
     * @param userId 用户ID
     * @param date 预约日期
     * @return 可预约时间范围
     */
    BookingTimeValidationDto.AvailableTimesResponse getAvailableBookingTimes(String userId, LocalDate date);
}