package com.proshine.system.service.impl;

import com.proshine.system.dto.BookingTimeConfigDTO;
import com.proshine.system.dto.BookingTimeRuleDto;
import com.proshine.system.dto.request.BookingTimeValidationRequest;
import com.proshine.system.dto.response.BookingTimeValidationResponse;
import com.proshine.system.entity.*;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 房间预约时间服务实现类
 */
@Slf4j
@Service
public class RoomBookingTimeServiceImpl implements RoomBookingTimeService {
    
    @Autowired
    private BookingPersonnelPermissionService bookingPersonnelPermissionService;
    
    @Autowired
    private BookingPersonnelPermissionRepository bookingPersonnelPermissionRepository;
    
    @Autowired
    private BookingTimeRuleService bookingTimeRuleService;
    
    @Autowired
    private ContinuousBookingSettingService continuousBookingSettingService;
    
    @Autowired
    private SchoolCalendar2Repository schoolCalendar2Repository;
    
    @Autowired
    private RoomBookingRepository roomBookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private static final String[] DAY_NAMES = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    
    @Override
    @Transactional(readOnly = true)
    public BookingTimeConfigDTO getBookingTimeConfig(String roomId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            String userId = SecurityUtil.getCurrentUserId();
            
            // 1. 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            
            // 2. 获取用户预约时间规则配置
            int advanceBookingDays = 365; // 默认值：不限制（设为365天）
            try {
                // 优先从BookingTimeRule系统获取
                List<BookingTimeRuleDto> rules = bookingTimeRuleService.getBookingTimeRulesByUserId(userId);
                if (rules != null && !rules.isEmpty()) {
                    // 如果用户有多个规则，取第一个规则的天数
                    BookingTimeRuleDto firstRule = rules.get(0);
                    if (firstRule.getAdvanceBookingDays() != null) {
                        advanceBookingDays = firstRule.getAdvanceBookingDays();
                        log.info("使用BookingTimeRule系统配置，用户ID: {}, 提前预约天数: {}, 规则名称: {}", 
                            userId, advanceBookingDays, firstRule.getRuleName());
                    }
                } else if (roomId != null) {
                    // 降级到原BookingPersonnelPermission系统
                    BookingPersonnelPermission permission = getPermission(userId, roomId);
                    if (permission != null && permission.getAdvanceBookingDays() != null) {
                        advanceBookingDays = permission.getAdvanceBookingDays();
                        log.info("降级使用BookingPersonnelPermission系统配置，用户ID: {}, 房间ID: {}, 提前预约天数: {}", 
                            userId, roomId, advanceBookingDays);
                    }
                }
            } catch (Exception e) {
                log.warn("获取预约时间规则失败，使用默认值，用户ID: {}", userId, e);
                // 如果获取规则失败，降级到原系统
                if (roomId != null) {
                    try {
                        BookingPersonnelPermission permission = getPermission(userId, roomId);
                        if (permission != null && permission.getAdvanceBookingDays() != null) {
                            advanceBookingDays = permission.getAdvanceBookingDays();
                        }
                    } catch (Exception ex) {
                        log.warn("降级获取BookingPersonnelPermission也失败，使用默认值365天", ex);
                    }
                }
            }
            
            // 3. 获取教室连续预约设置（如果roomId不为空）
            int continuousBookingDays = 7; // 默认值：7天
            boolean canContinuousBooking = true;
            boolean isUnlimited = false; // 默认值：非无限制
            
            if (roomId != null) {
                ContinuousBookingSetting setting = continuousBookingSettingService.getByRoomId(roomId);
                if (setting != null) {
                    canContinuousBooking = Boolean.TRUE.equals(setting.getCanContinuous());
                    isUnlimited = Boolean.TRUE.equals(setting.getIsUnlimited());
                    // 始终返回数据库原值，前端根据isUnlimited判断显示
                    if (canContinuousBooking && setting.getContinuousDays() != null) {
                        continuousBookingDays = setting.getContinuousDays();
                    }
                }
            }
            
            // 4. 获取当前教学周
            SchoolCalendar2 currentWeek = getCurrentWeek(customerId, today);
            
            // 5. 获取学期信息
            List<SchoolCalendar2> semesterWeeks = getSemesterWeeks(customerId);
            
            // 6. 计算可预约日期范围
            LocalDate maxBookingDate = today.plusDays(advanceBookingDays);
            
            // 如果有学期信息，不能超过学期结束日期
            if (!semesterWeeks.isEmpty()) {
                LocalDate semesterEndDate = convertToLocalDate(
                    semesterWeeks.get(semesterWeeks.size() - 1).getEndDate()
                );
                if (maxBookingDate.isAfter(semesterEndDate)) {
                    maxBookingDate = semesterEndDate;
                }
            }
            
            // 7. 构建响应数据
            BookingTimeConfigDTO dto = new BookingTimeConfigDTO();
            
            // 服务器时间
            dto.setServerTime(BookingTimeConfigDTO.ServerTime.builder()
                .currentDateTime(now.format(DATETIME_FORMATTER))
                .currentDate(today.format(DATE_FORMATTER))
                .timestamp(System.currentTimeMillis())
                .build());
            
            // 当前教学周信息
            if (currentWeek != null) {
                dto.setCurrentWeekInfo(buildCurrentWeekInfo(currentWeek, now));
            }
            
            // 预约限制
            BookingTimeConfigDTO.BookingLimits limits = BookingTimeConfigDTO.BookingLimits.builder()
                .advanceBookingDays(advanceBookingDays)
                .continuousBookingDays(continuousBookingDays)
                .canContinuousBooking(canContinuousBooking)
                .isUnlimited(isUnlimited)
                .minBookingDate(today.format(DATE_FORMATTER))
                .maxBookingDate(maxBookingDate.format(DATE_FORMATTER))
                .minBookingTime(now.format(TIME_FORMATTER))
                .availableDates(getAvailableDates(today, maxBookingDate, semesterWeeks, now))
                .build();
            dto.setBookingLimits(limits);
            
            // 教学周列表
            dto.setTeachingWeeks(buildTeachingWeeks(semesterWeeks, currentWeek, today, maxBookingDate));
            
            // 学期信息
            if (!semesterWeeks.isEmpty()) {
                dto.setSemesterInfo(buildSemesterInfo(semesterWeeks, currentWeek));
            }
            
            return dto;
            
        } catch (Exception e) {
            log.error("获取预约时间配置失败", e);
            throw new RuntimeException("获取预约时间配置失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingTimeValidationResponse validateBookingTime(BookingTimeValidationRequest request) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            String userId = SecurityUtil.getCurrentUserId();
            
            // 解析时间
            LocalDateTime startTime = LocalDateTime.parse(request.getBookingStartTime(), DATETIME_FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(request.getBookingEndTime(), DATETIME_FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            
            BookingTimeValidationResponse.ValidationDetails details = BookingTimeValidationResponse.ValidationDetails.builder()
                .passCurrentTime(true)
                .passAdvanceDays(true)
                .passContinuousDays(true)
                .passTeachingWeek(true)
                .passConflictCheck(true)
                .build();
            
            String errorCode = null;
            String errorMessage = null;
            String suggestion = null;
            
            // 1. 验证是否晚于当前时间
            if (startTime.isBefore(now)) {
                details.setPassCurrentTime(false);
                errorCode = "START_TIME_PAST";
                errorMessage = "开始时间不能早于当前时间";
                suggestion = String.format("请选择%s之后的时间", now.format(DATETIME_FORMATTER));
            }
            
            // 2. 验证提前预约天数
            if (errorCode == null) {
                int advanceDays = 365; // 默认值
                try {
                    // 优先从BookingTimeRule系统获取
                    List<BookingTimeRuleDto> rules = bookingTimeRuleService.getBookingTimeRulesByUserId(userId);
                    if (rules != null && !rules.isEmpty()) {
                        BookingTimeRuleDto firstRule = rules.get(0);
                        if (firstRule.getAdvanceBookingDays() != null) {
                            advanceDays = firstRule.getAdvanceBookingDays();
                        }
                    } else {
                        // 降级到原BookingPersonnelPermission系统
                        BookingPersonnelPermission permission = getPermission(userId, request.getRoomId());
                        if (permission != null && permission.getAdvanceBookingDays() != null) {
                            advanceDays = permission.getAdvanceBookingDays();
                        }
                    }
                } catch (Exception e) {
                    log.warn("验证时获取预约时间规则失败，使用默认值，用户ID: {}", userId, e);
                    // 降级到原系统
                    try {
                        BookingPersonnelPermission permission = getPermission(userId, request.getRoomId());
                        if (permission != null && permission.getAdvanceBookingDays() != null) {
                            advanceDays = permission.getAdvanceBookingDays();
                        }
                    } catch (Exception ex) {
                        log.warn("降级获取BookingPersonnelPermission也失败，使用默认值365天", ex);
                    }
                }
                
                LocalDate maxDate = LocalDate.now().plusDays(advanceDays);
                
                if (endTime.toLocalDate().isAfter(maxDate)) {
                    details.setPassAdvanceDays(false);
                    errorCode = "EXCEED_ADVANCE_DAYS";
                    errorMessage = String.format("超出允许的提前预约天数（%d天）", advanceDays);
                    suggestion = String.format("请选择%s之前的日期", maxDate.format(DATE_FORMATTER));
                }
            }
            
            // 3. 验证连续预约天数
            if (errorCode == null) {
                long duration = ChronoUnit.DAYS.between(startTime.toLocalDate(), endTime.toLocalDate()) + 1;
                ContinuousBookingSetting setting = continuousBookingSettingService.getByRoomId(request.getRoomId());
                
                if (setting != null && Boolean.TRUE.equals(setting.getCanContinuous())) {
                    // 如果是无限制预约，跳过天数验证
                    if (Boolean.TRUE.equals(setting.getIsUnlimited())) {
                        // 无限制预约，不验证天数限制
                        log.debug("房间{}设置为无限制预约，跳过连续预约天数验证", request.getRoomId());
                    } else {
                        // 按指定天数限制验证
                        int maxContinuousDays = setting.getContinuousDays() != null ? setting.getContinuousDays() : 7;
                        if (duration > maxContinuousDays) {
                            details.setPassContinuousDays(false);
                            errorCode = "EXCEED_CONTINUOUS_DAYS";
                            errorMessage = String.format("超出允许的连续预约天数（%d天）", maxContinuousDays);
                            suggestion = String.format("单次预约不能超过%d天", maxContinuousDays);
                        }
                    }
                } else if (setting != null && !Boolean.TRUE.equals(setting.getCanContinuous())) {
                    // 不允许连续预约
                    if (duration > 1) {
                        details.setPassContinuousDays(false);
                        errorCode = "NO_CONTINUOUS_BOOKING";
                        errorMessage = "该教室不允许连续预约";
                        suggestion = "请选择单日预约";
                    }
                }
            }
            
            // 4. 验证是否在教学周内
            if (errorCode == null) {
                if (!isInTeachingWeek(startTime.toLocalDate(), customerId) || 
                    !isInTeachingWeek(endTime.toLocalDate(), customerId)) {
                    details.setPassTeachingWeek(false);
                    errorCode = "NOT_IN_TEACHING_WEEK";
                    errorMessage = "预约时间必须在教学周内";
                    suggestion = "请选择教学周内的时间";
                }
            }
            
            // 5. 验证时间冲突
            if (errorCode == null) {
                List<RoomBooking> conflicts = roomBookingRepository.findConflictingBookings(
                    request.getRoomId(), startTime, endTime, customerId, null
                );
                if (!conflicts.isEmpty()) {
                    details.setPassConflictCheck(false);
                    errorCode = "TIME_CONFLICT";
                    errorMessage = "该时间段已被预约";
                    suggestion = "请选择其他时间段";
                }
            }
            
            // 计算预约信息
            BookingTimeValidationResponse.BookingInfo bookingInfo = calculateBookingInfo(startTime, endTime, customerId);
            
            return BookingTimeValidationResponse.builder()
                .isValid(errorCode == null)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .validationDetails(details)
                .bookingInfo(bookingInfo)
                .suggestion(suggestion)
                .build();
            
        } catch (Exception e) {
            log.error("验证预约时间失败", e);
            return BookingTimeValidationResponse.builder()
                .isValid(false)
                .errorCode("SYSTEM_ERROR")
                .errorMessage("系统错误: " + e.getMessage())
                .build();
        }
    }
    
    /**
     * 获取用户权限配置
     */
    private BookingPersonnelPermission getPermission(String userId, String roomId) {
        try {
            return bookingPersonnelPermissionService.getUserPermission(userId, roomId);
        } catch (Exception e) {
            log.warn("获取用户权限失败，使用默认值", e);
            return null;
        }
    }
    
    /**
     * 获取当前教学周
     */
    private SchoolCalendar2 getCurrentWeek(String customerId, LocalDate date) {
        Date targetDate = java.sql.Date.valueOf(date);
        return schoolCalendar2Repository
            .findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(
                targetDate, targetDate, customerId
            ).orElse(null);
    }
    
    /**
     * 获取学期所有教学周
     */
    private List<SchoolCalendar2> getSemesterWeeks(String customerId) {
        // 获取当前日期
        Date now = new Date();
        
        // 先尝试获取当前教学周
        Optional<SchoolCalendar2> currentWeekOpt = schoolCalendar2Repository
            .findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(now, now, customerId);
        
        if (currentWeekOpt.isPresent()) {
            SchoolCalendar2 currentWeek = currentWeekOpt.get();
            // 获取同一学年学期的所有周，按周次升序排列
            return schoolCalendar2Repository.findByCustomerIdAndXnAndXqOrderByWeek(
                customerId, currentWeek.getXn(), currentWeek.getXq()
            );
        }
        
        // 如果不在教学周内，获取最近学年的所有教学周
        // 先获取所有教学周，按开始日期降序
        List<SchoolCalendar2> allWeeks = schoolCalendar2Repository.findByCustomerIdOrderByStartDateDesc(customerId);
        if (!allWeeks.isEmpty()) {
            // 获取最近的一个教学周（第一个元素）
            SchoolCalendar2 latestWeek = allWeeks.get(0);
            // 重新查询该学年学期的所有周，按周次升序排列
            return schoolCalendar2Repository.findByCustomerIdAndXnAndXqOrderByWeek(
                customerId, latestWeek.getXn(), latestWeek.getXq()
            );
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 判断日期是否在教学周内
     */
    private boolean isInTeachingWeek(LocalDate date, String customerId) {
        Date targetDate = java.sql.Date.valueOf(date);
        return schoolCalendar2Repository
            .findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(
                targetDate, targetDate, customerId
            ).isPresent();
    }
    
    /**
     * 构建当前教学周信息
     */
    private BookingTimeConfigDTO.CurrentWeekInfo buildCurrentWeekInfo(SchoolCalendar2 week, LocalDateTime now) {
        int dayOfWeek = now.getDayOfWeek().getValue();
        return BookingTimeConfigDTO.CurrentWeekInfo.builder()
            .academicYear(week.getXn())
            .semester(week.getXq())
            .semesterName(week.getXq() == 1 ? "上学期" : "下学期")
            .weekNumber(week.getWeek())
            .weekName("第" + week.getWeek() + "周")
            .weekStartDate(week.getStartDateStr())
            .weekEndDate(week.getEndDateStr())
            .currentDayOfWeek(dayOfWeek)
            .currentDayName(DAY_NAMES[dayOfWeek])
            .build();
    }
    
    /**
     * 获取可预约日期列表
     */
    private List<BookingTimeConfigDTO.BookableDate> getAvailableDates(
            LocalDate startDate, LocalDate endDate, List<SchoolCalendar2> semesterWeeks, LocalDateTime now) {
        
        List<BookingTimeConfigDTO.BookableDate> dates = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            // 检查是否在教学周内
            final LocalDate checkDate = current;
            Optional<SchoolCalendar2> weekOpt = semesterWeeks.stream()
                .filter(w -> {
                    LocalDate weekStart = convertToLocalDate(w.getStartDate());
                    LocalDate weekEnd = convertToLocalDate(w.getEndDate());
                    return !checkDate.isBefore(weekStart) && !checkDate.isAfter(weekEnd);
                })
                .findFirst();
            
            if (weekOpt.isPresent()) {
                SchoolCalendar2 week = weekOpt.get();
                int dayOfWeek = current.getDayOfWeek().getValue();
                
                BookingTimeConfigDTO.BookableDate bookableDate = BookingTimeConfigDTO.BookableDate.builder()
                    .date(current.format(DATE_FORMATTER))
                    .dayOfWeek(dayOfWeek)
                    .dayName(DAY_NAMES[dayOfWeek])
                    .weekNumber(week.getWeek())
                    .isToday(current.equals(now.toLocalDate()))
                    .build();
                
                // 如果是今天，设置最早可预约时间
                if (bookableDate.getIsToday()) {
                    bookableDate.setMinTime(now.format(TIME_FORMATTER));
                } else {
                    bookableDate.setMinTime("00:00:00");
                }
                
                dates.add(bookableDate);
            }
            
            current = current.plusDays(1);
        }
        
        return dates;
    }
    
    /**
     * 构建教学周列表
     */
    private List<BookingTimeConfigDTO.TeachingWeek> buildTeachingWeeks(
            List<SchoolCalendar2> semesterWeeks, SchoolCalendar2 currentWeek, 
            LocalDate today, LocalDate maxBookingDate) {
        
        List<BookingTimeConfigDTO.TeachingWeek> weeks = new ArrayList<>();
        
        for (SchoolCalendar2 week : semesterWeeks) {
            LocalDate weekStart = convertToLocalDate(week.getStartDate());
            LocalDate weekEnd = convertToLocalDate(week.getEndDate());
            
            // 计算本周可预约天数
            int bookableDays = 0;
            LocalDate countDate = weekStart.isAfter(today) ? weekStart : today;
            while (!countDate.isAfter(weekEnd) && !countDate.isAfter(maxBookingDate)) {
                bookableDays++;
                countDate = countDate.plusDays(1);
            }
            
            BookingTimeConfigDTO.TeachingWeek teachingWeek = BookingTimeConfigDTO.TeachingWeek.builder()
                .weekNumber(week.getWeek())
                .weekName("第" + week.getWeek() + "周")
                .startDate(week.getStartDateStr())
                .endDate(week.getEndDateStr())
                .isCurrentWeek(currentWeek != null && week.getWeek().equals(currentWeek.getWeek()))
                .isBookable(bookableDays > 0)
                .bookableDays(bookableDays)
                .build();
            
            weeks.add(teachingWeek);
        }
        
        return weeks;
    }
    
    /**
     * 构建学期信息
     */
    private BookingTimeConfigDTO.SemesterInfo buildSemesterInfo(
            List<SchoolCalendar2> semesterWeeks, SchoolCalendar2 currentWeek) {
        
        if (semesterWeeks.isEmpty()) {
            return null;
        }
        
        // 找到真正的第一周和最后一周（根据周数而不是列表位置）
        SchoolCalendar2 firstWeek = semesterWeeks.stream()
            .min(Comparator.comparing(SchoolCalendar2::getWeek))
            .orElse(semesterWeeks.get(0));
        SchoolCalendar2 lastWeek = semesterWeeks.stream()
            .max(Comparator.comparing(SchoolCalendar2::getWeek))
            .orElse(semesterWeeks.get(semesterWeeks.size() - 1));
        
        return BookingTimeConfigDTO.SemesterInfo.builder()
            .semesterName(firstWeek.getXn() + "学年" + (firstWeek.getXq() == 1 ? "上学期" : "下学期"))
            .totalWeeks(semesterWeeks.size())
            .currentWeekIndex(currentWeek != null ? currentWeek.getWeek() : null)
            .semesterStartDate(firstWeek.getStartDateStr())
            .semesterEndDate(lastWeek.getEndDateStr())
            .build();
    }
    
    /**
     * 计算预约信息
     */
    private BookingTimeValidationResponse.BookingInfo calculateBookingInfo(
            LocalDateTime startTime, LocalDateTime endTime, String customerId) {
        
        // 计算持续天数
        long duration = ChronoUnit.DAYS.between(startTime.toLocalDate(), endTime.toLocalDate()) + 1;
        
        // 计算总时长（小时）
        double totalHours = ChronoUnit.MINUTES.between(startTime, endTime) / 60.0;
        
        // 获取涉及的教学周
        Set<Integer> weekNumbers = new HashSet<>();
        LocalDate current = startTime.toLocalDate();
        while (!current.isAfter(endTime.toLocalDate())) {
            SchoolCalendar2 week = getCurrentWeek(customerId, current);
            if (week != null) {
                weekNumbers.add(week.getWeek());
            }
            current = current.plusDays(1);
        }
        
        return BookingTimeValidationResponse.BookingInfo.builder()
            .duration(duration)
            .crossWeeks(weekNumbers.size() > 1)
            .weekNumbers(new ArrayList<>(weekNumbers))
            .totalHours(totalHours)
            .build();
    }
    
    /**
     * 转换Date到LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}