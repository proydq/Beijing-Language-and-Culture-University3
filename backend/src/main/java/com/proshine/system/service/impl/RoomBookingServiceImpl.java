package com.proshine.system.service.impl;


import com.proshine.system.dto.BookingDistributionDto;
import com.proshine.system.dto.BookingStatsDto;
import com.proshine.system.dto.BookingTrendDto;
import com.proshine.system.repository.RoomBookingRepository;
import com.proshine.system.repository.RoomRepository;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.BookingPersonnelPermissionService;
import com.proshine.system.service.RoomBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房屋借用管理服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class RoomBookingServiceImpl implements RoomBookingService {

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private BookingPersonnelPermissionService permissionService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public BookingStatsDto getBookingStats(LocalDateTime startTime, LocalDateTime endTime) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 统计总借用次数
        Long totalBookings = roomBookingRepository.countTotalBookings(customerId, startTime, endTime);
        
        // 统计教师借用次数
        Long teacherBookings = roomBookingRepository.countTeacherBookings(customerId, startTime, endTime);
        
        // 统计学生借用次数
        Long studentBookings = roomBookingRepository.countStudentBookings(customerId, startTime, endTime);
        
        // 统计待审批数量
        Long pendingApprovals = roomBookingRepository.countPendingApprovals(customerId);
        
        // 计算成功率（这里简化为通过的预约数 / 总预约数）
        Double successRate = 0.0;
        if (totalBookings != null && totalBookings > 0) {
            successRate = (double) totalBookings / (totalBookings + (pendingApprovals != null ? pendingApprovals : 0)) * 100;
        }
        
        return new BookingStatsDto(
            totalBookings != null ? totalBookings : 0L,
            teacherBookings != null ? teacherBookings : 0L,
            studentBookings != null ? studentBookings : 0L,
            pendingApprovals != null ? pendingApprovals : 0L,
            totalBookings != null ? totalBookings : 0L, // 已通过审批数量等于总借用次数（因为查询条件已过滤）
            0L, // 已拒绝数量（需要额外查询）
            0L, // 进行中的预约数量（需要额外查询）
            0L, // 已完成的预约数量（需要额外查询）
            successRate
        );
    }

    @Override
    public List<BookingDistributionDto> getBookingDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        String customerId = SecurityUtil.getCustomerId();
        
        List<Object[]> results = roomBookingRepository.getBookingDistribution(customerId, startTime, endTime);
        
        // 计算总数
        long totalCount = results.stream()
            .mapToLong(result -> ((Number) result[1]).longValue())
            .sum();
        
        // 转换为DTO并计算百分比
        return results.stream()
            .map(result -> {
                String type = (String) result[0];
                Long count = ((Number) result[1]).longValue();
                Double percentage = totalCount > 0 ? (double) count / totalCount * 100 : 0.0;
                return new BookingDistributionDto(type, count, percentage);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingTrendDto> getBookingTrend(int days) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        return getBookingTrend(startTime, endTime);
    }

    @Override
    public List<BookingTrendDto> getBookingTrend(LocalDateTime startTime, LocalDateTime endTime) {
        String customerId = SecurityUtil.getCustomerId();
        
        List<Object[]> results = roomBookingRepository.getBookingTrend(customerId, startTime, endTime);
        
        // 按日期分组数据
        Map<LocalDate, Map<String, Long>> dateGroupedData = new HashMap<>();
        
        for (Object[] result : results) {
            LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
            String type = (String) result[1];
            Long count = ((Number) result[2]).longValue();
            
            dateGroupedData.computeIfAbsent(date, k -> new HashMap<>()).put(type, count);
        }
        
        // 生成完整的日期范围数据
        List<BookingTrendDto> trendData = new ArrayList<>();
        LocalDate currentDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        
        while (!currentDate.isAfter(endDate)) {
            Map<String, Long> dayData = dateGroupedData.getOrDefault(currentDate, new HashMap<>());
            Long teacherCount = dayData.getOrDefault("教师", 0L);
            Long studentCount = dayData.getOrDefault("学生", 0L);
            
            trendData.add(new BookingTrendDto(currentDate, teacherCount, studentCount));
            currentDate = currentDate.plusDays(1);
        }
        
        return trendData;
    }


}