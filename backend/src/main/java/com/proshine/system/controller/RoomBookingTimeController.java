package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.BookingTimeConfigDTO;
import com.proshine.system.dto.request.BookingTimeValidationRequest;
import com.proshine.system.dto.response.BookingTimeValidationResponse;
import com.proshine.system.service.RoomBookingTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 房间预约时间控制器
 */
@Slf4j
@RestController
@RequestMapping("/room-booking")
public class RoomBookingTimeController {
    
    @Autowired
    private RoomBookingTimeService roomBookingTimeService;
    
    @GetMapping("/booking-time-config")
    @PreAuthorize("hasAuthority('booking:time-config:view') or hasAuthority('booking:booking:view') or hasAuthority('booking:booking:manage')")
    public ResponseEntity<BookingTimeConfigDTO> getBookingTimeConfig(
            @RequestParam String roomId) {
        
        log.info("获取预约时间配置，教室ID: {}", roomId);
        
        try {
            BookingTimeConfigDTO config = roomBookingTimeService.getBookingTimeConfig(roomId);
            return ResponseEntity.success(config);
        } catch (Exception e) {
            log.error("获取预约时间配置失败", e);
            return ResponseEntity.fail("获取预约时间配置失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/validate-booking-time")
    @PreAuthorize("hasAuthority('booking:time-config:validate') or hasAuthority('booking:booking:create') or hasAuthority('booking:booking:manage')")
    public ResponseEntity<BookingTimeValidationResponse> validateBookingTime(
            @RequestBody BookingTimeValidationRequest request) {
        
        log.info("验证预约时间，教室ID: {}, 开始时间: {}, 结束时间: {}", 
            request.getRoomId(), request.getBookingStartTime(), request.getBookingEndTime());
        
        try {
            BookingTimeValidationResponse response = roomBookingTimeService.validateBookingTime(request);
            return ResponseEntity.success(response);
        } catch (Exception e) {
            log.error("验证预约时间失败", e);
            return ResponseEntity.fail("验证预约时间失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/current-time-info")
    @PreAuthorize("hasAuthority('booking:time-info:view') or hasAuthority('booking:booking:view') or hasAuthority('booking:booking:manage')")
    public ResponseEntity<BookingTimeConfigDTO> getCurrentTimeInfo() {
        log.info("获取当前时间和教学周信息");
        
        try {
            // 使用默认的教室ID或者不需要教室ID的场景
            // 这里返回基础的时间信息，不包含特定教室的配置
            BookingTimeConfigDTO config = roomBookingTimeService.getBookingTimeConfig(null);
            return ResponseEntity.success(config);
        } catch (Exception e) {
            log.error("获取当前时间信息失败", e);
            return ResponseEntity.fail("获取当前时间信息失败: " + e.getMessage());
        }
    }
}