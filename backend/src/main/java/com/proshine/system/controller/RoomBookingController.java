package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.BookingStatsDto;
import com.proshine.system.dto.BookingDistributionDto;
import com.proshine.system.dto.BookingTrendDto;
import com.proshine.system.service.RoomBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 房屋借用管理控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/api/room-booking")
@Slf4j
public class RoomBookingController {

    @Autowired
    private RoomBookingService roomBookingService;

    /**
     * 获取借用统计数据
     * 支持时间范围筛选
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<BookingStatsDto> getBookingStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            log.info("==========/api/room-booking/stats [GET]=============startTime:{}, endTime:{}", startTime, endTime);
            BookingStatsDto result = roomBookingService.getBookingStats(startTime, endTime);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取借用统计数据失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取借用数据分布（饼图数据）
     * 展示学生和教师的借用数据分布
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分布数据
     */
    @GetMapping("/distribution")
    public ResponseEntity<List<BookingDistributionDto>> getBookingDistribution(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            log.info("==========/api/room-booking/distribution [GET]=============startTime:{}, endTime:{}", startTime, endTime);
            List<BookingDistributionDto> result = roomBookingService.getBookingDistribution(startTime, endTime);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取借用数据分布失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取借用趋势数据（折线图数据）
     * 支持快捷时间选择：近7天、近15天、近30天、近90天
     * 展现学生和教师在同一时间的借用占比数据
     * 
     * @param days 天数（7、15、30、90）
     * @return 趋势数据
     */
    @GetMapping("/trend")
    public ResponseEntity<List<BookingTrendDto>> getBookingTrend(
            @RequestParam(defaultValue = "7") int days) {
        try {
            log.info("==========/api/room-booking/trend [GET]=============days:{}", days);
            
            // 验证天数参数
            if (days != 7 && days != 15 && days != 30 && days != 90) {
                return ResponseEntity.fail("天数参数只支持7、15、30、90");
            }
            
            List<BookingTrendDto> result = roomBookingService.getBookingTrend(days);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取借用趋势数据失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取借用趋势数据（自定义时间范围）
     * 展现学生和教师在同一时间的借用占比数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势数据
     */
    @GetMapping("/trend/custom")
    public ResponseEntity<List<BookingTrendDto>> getBookingTrendCustom(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            log.info("==========/api/room-booking/trend/custom [GET]=============startTime:{}, endTime:{}", startTime, endTime);
            
            // 验证时间范围
            if (startTime.isAfter(endTime)) {
                return ResponseEntity.fail("开始时间不能晚于结束时间");
            }
            
            List<BookingTrendDto> result = roomBookingService.getBookingTrend(startTime, endTime);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取借用趋势数据失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}