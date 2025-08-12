package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.BookingTimeValidationDto;
import com.proshine.system.service.BookingTimeValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 预约时间验证控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/booking-validation")
@Slf4j
public class BookingTimeValidationController {

    @Autowired
    private BookingTimeValidationService bookingTimeValidationService;

    /**
     * 验证预约时间是否有效
     * 
     * @param request 验证请求
     * @return 验证结果
     */
    @PostMapping("/check-time")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingTimeValidationDto.CheckTimeResponse> checkBookingTime(
            @RequestBody BookingTimeValidationDto.CheckTimeRequest request) {
        try {
            BookingTimeValidationDto.CheckTimeResponse result = bookingTimeValidationService.checkBookingTime(request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("验证预约时间失败", e);
            return ResponseEntity.fail("验证预约时间失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户可预约的时间范围
     * 
     * @param userId 用户ID
     * @param date 预约日期
     * @return 可预约时间范围
     */
    @GetMapping("/available-times")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingTimeValidationDto.AvailableTimesResponse> getAvailableBookingTimes(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            BookingTimeValidationDto.AvailableTimesResponse result = 
                bookingTimeValidationService.getAvailableBookingTimes(userId, date);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取可预约时间范围失败, userId: {}, date: {}", userId, date, e);
            return ResponseEntity.fail("获取可预约时间范围失败: " + e.getMessage());
        }
    }
}