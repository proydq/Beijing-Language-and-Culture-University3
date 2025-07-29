package com.proshine.system.service.impl;

import com.proshine.system.dto.BookingTimeValidationDto;
import com.proshine.system.entity.BookingTimeRule;
import com.proshine.system.entity.UserBookingLimit;
import com.proshine.system.repository.BookingTimeRuleRepository;
import com.proshine.system.repository.UserBookingLimitRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.BookingTimeValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 预约时间验证服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class BookingTimeValidationServiceImpl implements BookingTimeValidationService {

    @Autowired
    private UserBookingLimitRepository limitRepository;

    @Autowired
    private BookingTimeRuleRepository ruleRepository;

    @Override
    public BookingTimeValidationDto.CheckTimeResponse checkBookingTime(BookingTimeValidationDto.CheckTimeRequest request) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 参数验证
            if (!StringUtils.hasText(request.getUserId()) || 
                !StringUtils.hasText(request.getStartTime()) || 
                !StringUtils.hasText(request.getEndTime()) || 
                !StringUtils.hasText(request.getBookingDate())) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, "参数不完整", "INVALID_PARAMS"
                );
            }
            
            // 解析时间
            LocalDate bookingDate;
            LocalTime startTime;
            LocalTime endTime;
            
            try {
                bookingDate = LocalDate.parse(request.getBookingDate());
                startTime = LocalTime.parse(request.getStartTime());
                endTime = LocalTime.parse(request.getEndTime());
            } catch (DateTimeParseException e) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, "时间格式错误", "INVALID_TIME_FORMAT"
                );
            }
            
            // 检查时间逻辑
            if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, "开始时间必须早于结束时间", "INVALID_TIME_RANGE"
                );
            }
            
            // 检查预约日期不能是过去的日期
            if (bookingDate.isBefore(LocalDate.now())) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, "不能预约过去的日期", "PAST_DATE"
                );
            }
            
            // 获取用户预约限制
            Integer maxAdvanceDays = getUserMaxAdvanceDays(request.getUserId(), customerId);
            
            // 检查提前预约天数限制
            LocalDate maxBookingDate = LocalDate.now().plusDays(maxAdvanceDays);
            if (bookingDate.isAfter(maxBookingDate)) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, String.format("最多只能提前%d天预约", maxAdvanceDays), "EXCEED_ADVANCE_DAYS"
                );
            }
            
            // 检查工作时间（假设工作时间为8:00-18:00）
            LocalTime workStartTime = LocalTime.of(8, 0);
            LocalTime workEndTime = LocalTime.of(18, 0);
            
            if (startTime.isBefore(workStartTime) || endTime.isAfter(workEndTime)) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, "预约时间必须在工作时间内（8:00-18:00）", "OUTSIDE_WORK_HOURS"
                );
            }
            
            // 检查周末（假设周末不允许预约）
            if (bookingDate.getDayOfWeek().getValue() >= 6) {
                return new BookingTimeValidationDto.CheckTimeResponse(
                    false, "周末不允许预约", "WEEKEND_NOT_ALLOWED"
                );
            }
            
            // 所有验证通过
            return new BookingTimeValidationDto.CheckTimeResponse(
                true, "预约时间有效", null
            );
            
        } catch (Exception e) {
            log.error("验证预约时间失败", e);
            return new BookingTimeValidationDto.CheckTimeResponse(
                false, "系统错误，请稍后重试", "SYSTEM_ERROR"
            );
        }
    }

    @Override
    public BookingTimeValidationDto.AvailableTimesResponse getAvailableBookingTimes(String userId, LocalDate date) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 获取用户预约限制
            Integer maxAdvanceDays = getUserMaxAdvanceDays(userId, customerId);
            
            // 检查日期是否在允许范围内
            LocalDate maxBookingDate = LocalDate.now().plusDays(maxAdvanceDays);
            if (date.isAfter(maxBookingDate) || date.isBefore(LocalDate.now())) {
                return new BookingTimeValidationDto.AvailableTimesResponse(
                    userId, date, maxAdvanceDays, new ArrayList<>(), 
                    "所选日期不在可预约范围内"
                );
            }
            
            // 检查是否为周末
            if (date.getDayOfWeek().getValue() >= 6) {
                return new BookingTimeValidationDto.AvailableTimesResponse(
                    userId, date, maxAdvanceDays, new ArrayList<>(), 
                    "周末不允许预约"
                );
            }
            
            // 生成可预约时间段（工作时间内，每2小时一个时间段）
            List<BookingTimeValidationDto.AvailableTimeRange> timeRanges = new ArrayList<>();
            
            LocalTime workStartTime = LocalTime.of(8, 0);
            LocalTime workEndTime = LocalTime.of(18, 0);
            
            LocalTime currentTime = workStartTime;
            while (currentTime.isBefore(workEndTime)) {
                LocalTime endTime = currentTime.plusHours(2);
                if (endTime.isAfter(workEndTime)) {
                    endTime = workEndTime;
                }
                
                timeRanges.add(new BookingTimeValidationDto.AvailableTimeRange(
                    currentTime, endTime, 
                    String.format("%s - %s", 
                        currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    )
                ));
                
                currentTime = currentTime.plusHours(2);
            }
            
            return new BookingTimeValidationDto.AvailableTimesResponse(
                userId, date, maxAdvanceDays, timeRanges, "查询成功"
            );
            
        } catch (Exception e) {
            log.error("获取可预约时间失败, userId: {}, date: {}", userId, date, e);
            return new BookingTimeValidationDto.AvailableTimesResponse(
                userId, date, 0, new ArrayList<>(), "系统错误，请稍后重试"
            );
        }
    }

    /**
     * 获取用户最大提前预约天数
     */
    private Integer getUserMaxAdvanceDays(String userId, String customerId) {
        // 首先查询用户特定的预约限制
        Optional<UserBookingLimit> limitOpt = limitRepository.findByUserIdAndCustomerIdAndDeletedFalse(userId, customerId);
        if (limitOpt.isPresent()) {
            return limitOpt.get().getMaxAdvanceDays();
        }
        
        // 然后查询适用于该用户的预约时间规则
        List<BookingTimeRule> rules = ruleRepository.findByUserIdAndCustomerId(userId, customerId);
        if (!rules.isEmpty()) {
            // 取最大的提前预约天数
            return rules.stream()
                .mapToInt(BookingTimeRule::getAdvanceBookingDays)
                .max()
                .orElse(7); // 默认7天
        }
        
        // 如果都没有设置，返回默认值
        return 7;
    }
}