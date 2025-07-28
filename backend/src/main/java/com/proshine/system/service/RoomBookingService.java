package com.proshine.system.service;

import com.proshine.system.dto.BookingStatsDto;
import com.proshine.system.dto.BookingDistributionDto;
import com.proshine.system.dto.BookingTrendDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 房屋借用管理服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface RoomBookingService {

    /**
     * 获取借用统计数据
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计数据
     */
    BookingStatsDto getBookingStats(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取借用数据分布
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分布数据
     */
    List<BookingDistributionDto> getBookingDistribution(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取借用趋势数据
     * 
     * @param days 天数（7、15、30、90）
     * @return 趋势数据
     */
    List<BookingTrendDto> getBookingTrend(int days);

    /**
     * 获取借用趋势数据（指定时间范围）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势数据
     */
    List<BookingTrendDto> getBookingTrend(LocalDateTime startTime, LocalDateTime endTime);
}