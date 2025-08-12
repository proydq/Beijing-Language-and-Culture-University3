package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.UserBookingLimitDto;
import com.proshine.system.service.UserBookingLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户预约限制控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/user-booking-limits")
@Slf4j
public class UserBookingLimitController {

    @Autowired
    private UserBookingLimitService userBookingLimitService;

    /**
     * 获取用户预约限制设置
     * 
     * @param userId 用户ID
     * @return 预约限制设置
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_BOOKING_LIMIT_VIEW') or hasAuthority('USER_BOOKING_LIMIT_MANAGE')")
    public ResponseEntity<UserBookingLimitDto> getUserBookingLimit(@PathVariable String userId) {
        try {
            UserBookingLimitDto result = userBookingLimitService.getUserBookingLimit(userId);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取用户预约限制设置失败, userId: {}", userId, e);
            return ResponseEntity.fail("获取用户预约限制失败: " + e.getMessage());
        }
    }

    /**
     * 设置用户预约限制
     * 
     * @param dto 预约限制信息
     * @return 设置后的预约限制
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_BOOKING_LIMIT_EDIT') or hasAuthority('USER_BOOKING_LIMIT_MANAGE')")
    public ResponseEntity<UserBookingLimitDto> setUserBookingLimit(@RequestBody UserBookingLimitDto dto) {
        try {
            UserBookingLimitDto result = userBookingLimitService.setUserBookingLimit(dto);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("设置用户预约限制失败", e);
            return ResponseEntity.fail("设置用户预约限制失败: " + e.getMessage());
        }
    }

    /**
     * 批量设置用户预约限制
     * 
     * @param dtoList 预约限制信息列表
     * @return 设置后的预约限制列表
     */
    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('USER_BOOKING_LIMIT_EDIT') or hasAuthority('USER_BOOKING_LIMIT_MANAGE')")
    public ResponseEntity<List<UserBookingLimitDto>> batchSetUserBookingLimits(@RequestBody List<UserBookingLimitDto> dtoList) {
        try {
            List<UserBookingLimitDto> result = userBookingLimitService.batchSetUserBookingLimits(dtoList);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("批量设置用户预约限制失败", e);
            return ResponseEntity.fail("批量设置用户预约限制失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户预约限制设置
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_BOOKING_LIMIT_DELETE') or hasAuthority('USER_BOOKING_LIMIT_MANAGE')")
    public ResponseEntity<String> deleteUserBookingLimit(@PathVariable String userId) {
        try {
            userBookingLimitService.deleteUserBookingLimit(userId);
            return ResponseEntity.success("删除成功");
        } catch (Exception e) {
            log.error("删除用户预约限制设置失败, userId: {}", userId, e);
            return ResponseEntity.fail("删除用户预约限制失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户的预约限制设置
     * 
     * @return 所有用户的预约限制设置
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER_BOOKING_LIMIT_VIEW') or hasAuthority('USER_BOOKING_LIMIT_MANAGE')")
    public ResponseEntity<List<UserBookingLimitDto>> getAllUserBookingLimits() {
        try {
            List<UserBookingLimitDto> result = userBookingLimitService.getAllUserBookingLimits();
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取所有用户预约限制设置失败", e);
            return ResponseEntity.fail("获取所有用户预约限制失败: " + e.getMessage());
        }
    }
}