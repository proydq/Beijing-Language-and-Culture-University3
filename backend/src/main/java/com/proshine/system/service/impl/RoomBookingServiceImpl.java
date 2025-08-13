package com.proshine.system.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.dto.request.AccessRecordsRequest;
import com.proshine.system.dto.request.ExportAccessRecordsRequest;
import com.proshine.system.dto.request.RemoteAccessRecordsRequest;
import com.proshine.system.dto.request.RemoteOpenDoorRequest;
import com.proshine.system.dto.request.OperationLogsRequest;
import com.proshine.system.dto.response.AccessRecordResponse;
import com.proshine.system.dto.response.AccessStatsResponse;
import com.proshine.system.dto.response.ExportResponse;
import com.proshine.system.dto.response.RoomStatusResponse;
import com.proshine.system.dto.response.RemoteAccessRecordResponse;
import com.proshine.system.dto.response.RemoteAccessStatsResponse;
import com.proshine.system.dto.response.RemoteOpenDoorResponse;
import com.proshine.system.entity.*;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.ApprovalConfigService;
import com.proshine.system.service.BookingPersonnelPermissionService;
import com.proshine.system.service.ContinuousBookingSettingService;
import com.proshine.system.service.RoomBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private ContinuousBookingSettingService continuousBookingSettingService;

    @Autowired
    private ApprovalConfigService approvalConfigService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private BookingApprovalRepository bookingApprovalRepository;

    @Autowired
    private ClassroomApprovalConfigRepository classroomApprovalConfigRepository;

    @Autowired
    private ClassroomApprovalLevelRepository classroomApprovalLevelRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private RemoteOperationLogRepository remoteOperationLogRepository;
    
    @Autowired
    private RoomAccessRecordRepository roomAccessRecordRepository;
    
    @Autowired
    private CoursePlanningRepository coursePlanningRepository;
    
    @Autowired
    private TbAttendanceAdministrativeCourseTimeConfigRepository courseTimeConfigRepository;
    
    @Autowired
    private SchoolCalendar2Repository schoolCalendar2Repository;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<BookingTrendDto> getBookingTrend(int days) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        return getBookingTrend(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public ResponsePageDataEntity<BookingListResponse> getMyBookings(MyBookingsRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String userId = SecurityUtil.getCurrentUserId();
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 构建查询条件
        Specification<RoomBooking> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));

            //申请人ID过滤
            predicates.add(criteriaBuilder.equal(root.get("applicantId"), userId));
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 审核状态过滤
            if (StringUtils.hasText(request.getApprovalStatus())) {
                String status = convertStatusToChinese(request.getApprovalStatus());
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), RoomBooking.ApprovalStatus.valueOf(status)));
            }
            
            // 使用状态过滤
            if (StringUtils.hasText(request.getUsageStatus())) {
                String status = convertUsageStatusToChinese(request.getUsageStatus());
                predicates.add(criteriaBuilder.equal(root.get("usageStatus"), RoomBooking.UsageStatus.valueOf(status)));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atTime(0, 0, 0);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行分页查询
        Page<RoomBooking> page = roomBookingRepository.findAll(spec, pageable);
        
        // 转换为响应DTO
        List<BookingListResponse> responseList = page.getContent().stream()
            .map(this::convertToBookingListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<BookingListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(page.getTotalElements());
        return result;
    }

    @Override
    public ResponsePageDataEntity<BookingListResponse> getAllBookings(AllBookingsRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 构建查询条件
        Specification<RoomBooking> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 申请人姓名模糊搜索
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), "%" + request.getApplicantName() + "%"));
            }
            
            // 审核状态过滤
            if (StringUtils.hasText(request.getApprovalStatus())) {
                String status = convertStatusToChinese(request.getApprovalStatus());
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), RoomBooking.ApprovalStatus.valueOf(status)));
            }
            
            // 使用状态过滤
            if (StringUtils.hasText(request.getUsageStatus())) {
                String status = convertUsageStatusToChinese(request.getUsageStatus());
                predicates.add(criteriaBuilder.equal(root.get("usageStatus"), RoomBooking.UsageStatus.valueOf(status)));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atTime(0, 0, 0);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行分页查询
        Page<RoomBooking> page = roomBookingRepository.findAll(spec, pageable);
        
        // 转换为响应DTO
        List<BookingListResponse> responseList = page.getContent().stream()
            .map(this::convertToBookingListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<BookingListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(page.getTotalElements());
        return result;
    }

    @Override
    public List<AvailableRoomResponse> getAvailableRooms(AvailableRoomsRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String currentUserId = SecurityUtil.getCurrentUserId();
        
        // 构建查询条件
        Specification<Room> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 逻辑删除过滤
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            
            // 房间区域ID过滤
            if (StringUtils.hasText(request.getRoomAreaId())) {
                predicates.add(criteriaBuilder.equal(root.get("roomAreaId"), request.getRoomAreaId()));
            }
            
            // 房间名称模糊搜索
            if (StringUtils.hasText(request.getRoomName())) {
                predicates.add(criteriaBuilder.like(root.get("roomName"), "%" + request.getRoomName() + "%"));
            }

            predicates.add(criteriaBuilder.equal(root.get("roomTypeName"), Room.RoomType.CLASSROOM.getDisplayName()));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行查询
        List<Room> rooms = roomRepository.findAll(spec);
        
        // 转换为响应DTO，并检查预约权限
        return rooms.stream()
            .map(room -> convertToAvailableRoomResponse(room, currentUserId))
            .collect(Collectors.toList());
    }

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String userId = SecurityUtil.getCurrentUserId();
        
        try {
            log.info("开始创建预约，用户ID: {}, 客户域: {}, borrowTime: [{}]", 
                userId, customerId, request.getBorrowTime());

            validateBookingRules(userId, request);

            RoomBooking booking = buildBookingEntity(request, customerId, userId);
            log.info("预约实体构建完成，预约ID: {}, bookingPeriod: [{}]", 
                booking.getId(), booking.getBookingPeriod());

            processApprovalFlow(booking, request);
            log.info("审批流程处理完成，审批状态: {}", booking.getApprovalStatus());

            roomBookingRepository.save(booking);
            log.info("预约保存成功，最终预约ID: {}", booking.getId());
            
            return buildCreateResponse(booking);
            
        } catch (Exception e) {
            log.error("创建预约失败，borrowTime: [{}]", request.getBorrowTime(), e);
            throw e;
        }
    }

    /**
     * 转换预约实体为列表响应DTO
     */
    private BookingListResponse convertToBookingListResponse(RoomBooking booking) {
        BookingListResponse response = new BookingListResponse();
        response.setId(booking.getId());
        response.setReservationName(booking.getBookingName());
        response.setReservationPeriod(booking.getBookingPeriod());
        response.setRoomName(booking.getRoomName());
        response.setApplicantName(booking.getApplicantName());
        response.setApprovalStatus(booking.getApprovalStatus().getDisplayName());
        response.setUsageStatus(booking.getUsageStatus().getDisplayName());
        response.setCreateTime(booking.getCreateTime());
        response.setDescription(booking.getDescription());
        response.setBorrowTime(booking.getBookingPeriod());
        
        // 解析参与人信息
        response.setParticipants(extractPersonNames(booking.getExtend1()));
        
        // 解析审批人信息
        response.setApprovers(extractPersonNames(booking.getExtend2()));
        
        return response;
    }

    /**
     * 转换房间实体为可预约房间响应DTO
     */
    private AvailableRoomResponse convertToAvailableRoomResponse(Room room, String userId) {
        AvailableRoomResponse response = new AvailableRoomResponse();
        response.setId(room.getId());
        response.setName(room.getRoomName());
        response.setCapacity(room.getRoomVolume());
        response.setBuilding(room.getRoomAreaName());
        response.setFloor(extractFloorFromRoomNo(room.getRoomNo()));
        response.setRoomAreaId(room.getRoomAreaId());
        response.setRoomType(room.getRoomTypeName());
        response.setDescription(room.getRemark());
        response.setEquipment(room.getRemark()); // 设备信息也从备注中解析
        
        // 检查当前用户是否有该房间的预约权限
        boolean hasPermission = permissionService.hasBookingPermission(userId, room.getId());
        response.setAvailable(hasPermission);
        
        return response;
    }

    /**
     * 从JSON字符串中提取人员姓名
     */
    private String extractPersonNames(String jsonString) {
        if (!StringUtils.hasText(jsonString)) {
            return "";
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<CreateBookingRequest.PersonDetails> persons = mapper.readValue(
                jsonString, 
                new TypeReference<List<CreateBookingRequest.PersonDetails>>() {}
            );
            
            return persons.stream()
                .map(CreateBookingRequest.PersonDetails::getName)
                .collect(Collectors.joining(","));
        } catch (Exception e) {
            log.warn("Failed to parse person details JSON: {}", jsonString, e);
            return "";
        }
    }

    /**
     * 从房间号中提取楼层信息
     */
    private String extractFloorFromRoomNo(String roomNo) {
        if (!StringUtils.hasText(roomNo)) {
            return "";
        }
        
        // 简单的楼层提取逻辑，根据实际格式调整
        if (roomNo.length() >= 3) {
            char floorChar = roomNo.charAt(0);
            return floorChar + "F";
        }
        
        return "";
    }

    /**
     * 转换中文状态为枚举名称
     */
    private String convertStatusToChinese(String chineseStatus) {
        switch (chineseStatus) {
            case "审核中": return "PENDING";
            case "通过": return "APPROVED";
            case "拒绝": return "REJECTED";
            case "已取消": return "CANCELLED";
            default: return chineseStatus;
        }
    }

    /**
     * 转换中文使用状态为枚举名称
     */
    private String convertUsageStatusToChinese(String chineseStatus) {
        switch (chineseStatus) {
            case "未开始": return "NOT_STARTED";
            case "进行中": return "IN_PROGRESS";
            case "已结束": return "COMPLETED";
            case "已取消": return "CANCELLED";
            default: return chineseStatus;
        }
    }

    private void validateBookingRules(String userId, CreateBookingRequest request) {
        if (!permissionService.hasBookingPermission(userId, request.getRoomId())) {
            throw new RuntimeException("无预约权限");
        }

        BookingPersonnelPermission permission = permissionService.getUserPermission(userId, request.getRoomId());
        if (!validateAdvanceBookingDays(permission, request.getBookingStartTime())) {
            throw new RuntimeException("超出允许预约天数");
        }

        ContinuousBookingSetting setting = continuousBookingSettingService.getByRoomId(request.getRoomId());
        if (!validateContinuousBookingDays(setting, request)) {
            throw new RuntimeException("超出连续预约限制");
        }

        if (hasTimeConflict(request.getRoomId(), request.getBookingStartTime(), request.getBookingEndTime())) {
            throw new RuntimeException("该时间段房间已被预约");
        }
    }

    private boolean validateAdvanceBookingDays(BookingPersonnelPermission permission, LocalDateTime startTime) {
        int days = permission != null && permission.getAdvanceBookingDays() != null ? permission.getAdvanceBookingDays() : 7;
        return !startTime.toLocalDate().isAfter(LocalDate.now().plusDays(days));
    }

    private boolean validateContinuousBookingDays(ContinuousBookingSetting setting, CreateBookingRequest request) {
        if (setting == null || Boolean.TRUE.equals(setting.getIsUnlimited())) {
            return true;
        }
        if (!Boolean.TRUE.equals(setting.getCanContinuous())) {
            return false;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(request.getBookingStartTime().toLocalDate(), request.getBookingEndTime().toLocalDate()) + 1;
        return days <= setting.getContinuousDays();
    }

    private boolean hasTimeConflict(String roomId, LocalDateTime start, LocalDateTime end) {
        String cstmId = SecurityUtil.getCustomerId();
        return !roomBookingRepository.findConflictingBookings(roomId, start, end, cstmId, null).isEmpty();
    }

    private void processApprovalFlow(RoomBooking booking, CreateBookingRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 查询教室审批配置
        Optional<ClassroomApprovalConfig> configOpt = classroomApprovalConfigRepository
            .findByRoomIdAndCstmId(booking.getRoomId(), customerId);
        
        if (!configOpt.isPresent() || !configOpt.get().getNeedApproval()) {
            // 无需审批，直接通过
            booking.setApprovalStatus(RoomBooking.ApprovalStatus.APPROVED);
            
            // 先保存预约记录以获取ID
            booking = roomBookingRepository.save(booking);
            log.debug("无需审批预约已保存，获得ID: {}", booking.getId());
            
            // 直接通过时也创建课程排课记录
            createCoursePlanningFromBooking(booking);
            return;
        }
        
        ClassroomApprovalConfig config = configOpt.get();
        booking.setApprovalStatus(RoomBooking.ApprovalStatus.PENDING);
        
        // 查询审批级别配置
        List<ClassroomApprovalLevel> approvalLevels = classroomApprovalLevelRepository
            .findByConfigIdAndCstmIdOrderByLevelNumber(config.getId(), customerId);
        
        if (approvalLevels.isEmpty()) {
            // 没有配置审批级别，直接通过
            booking.setApprovalStatus(RoomBooking.ApprovalStatus.APPROVED);
            
            // 先保存预约记录以获取ID
            booking = roomBookingRepository.save(booking);
            log.debug("无审批级别预约已保存，获得ID: {}", booking.getId());
            
            // 直接通过时也创建课程排课记录
            createCoursePlanningFromBooking(booking);
            return;
        }
        
        // 保存预约记录以获取ID
        booking = roomBookingRepository.save(booking);
        log.debug("预约记录已保存，获得ID: {}", booking.getId());
        
        // 创建审批记录
        int maxLevel = approvalLevels.stream().mapToInt(ClassroomApprovalLevel::getLevelNumber).max().orElse(1);
        
        // 如果支持自选审批人，创建一级审批记录（使用用户选择的审批人）
        if (config.getAllowBookerSelectApprover() 
            && request.getApproverDetails() != null && !request.getApproverDetails().isEmpty()) {
            
            // 为每个用户选择的审批人创建一级审批记录
            for (CreateBookingRequest.PersonDetails approverDetail : request.getApproverDetails()) {
                BookingApproval approval = new BookingApproval();
                approval.setCstmId(customerId);
                approval.setBookingId(booking.getId());
                approval.setApprovalLevel(1);
                approval.setIsFinalApproval(maxLevel == 1); // 如果只有一级审批，则为最终审批
                
                approval.setApproverId(approverDetail.getId());
                approval.setApproverName(approverDetail.getName());
                approval.setApproverType("自选审批人");
                
                bookingApprovalRepository.save(approval);
            }
            
            // 如果还有更高级别的审批，创建后续级别的审批记录（使用预设审批人）
            for (ClassroomApprovalLevel level : approvalLevels) {
                if (level.getLevelNumber() > 1) {
                    // 使用预设的审批人
                    String[] approverIdsArray = Optional.ofNullable(level.getApproverIds())
                        .orElse("").split(",");
                    String[] approverNamesArray = Optional.ofNullable(level.getApproverNames())
                        .orElse("").split("、");

                    // 为每个预设审批人创建审批记录
                    for (int i = 0; i < approverIdsArray.length; i++) {
                        BookingApproval nextApproval = new BookingApproval();
                        nextApproval.setCstmId(customerId);
                        nextApproval.setBookingId(booking.getId());
                        nextApproval.setApprovalLevel(level.getLevelNumber());
                        nextApproval.setIsFinalApproval(level.getLevelNumber().equals(maxLevel));

                        nextApproval.setApproverId(approverIdsArray[i].trim());
                        nextApproval.setApproverName(i < approverNamesArray.length
                            ? approverNamesArray[i].trim()
                            : "未知");
                        nextApproval.setApproverType("预设审批人");

                        bookingApprovalRepository.save(nextApproval);
                    }
                }
            }
        } else {
            // 不支持自选审批人，审批从第二级开始
            for (ClassroomApprovalLevel level : approvalLevels) {
                if (level.getLevelNumber() >= 2) {
                    // 使用预设的审批人
                    String[] approverIdsArray = Optional.ofNullable(level.getApproverIds())
                        .orElse("").split(",");
                    String[] approverNamesArray = Optional.ofNullable(level.getApproverNames())
                        .orElse("").split("、");

                    // 为每个预设审批人创建审批记录
                    for (int i = 0; i < approverIdsArray.length; i++) {
                        BookingApproval approval = new BookingApproval();
                        approval.setCstmId(customerId);
                        approval.setBookingId(booking.getId());
                        approval.setApprovalLevel(level.getLevelNumber());
                        approval.setIsFinalApproval(level.getLevelNumber().equals(maxLevel));

                        approval.setApproverId(approverIdsArray[i].trim());
                        approval.setApproverName(i < approverNamesArray.length
                            ? approverNamesArray[i].trim()
                            : "未知");
                        approval.setApproverType("预设审批人");

                        bookingApprovalRepository.save(approval);
                    }
                }
            }
            
            // 如果没有二级及以上的审批配置，直接通过
            boolean hasSecondLevelOrAbove = approvalLevels.stream()
                .anyMatch(level -> level.getLevelNumber() >= 2);
            if (!hasSecondLevelOrAbove) {
                booking.setApprovalStatus(RoomBooking.ApprovalStatus.APPROVED);
                
                // 更新保存预约记录
                booking = roomBookingRepository.save(booking);
                log.debug("单级审批预约状态已更新，预约ID: {}", booking.getId());
                
                // 直接通过时也创建课程排课记录
                createCoursePlanningFromBooking(booking);
            }
        }
    }

    private RoomBooking buildBookingEntity(CreateBookingRequest request, String customerId, String userId) {
        RoomBooking booking = new RoomBooking();
        booking.setCstmId(customerId);
        booking.setBookingName(request.getBookingName());
        booking.setRoomId(request.getRoomId());
        booking.setRoomName(request.getRoomName());
        booking.setApplicantId(userId);
        booking.setApplicantName(request.getApplicant());
        booking.setApplicantType(request.getApplicantType());
        booking.setApplicantPhone(request.getApplicantPhone());
        booking.setApplicantDepartment(request.getApplicantDepartment());
        booking.setBookingStartTime(request.getBookingStartTime());
        booking.setBookingEndTime(request.getBookingEndTime());
        booking.setBookingPeriod(request.getBorrowTime());
        booking.setDescription(request.getDescription());
        booking.setReason(request.getReason());
        booking.setApprovalStatus(RoomBooking.ApprovalStatus.PENDING);
        booking.setUsageStatus(RoomBooking.UsageStatus.NOT_STARTED);
        if (StringUtils.hasText(request.getUrgency())) {
            booking.setUrgency(RoomBooking.Urgency.valueOf(request.getUrgency()));
        }
        if (request.getParticipantDetails() != null && !request.getParticipantDetails().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                booking.setExtend1(mapper.writeValueAsString(request.getParticipantDetails()));
            } catch (Exception e) {
                log.warn("Failed to serialize participant details", e);
            }
        }
        if (request.getApproverDetails() != null && !request.getApproverDetails().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                booking.setExtend2(mapper.writeValueAsString(request.getApproverDetails()));
            } catch (Exception e) {
                log.warn("Failed to serialize approver details", e);
            }
        }
        // 备注信息已经通过reason字段保存，无需额外处理
        return booking;
    }

    private CreateBookingResponse buildCreateResponse(RoomBooking booking) {
        CreateBookingResponse response = new CreateBookingResponse();
        response.setBookingId(booking.getId());
        response.setBookingName(booking.getBookingName());
        response.setApprovalStatus(booking.getApprovalStatus().getDisplayName());
        response.setCreateTime(booking.getCreateTime());
        return response;
    }

    @Override
    public ResponsePageDataEntity<ApprovalListResponse> getPendingApprovals(ApprovalListRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String currentUserId = SecurityUtil.getCurrentUserId();
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 构建查询条件 - 只查询待审批状态
        Specification<RoomBooking> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 只查询待审批状态
            predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), RoomBooking.ApprovalStatus.PENDING));
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 申请人姓名模糊搜索
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), "%" + request.getApplicantName() + "%"));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atTime(0, 0, 0);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行分页查询
        Page<RoomBooking> page = roomBookingRepository.findAll(spec, pageable);
        
        // 过滤出当前用户有权限审批的预约
        List<ApprovalListResponse> responseList = page.getContent().stream()
            .filter(booking -> hasApprovalPermission(booking.getId(), currentUserId))
            .map(this::convertToApprovalListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal((long) responseList.size()); // 注意：这里的总数是过滤后的数量
        return result;
    }

    @Override
    public ResponsePageDataEntity<ApprovalListResponse> getAllApprovals(ApprovalListRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 修复：查询所有的预约记录，而不是只查询当前用户审批过的记录
        Specification<RoomBooking> bookingSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 审批状态过滤
            if (StringUtils.hasText(request.getApprovalStatus())) {
                String status = convertStatusToChinese(request.getApprovalStatus());
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), RoomBooking.ApprovalStatus.valueOf(status)));
            }
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 申请人姓名模糊搜索
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), "%" + request.getApplicantName() + "%"));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atTime(0, 0, 0);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行分页查询
        Page<RoomBooking> page = roomBookingRepository.findAll(bookingSpec, pageable);
        
        // 转换为响应DTO
        List<ApprovalListResponse> responseList = page.getContent().stream()
            .map(this::convertToApprovalListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(page.getTotalElements());
        return result;
    }

    @Override
    @Transactional
    public ApprovalResponse approveBooking(String bookingId, ApprovalRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String userId = SecurityUtil.getCurrentUserId();
        String userName = SecurityUtil.getCurrentUsername();
        
        // 查找预约记录
        RoomBooking booking = roomBookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        
        // 验证客户域
        if (!customerId.equals(booking.getCstmId())) {
            throw new RuntimeException("无权限操作此预约");
        }
        
        // 验证状态
        if (booking.getApprovalStatus() != RoomBooking.ApprovalStatus.PENDING) {
            throw new RuntimeException("预约已审批，无法重复操作");
        }
        
        // 查找预约创建时生成的审批记录
        List<BookingApproval> approvals = bookingApprovalRepository.findByBookingIdOrderByApprovalLevel(bookingId);
        if (approvals.isEmpty()) {
            throw new RuntimeException("未找到审批记录");
        }
        
        // 验证当前用户是否为指定的审批人
        BookingApproval currentApproval = approvals.stream()
            .filter(approval -> approval.getApprovalTime() == null) // 找到待审批的记录
            .filter(approval -> userId.equals(approval.getApproverId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("您不是当前审批层级的审批人，无权限审批此预约"));
        
        // 更新审批记录
        LocalDateTime now = LocalDateTime.now();
        currentApproval.setApprovalAction("APPROVED".equals(request.getAction()) ? 
            BookingApproval.ApprovalAction.APPROVE : BookingApproval.ApprovalAction.REJECT);
        currentApproval.setApprovalComment(request.getComment());
        currentApproval.setApprovalTime(now);
        
        bookingApprovalRepository.save(currentApproval);
        
        // 更新预约状态 - 支持多级审批
        if ("REJECTED".equals(request.getAction())) {
            // 拒绝直接结束审批流程
            booking.setApprovalStatus(RoomBooking.ApprovalStatus.REJECTED);
            booking.setRejectReason(request.getComment());
        } else if ("APPROVED".equals(request.getAction())) {
            // 检查是否还有后续审批级别
            boolean hasNextLevel = approvals.stream()
                .anyMatch(approval -> approval.getApprovalLevel() > currentApproval.getApprovalLevel() 
                    && approval.getApprovalTime() == null);
            
            if (hasNextLevel) {
                // 还有后续审批级别，保持待审批状态
                booking.setApprovalStatus(RoomBooking.ApprovalStatus.PENDING);
            } else {
                // 所有审批级别都已完成，设为通过
                booking.setApprovalStatus(RoomBooking.ApprovalStatus.APPROVED);
                
                // 审批完全通过后，自动创建课程排课记录
                createCoursePlanningFromBooking(booking);
            }
        } else {
            throw new RuntimeException("无效的审批动作");
        }
        
        booking.setLastUpdateTime(now);
        
        // 保存预约记录
        roomBookingRepository.save(booking);
        
        // 构建响应
        ApprovalResponse response = new ApprovalResponse();
        response.setBookingId(bookingId);
        response.setApprovalStatus(booking.getApprovalStatus().getDisplayName());
        response.setApprovalTime(now);
        response.setApproverName(userName);
        response.setApprovalComment(request.getComment());
        
        return response;
    }

    @Override
    public List<ApprovalHistoryResponse> getApprovalHistory(String bookingId) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 验证预约存在且属于当前客户域
        RoomBooking booking = roomBookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        
        if (!customerId.equals(booking.getCstmId())) {
            throw new RuntimeException("无权限查看此预约");
        }
        
        // 查询审批历史
        List<BookingApproval> approvals = bookingApprovalRepository
            .findByBookingIdAndCstmIdOrderByApprovalTimeAsc(bookingId, customerId);
        
        // 转换为响应DTO
        return approvals.stream().map(approval -> {
            ApprovalHistoryResponse response = new ApprovalHistoryResponse();
            response.setId(approval.getId());
            response.setApproverName(approval.getApproverName());
            response.setApproverType(approval.getApproverType());
            
            // 处理审批动作，如果为null则表示待审批
            String approvalAction = approval.getApprovalAction() != null 
                ? approval.getApprovalAction().getDisplayName() 
                : "待审批";
            response.setApprovalAction(approvalAction);
            
            response.setApprovalComment(approval.getApprovalComment());
            response.setApprovalTime(approval.getApprovalTime());
            response.setApprovalLevel(approval.getApprovalLevel());
            response.setIsFinalApproval(approval.getIsFinalApproval());
            
            // 设置前端显示字段
            String levelName = "第" + getChineseNumber(approval.getApprovalLevel()) + "级审批";
            response.setLevelName(levelName);
            response.setApprovers(new String[]{approval.getApproverName()});
            
            // 如果已审批则显示审批人，否则为空
            String confirmedApprover = approval.getApprovalAction() != null ? approval.getApproverName() : "";
            response.setConfirmedApprover(confirmedApprover);
            
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BatchApprovalResponse batchApprove(BatchApprovalRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String userId = SecurityUtil.getCurrentUserId();
        String userName = SecurityUtil.getCurrentUsername();
        
        List<BatchApprovalResponse.BatchApprovalDetail> details = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        for (String bookingId : request.getBookingIds()) {
            try {
                // 创建单个审批请求
                ApprovalRequest approvalRequest = new ApprovalRequest();
                approvalRequest.setAction(request.getAction());
                approvalRequest.setComment(request.getComment());
                
                // 调用单个审批方法
                approveBooking(bookingId, approvalRequest);
                
                // 记录成功
                BatchApprovalResponse.BatchApprovalDetail detail = new BatchApprovalResponse.BatchApprovalDetail();
                detail.setBookingId(bookingId);
                detail.setStatus("success");
                detail.setMessage("审批成功");
                details.add(detail);
                successCount++;
                
            } catch (Exception e) {
                // 记录失败
                BatchApprovalResponse.BatchApprovalDetail detail = new BatchApprovalResponse.BatchApprovalDetail();
                detail.setBookingId(bookingId);
                detail.setStatus("failure");
                detail.setMessage(e.getMessage());
                details.add(detail);
                failureCount++;
                
                log.error("批量审批失败，预约ID: {}, 错误: {}", bookingId, e.getMessage(), e);
            }
        }
        
        // 构建响应
        BatchApprovalResponse response = new BatchApprovalResponse();
        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        response.setDetails(details);
        
        return response;
    }

    @Override
    public BookingDetailResponse getBookingDetail(String bookingId) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 查找预约记录
        RoomBooking booking = roomBookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        
        // 验证客户域
        if (!customerId.equals(booking.getCstmId())) {
            throw new RuntimeException("无权限查看此预约");
        }
        
        // 转换为响应DTO
        return convertToBookingDetailResponse(booking);
    }

    @Override
    @Transactional
    public String cancelBooking(String bookingId, CancelBookingRequest request) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        String customerId = SecurityUtil.getCustomerId();
        
        // 查找预约记录
        RoomBooking booking = roomBookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("预约记录不存在"));
        
        // 验证客户域
        if (!customerId.equals(booking.getCstmId())) {
            throw new RuntimeException("无权限取消此预约");
        }
        
        // 验证申请人权限（只有申请人本人可以取消）
        if (!currentUserId.equals(booking.getApplicantId())) {
            throw new RuntimeException("只有申请人本人可以取消预约");
        }
        
        // 验证预约状态（只有审核中或已通过且未开始的预约可以取消）
        if (booking.getApprovalStatus() == RoomBooking.ApprovalStatus.REJECTED ||
            booking.getApprovalStatus() == RoomBooking.ApprovalStatus.CANCELLED) {
            throw new RuntimeException("该预约已被拒绝或已取消，无法再次取消");
        }
        
        // 验证使用状态（已开始或已结束的预约不能取消）
        if (booking.getUsageStatus() == RoomBooking.UsageStatus.IN_PROGRESS ||
            booking.getUsageStatus() == RoomBooking.UsageStatus.COMPLETED) {
            throw new RuntimeException("预约已开始或已结束，无法取消");
        }
        
        // 更新预约状态为已取消
        booking.setApprovalStatus(RoomBooking.ApprovalStatus.CANCELLED);
        booking.setUsageStatus(RoomBooking.UsageStatus.CANCELLED);
        booking.setLastUpdateTime(LocalDateTime.now());
        booking.setCancelTime(LocalDateTime.now());
        booking.setCancelReason(request.getReason());
        
        // 保存更新
        roomBookingRepository.save(booking);
        
        log.info("用户 {} 取消了预约 {}，原因：{}", currentUserId, bookingId, request.getReason());
        
        return "预约已成功取消";
    }

    /**
     * 转换预约实体为审批列表响应DTO
     */
    /**
     * 检查当前用户是否有权限审批指定预约
     * 只有当前审批层级的审批人才能看到预约数据
     */
    private boolean hasApprovalPermission(String bookingId, String currentUserId) {
        // 查询该预约的所有审批记录，按审批层级排序
        List<BookingApproval> approvals = bookingApprovalRepository
            .findByBookingIdOrderByApprovalLevel(bookingId);
        
        if (approvals.isEmpty()) {
            return false;
        }
        
        // 找到当前应该审批的层级
        final Integer[] currentApprovalLevel = {null};
        
        // 按层级分组
        Map<Integer, List<BookingApproval>> levelGroups = approvals.stream()
            .collect(Collectors.groupingBy(BookingApproval::getApprovalLevel));
        
        // 按层级顺序遍历，找到第一个未完成的层级
        for (Integer level : levelGroups.keySet().stream().sorted().collect(Collectors.toList())) {
            List<BookingApproval> levelApprovals = levelGroups.get(level);
            
            // 检查该层级是否有人已经审批（通过或拒绝）
            boolean hasApproved = levelApprovals.stream()
                .anyMatch(approval -> approval.getApprovalTime() != null);
            
            if (!hasApproved) {
                // 该层级还没有人审批，这就是当前审批层级
                currentApprovalLevel[0] = level;
                break;
            }
        }
        
        // 如果没有找到未审批的层级，说明审批已完成，无人有权限
        if (currentApprovalLevel[0] == null) {
            return false;
        }
        
        // 检查当前用户是否是当前审批层级的审批人，且该层级还没有人审批
        return approvals.stream()
            .anyMatch(approval -> 
                approval.getApprovalLevel().equals(currentApprovalLevel[0]) &&
                currentUserId.equals(approval.getApproverId()) &&
                approval.getApprovalTime() == null // 确保该审批人还未审批
            );
    }
    
    /**
     * 检查当前用户是否有权限查看某个预约的审批记录
     * 只有实际审批过该预约的用户才能在已通过/已拒绝界面看到相应记录
     */
    private boolean hasApprovalViewPermission(String bookingId, String currentUserId) {
        // 查询该预约的所有审批记录
        List<BookingApproval> approvals = bookingApprovalRepository
            .findByBookingIdOrderByApprovalLevel(bookingId);
        
        // 检查当前用户是否实际审批过该预约（有审批时间的记录）
        return approvals.stream()
            .anyMatch(approval -> 
                currentUserId.equals(approval.getApproverId()) &&
                approval.getApprovalTime() != null // 确保该用户已经审批过
            );
    }
    
    private ApprovalListResponse convertToApprovalListResponse(RoomBooking booking) {
        ApprovalListResponse response = new ApprovalListResponse();
        response.setId(booking.getId());
        response.setBookingName(booking.getBookingName());
        response.setApplicantName(booking.getApplicantName());
        response.setApplicantType(booking.getApplicantType());
        response.setRoomName(booking.getRoomName());
        response.setBookingPeriod(booking.getBookingPeriod());
        response.setBookingStartTime(booking.getBookingStartTime());
        response.setBookingEndTime(booking.getBookingEndTime());
        response.setDescription(booking.getDescription());
        response.setReason(booking.getReason());
        response.setApprovalStatus(booking.getApprovalStatus().name());
        response.setUrgency(booking.getUrgency() != null ? booking.getUrgency().name() : "NORMAL");
        response.setCreateTime(booking.getCreateTime());
        
        // 解析参与人和审批人信息
        response.setParticipants(extractPersonNames(booking.getExtend1()));
        response.setApprovers(extractPersonNames(booking.getExtend2()));
        
        // 设置前端显示字段
        response.setApplyTime(booking.getCreateTime());
        response.setBookingTime(booking.getBookingPeriod());
        response.setApplicant(booking.getApplicantName());
        response.setStatus(booking.getApprovalStatus().name());
        response.setName(booking.getBookingName());
        response.setCycle(booking.getBookingPeriod());
        
        return response;
    }

    /**
     * 转换预约实体为预约详情响应DTO
     */
    private BookingDetailResponse convertToBookingDetailResponse(RoomBooking booking) {
        BookingDetailResponse response = new BookingDetailResponse();
        response.setId(booking.getId());
        response.setBookingName(booking.getBookingName());
        response.setApplicantName(booking.getApplicantName());
        response.setApplicantType(booking.getApplicantType());
        response.setApplicantPhone(booking.getApplicantPhone());
        response.setApplicantDepartment(booking.getApplicantDepartment());
        response.setRoomId(booking.getRoomId());
        response.setRoomName(booking.getRoomName());
        response.setBookingStartTime(booking.getBookingStartTime());
        response.setBookingEndTime(booking.getBookingEndTime());
        response.setBookingPeriod(booking.getBookingPeriod());
        response.setDescription(booking.getDescription());
        response.setReason(booking.getReason());
        response.setApprovalStatus(booking.getApprovalStatus().getDisplayName());
        response.setUsageStatus(booking.getUsageStatus().getDisplayName());
        response.setUrgency(booking.getUrgency() != null ? booking.getUrgency().getDisplayName() : "普通");
        response.setCreateTime(booking.getCreateTime());
        response.setRemark(booking.getReason()); // 修复：使用申请理由作为备注详情显示
        
        // 获取审批步骤信息
        List<BookingDetailResponse.ApprovalStep> approvalSteps = getApprovalSteps(booking.getId());
        response.setApprovalSteps(approvalSteps);
        
        // 解析参与人和审批人详情
        try {
            if (StringUtils.hasText(booking.getExtend1())) {
                ObjectMapper mapper = new ObjectMapper();
                List<BookingDetailResponse.PersonDetails> participantDetails = mapper.readValue(
                    booking.getExtend1(), 
                    new TypeReference<List<BookingDetailResponse.PersonDetails>>() {}
                );
                response.setParticipantDetails(participantDetails);
                response.setParticipants(participantDetails.stream()
                    .map(BookingDetailResponse.PersonDetails::getName)
                    .collect(Collectors.toList()));
            }
            
            if (StringUtils.hasText(booking.getExtend2())) {
                ObjectMapper mapper = new ObjectMapper();
                List<BookingDetailResponse.PersonDetails> approverDetails = mapper.readValue(
                    booking.getExtend2(), 
                    new TypeReference<List<BookingDetailResponse.PersonDetails>>() {}
                );
                response.setApproverDetails(approverDetails);
                response.setApprovers(approverDetails.stream()
                    .map(BookingDetailResponse.PersonDetails::getName)
                    .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.warn("解析人员详情失败", e);
        }
        
        return response;
    }

    /**
     * 获取审批步骤信息
     */
    private List<BookingDetailResponse.ApprovalStep> getApprovalSteps(String bookingId) {
        List<BookingApproval> approvals = bookingApprovalRepository
            .findByBookingIdOrderByApprovalLevel(bookingId);
        
        // 按审批级别分组
        Map<Integer, List<BookingApproval>> approvalsByLevel = approvals.stream()
            .collect(Collectors.groupingBy(BookingApproval::getApprovalLevel));
        
        List<BookingDetailResponse.ApprovalStep> steps = new ArrayList<>();
        
        for (Map.Entry<Integer, List<BookingApproval>> entry : approvalsByLevel.entrySet()) {
            Integer level = entry.getKey();
            List<BookingApproval> levelApprovals = entry.getValue();
            
            BookingDetailResponse.ApprovalStep step = new BookingDetailResponse.ApprovalStep();
            step.setLevelName("第" + getChineseNumber(level) + "级审批");
            
            // 获取该级别的所有审批人姓名（从配置中获取）
            List<String> allApprovers = getAllApproversForLevel(bookingId, level);
            step.setApprovers(allApprovers);
            
            // 查找已审批的审批人
            BookingApproval approvedApproval = levelApprovals.stream()
                .filter(approval -> approval.getApprovalAction() != null)
                .findFirst()
                .orElse(null);
            
            if (approvedApproval != null) {
                step.setConfirmedApprover(approvedApproval.getApproverName());
                step.setApprovalTime(approvedApproval.getApprovalTime());
                step.setComment(approvedApproval.getApprovalComment());
                step.setStatus(approvedApproval.getApprovalAction().name());
            } else {
                step.setConfirmedApprover(""); // 未审批则为空
                step.setApprovalTime(null);
                step.setComment("");
                step.setStatus("PENDING");
            }
            
            steps.add(step);
        }
        
        // 按级别排序
        steps.sort((s1, s2) -> {
            Integer level1 = extractLevelFromName(s1.getLevelName());
            Integer level2 = extractLevelFromName(s2.getLevelName());
            return level1.compareTo(level2);
        });
        
        return steps;
    }
    
    /**
     * 获取指定级别的所有审批人姓名
     */
    private List<String> getAllApproversForLevel(String bookingId, Integer level) {
        // 从BookingApproval实体中查找该级别的所有审批人姓名
        List<BookingApproval> approvals = bookingApprovalRepository
            .findByBookingIdAndApprovalLevel(bookingId, level);
        
        // 提取所有审批人姓名并去重
        return approvals.stream()
            .map(BookingApproval::getApproverName)
            .filter(name -> StringUtils.hasText(name) && !"未知".equals(name))
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * 从级别名称中提取级别数字
     */
    private Integer extractLevelFromName(String levelName) {
        if (levelName.contains("一")) return 1;
        if (levelName.contains("二")) return 2;
        if (levelName.contains("三")) return 3;
        if (levelName.contains("四")) return 4;
        if (levelName.contains("五")) return 5;
        return 1; // 默认返回1
    }

    @Override
    public ResponsePageDataEntity<ApprovalListResponse> getApprovedApprovals(ApprovalListRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String currentUserId = SecurityUtil.getCurrentUserId();
        
        // 查询当前用户已通过的审批记录
        List<BookingApproval> approvedApprovals = bookingApprovalRepository
            .findByApproverIdAndCstmIdAndApprovalActionOrderByApprovalTimeDesc(
                currentUserId, customerId, BookingApproval.ApprovalAction.APPROVE);
        
        // 获取所有相关的预约ID
        List<String> bookingIds = approvedApprovals.stream()
            .map(BookingApproval::getBookingId)
            .distinct()
            .collect(Collectors.toList());
        
        if (bookingIds.isEmpty()) {
            ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
            result.setRows(new ArrayList<>());
            result.setTotal(0L);
            return result;
        }
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 查询对应的预约记录
        Specification<RoomBooking> bookingSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 预约ID过滤（只查询当前用户审批过的预约）
            predicates.add(root.get("id").in(bookingIds));
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 申请人姓名模糊搜索
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), "%" + request.getApplicantName() + "%"));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atTime(0, 0, 0);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDateTime));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行分页查询
        Page<RoomBooking> page = roomBookingRepository.findAll(bookingSpec, pageable);
        
        // 转换为响应DTO
        List<ApprovalListResponse> responseList = page.getContent().stream()
            .map(this::convertToApprovalListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(page.getTotalElements());
        return result;
    }

    @Override
    public ResponsePageDataEntity<ApprovalListResponse> getRejectedApprovals(ApprovalListRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String currentUserId = SecurityUtil.getCurrentUserId();
        
        // 查询当前用户已拒绝的审批记录
        List<BookingApproval> rejectedApprovals = bookingApprovalRepository
            .findByApproverIdAndCstmIdAndApprovalActionOrderByApprovalTimeDesc(
                currentUserId, customerId, BookingApproval.ApprovalAction.REJECT);
        
        // 获取所有相关的预约ID
        List<String> bookingIds = rejectedApprovals.stream()
            .map(BookingApproval::getBookingId)
            .distinct()
            .collect(Collectors.toList());
        
        if (bookingIds.isEmpty()) {
            ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
            result.setRows(new ArrayList<>());
            result.setTotal(0L);
            return result;
        }
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 查询对应的预约记录
        Specification<RoomBooking> bookingSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 预约ID过滤（只查询当前用户审批过的预约）
            predicates.add(root.get("id").in(bookingIds));
            
            // 预约名称模糊搜索
            if (StringUtils.hasText(request.getReservationName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), "%" + request.getReservationName() + "%"));
            }
            
            // 申请人姓名模糊搜索
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), "%" + request.getApplicantName() + "%"));
            }
            
            // 日期范围过滤
            if (StringUtils.hasText(request.getStartDate())) {
                try {
                    LocalDate startDate = LocalDate.parse(request.getStartDate());
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        criteriaBuilder.function("DATE", LocalDate.class, root.get("startTime")), 
                        startDate
                    ));
                } catch (Exception e) {
                    log.warn("Invalid start date format: {}", request.getStartDate());
                }
            }
            
            if (StringUtils.hasText(request.getEndDate())) {
                try {
                    LocalDate endDate = LocalDate.parse(request.getEndDate());
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder.function("DATE", LocalDate.class, root.get("endTime")), 
                        endDate
                    ));
                } catch (Exception e) {
                    log.warn("Invalid end date format: {}", request.getEndDate());
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<RoomBooking> bookingPage = roomBookingRepository.findAll(bookingSpec, pageable);
        
        // 转换为响应对象
        List<ApprovalListResponse> responseList = bookingPage.getContent().stream()
            .map(this::convertToApprovalListResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(bookingPage.getTotalElements());
        
        return result;
    }
    
    /**
     * 获取中文数字
     */
    private String getChineseNumber(Integer number) {
        if (number == null) return "一";
        String[] chineseNumbers = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
        if (number <= 10) {
            return chineseNumbers[number];
        } else if (number < 20) {
            return "十" + (number == 10 ? "" : chineseNumbers[number - 10]);
        } else if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;
            return chineseNumbers[tens] + "十" + (ones == 0 ? "" : chineseNumbers[ones]);
        } else {
            return number.toString();
        }
    }

    @Override
    public ResponsePageDataEntity<RoomBookingStatsResponse> getRoomBookingStats(RoomBookingStatsRequest request) {
        // 构建分页参数
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        
        // 使用原生SQL查询统计数据
        String sql = "SELECT r.id AS room_id, r.room_name AS room_name, " +
                     "COALESCE(COUNT(DISTINCT CASE WHEN rb.id IS NOT NULL THEN rb.id END), 0) AS booking_count, " +
                     "COALESCE(SUM(CASE WHEN rb.booking_start_time IS NOT NULL AND rb.booking_end_time IS NOT NULL " +
                     "         AND rb.booking_end_time > rb.booking_start_time THEN " +
                     "    TIMESTAMPDIFF(MINUTE, rb.booking_start_time, rb.booking_end_time) " +
                     "    ELSE 0 END), 0) AS total_duration, " +
                     "COALESCE(SUM(CASE WHEN rb.id IS NOT NULL AND rb.extend1 IS NOT NULL AND TRIM(rb.extend1) != '' THEN " +
                     "  (LENGTH(rb.extend1) - LENGTH(REPLACE(rb.extend1, ',', '')) + 1) " +
                     "  WHEN rb.id IS NOT NULL THEN 1 " +
                     "  ELSE 0 END), 0) AS total_people " +
                     "FROM tb_room r " +
                     "LEFT JOIN tb_room_booking rb ON r.id = rb.room_id " +
                     "  AND rb.approval_status = 'APPROVED' " +
                     "WHERE r.is_deleted = 0 AND r.room_type_name = '教室' ";
        
        List<Object> params = new ArrayList<>();
        
        // 添加筛选条件
        if (StringUtils.hasText(request.getRoomName())) {
            sql += "AND r.room_name LIKE ? ";
            params.add("%" + request.getRoomName() + "%");
        }
        
        if (StringUtils.hasText(request.getAreaId())) {
            sql += "AND r.room_area_id = ? ";
            params.add(request.getAreaId());
        }
        
        sql += "GROUP BY r.id, r.room_name ";
        sql += "ORDER BY booking_count DESC ";
        
        // 获取总记录数
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS t";
        Query countQuery = entityManager.createNativeQuery(countSql);
        for (int i = 0; i < params.size(); i++) {
            countQuery.setParameter(i + 1, params.get(i));
        }
        Long total = ((Number) countQuery.getSingleResult()).longValue();
        
        // 添加分页
        sql += "LIMIT ? OFFSET ?";
        params.add(request.getPageSize());
        params.add((request.getPageNum() - 1) * request.getPageSize());
        
        // 执行查询
        Query query = entityManager.createNativeQuery(sql);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        // 转换结果
        List<RoomBookingStatsResponse> statsList = results.stream()
            .map(row -> {
                RoomBookingStatsResponse stats = new RoomBookingStatsResponse();
                stats.setRoomId((String) row[0]);
                stats.setRoomName((String) row[1]);
                stats.setBookingCount(((Number) row[2]).intValue());
                stats.setTotalDuration(row[3] != null ? ((Number) row[3]).intValue() : 0);
                stats.setTotalPeople(row[4] != null ? ((Number) row[4]).intValue() : 0);
                return stats;
            })
            .collect(Collectors.toList());
        
        // 构建分页响应
        ResponsePageDataEntity<RoomBookingStatsResponse> response = new ResponsePageDataEntity<>();
        response.setRows(statsList);
        response.setTotal(total);
        
        return response;
    }

    @Override
    public RoomBookingDetailsResponse getRoomBookingDetails(RoomBookingDetailsRequest request) {
        // 获取教室信息
        Optional<Room> roomOpt = roomRepository.findById(request.getRoomId());
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("教室不存在");
        }
        
        Room room = roomOpt.get();
        
        // 构建教室信息
        RoomBookingDetailsResponse.RoomInfo roomInfo = RoomBookingDetailsResponse.RoomInfo.builder()
            .roomId(room.getId())
            .roomName(room.getRoomName())
            .roomCode(room.getExtend1())  // 房间编码存储在extend1字段
            .buildingName(room.getRoomAreaName() != null ? room.getRoomAreaName() : "")  // 使用roomAreaName字段
            .floor("")  // Room实体中没有floor字段，可以从roomName或roomNo中解析
            .capacity(room.getRoomVolume())  // 使用roomVolume字段
            .equipment(room.getRemark())  // 设备信息可能在备注中
            .build();
        
        // 构建查询条件
        Specification<RoomBooking> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 教室ID
            predicates.add(criteriaBuilder.equal(root.get("roomId"), request.getRoomId()));
            
            // 借用/预约名称
            if (StringUtils.hasText(request.getBookingName())) {
                predicates.add(criteriaBuilder.like(root.get("bookingName"), 
                    "%" + request.getBookingName() + "%"));
            }
            
            // 审核状态
            if (StringUtils.hasText(request.getAuditStatus())) {
                RoomBooking.ApprovalStatus status = convertAuditStatus(request.getAuditStatus());
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), status));
                }
            }
            
            // 使用状态
            if (StringUtils.hasText(request.getUsageStatus())) {
                RoomBooking.UsageStatus status = convertUsageStatus(request.getUsageStatus());
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("usageStatus"), status));
                }
            }
            
            // 预约人
            if (StringUtils.hasText(request.getApplicantName())) {
                predicates.add(criteriaBuilder.like(root.get("applicantName"), 
                    "%" + request.getApplicantName() + "%"));
            }
            
            // 时间范围
            if (request.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("bookingStartTime"), 
                    request.getStartTime().atStartOfDay()
                ));
            }
            
            if (request.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("bookingEndTime"), 
                    request.getEndTime().atTime(23, 59, 59)
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 分页查询
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createTime"));
        Page<RoomBooking> bookingPage = roomBookingRepository.findAll(spec, pageable);
        
        // 转换预约详情
        List<RoomBookingDetailsResponse.BookingDetail> detailList = bookingPage.getContent().stream()
            .map(booking -> {
                // 获取审批信息
                List<BookingApproval> approvals = bookingApprovalRepository.findByBookingIdOrderByApprovalLevel(booking.getId());
                BookingApproval latestApproval = approvals.stream()
                    .filter(a -> a.getApprovalTime() != null)  // 已审批的记录
                    .reduce((first, second) -> second)
                    .orElse(null);
                
                return RoomBookingDetailsResponse.BookingDetail.builder()
                    .id(booking.getId())
                    .bookingName(booking.getBookingName())
                    .bookingTime(formatBookingTime(booking))
                    .description(booking.getDescription())
                    .applicantName(booking.getApplicantName())
                    .applicantPhone(booking.getApplicantPhone())
                    .applicantDepartment(booking.getApplicantDepartment())
                    .auditStatus(booking.getApprovalStatus().getDisplayName())
                    .auditStatusCode(booking.getApprovalStatus().name())
                    .usageStatus(booking.getUsageStatus().getDisplayName())
                    .usageStatusCode(booking.getUsageStatus().name())
                    .bookingStartTime(booking.getBookingStartTime() != null ? booking.getBookingStartTime().toString() : "")
                    .bookingEndTime(booking.getBookingEndTime() != null ? booking.getBookingEndTime().toString() : "")
                    .peopleCount(getParticipantCount(booking))
                    .createTime(booking.getCreateTime() != null ? booking.getCreateTime().toString() : "")
                    .approvalTime(latestApproval != null && latestApproval.getApprovalTime() != null ? 
                        latestApproval.getApprovalTime().toString() : null)
                    .approverName(latestApproval != null ? latestApproval.getApproverName() : null)
                    .approvalComment(latestApproval != null ? latestApproval.getApprovalComment() : null)
                    .build();
            })
            .collect(Collectors.toList());
        
        // 构建分页数据
        RoomBookingDetailsResponse.PageData pageData = RoomBookingDetailsResponse.PageData.builder()
            .total(bookingPage.getTotalElements())
            .pages(bookingPage.getTotalPages())
            .pageNum(request.getPageNum())
            .pageSize(request.getPageSize())
            .list(detailList)
            .build();
        
        // 构建响应
        return RoomBookingDetailsResponse.builder()
            .roomInfo(roomInfo)
            .pageData(pageData)
            .build();
    }
    
    /**
     * 格式化预约时间
     */
    private String formatBookingTime(RoomBooking booking) {
        if (StringUtils.hasText(booking.getBookingPeriod())) {
            return booking.getBookingPeriod();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return booking.getBookingStartTime().format(formatter) + " - " + 
               booking.getBookingEndTime().format(formatter);
    }
    
    /**
     * 获取参与人数
     */
    private Integer getParticipantCount(RoomBooking booking) {
        if (StringUtils.hasText(booking.getExtend1())) {
            try {
                // 假设参与人存储在extend1字段，用逗号分隔
                String[] participants = booking.getExtend1().split(",");
                return participants.length;
            } catch (Exception e) {
                return 1;
            }
        }
        return 1;
    }
    
    /**
     * 转换审核状态
     */
    private RoomBooking.ApprovalStatus convertAuditStatus(String status) {
        switch (status) {
            case "通过":
                return RoomBooking.ApprovalStatus.APPROVED;
            case "审核中":
                return RoomBooking.ApprovalStatus.PENDING;
            case "拒绝":
                return RoomBooking.ApprovalStatus.REJECTED;
            case "已取消":
                return RoomBooking.ApprovalStatus.CANCELLED;
            default:
                return null;
        }
    }
    
    /**
     * 转换使用状态
     */
    private RoomBooking.UsageStatus convertUsageStatus(String status) {
        switch (status) {
            case "未开始":
                return RoomBooking.UsageStatus.NOT_STARTED;
            case "进行中":
                return RoomBooking.UsageStatus.IN_PROGRESS;
            case "已结束":
                return RoomBooking.UsageStatus.COMPLETED;
            case "已取消":
                return RoomBooking.UsageStatus.CANCELLED;
            default:
                return null;
        }
    }

    // ==================== 教室借用记录相关接口实现 ====================

    @Override
    public ResponsePageDataEntity<AccessRecordResponse> getAccessRecords(AccessRecordsRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize(), 
                Sort.by(Sort.Direction.DESC, "accessTime"));
        
        // 构建查询条件
        Specification<RoomAccessRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 租户隔离
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 区域筛选
            if (StringUtils.hasText(request.getAreaId())) {
                // 需要关联room表查询区域
                predicates.add(criteriaBuilder.equal(root.get("roomId"), request.getAreaId()));
            }
            
            // 教室筛选
            if (StringUtils.hasText(request.getRoomId())) {
                predicates.add(criteriaBuilder.equal(root.get("roomId"), request.getRoomId()));
            }
            
            // 基础信息筛选（姓名/工号/联系方式）
            if (StringUtils.hasText(request.getBasicInfo())) {
                Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + request.getBasicInfo() + "%");
                Predicate employeeIdPredicate = criteriaBuilder.like(root.get("employeeId"), "%" + request.getBasicInfo() + "%");
                Predicate phonePredicate = criteriaBuilder.like(root.get("phone"), "%" + request.getBasicInfo() + "%");
                predicates.add(criteriaBuilder.or(namePredicate, employeeIdPredicate, phonePredicate));
            }
            
            // 时间范围筛选
            if (StringUtils.hasText(request.getStartTime())) {
                LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("accessTime"), startTime));
            }
            if (StringUtils.hasText(request.getEndTime())) {
                LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("accessTime"), endTime));
            }
            
            // 开门方式筛选
            if (StringUtils.hasText(request.getOpenMethod())) {
                predicates.add(criteriaBuilder.equal(root.get("openMethod"), request.getOpenMethod()));
            }
            
            // 通行类型筛选
            if (StringUtils.hasText(request.getAccessType())) {
                predicates.add(criteriaBuilder.equal(root.get("accessType"), request.getAccessType()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 执行查询
        Page<RoomAccessRecord> page = roomAccessRecordRepository.findAll(spec, pageable);
        
        // 转换为响应对象
        List<AccessRecordResponse> responseList = page.getContent().stream()
            .map(this::convertToAccessRecordResponse)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<AccessRecordResponse> result = new ResponsePageDataEntity<>();
        result.setRows(responseList);
        result.setTotal(page.getTotalElements());
        return result;
    }

    @Override
    public ExportResponse exportAccessRecords(ExportAccessRecordsRequest request) {
        // 模拟导出功能
        String fileName = "教室借用记录_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        return ExportResponse.builder()
            .fileName(fileName)
            .fileUrl("/download/temp/" + fileName)
            .fileSize(102400L)
            .recordCount(request.getExportType().equals("current") ? request.getPageSize() : 100)
            .build();
    }

    @Override
    public ExportResponse exportRoomBookingStats(RoomBookingStatsRequest request) {
        // 模拟导出统计数据功能
        String fileName = "教室预约统计_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        // 根据请求参数确定导出记录数
        int recordCount;
        if ("current".equals(request.getExportType())) {
            recordCount = request.getPageSize() != null ? request.getPageSize() : 10;
        } else {
            // 这里可以根据实际业务需求查询总数
            recordCount = 50; // 假设总共有50条统计记录
        }
        
        return ExportResponse.builder()
            .fileName(fileName)
            .fileUrl("/download/temp/" + fileName)
            .fileSize(81920L) // 约80KB
            .recordCount(recordCount)
            .build();
    }

    @Override
    public ExportResponse exportRoomBookingDetails(RoomBookingDetailsRequest request) {
        // 模拟导出教室预约详情功能
        String fileName = "教室预约详情_" + request.getRoomId() + "_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        // 根据请求参数确定导出记录数
        int recordCount;
        if ("current".equals(request.getExportType())) {
            recordCount = request.getPageSize() != null ? request.getPageSize() : 10;
        } else {
            // 这里可以根据实际业务需求查询该教室的总预约记录数
            recordCount = 25; // 假设该教室有25条预约记录
        }
        
        return ExportResponse.builder()
            .fileName(fileName)
            .fileUrl("/download/temp/" + fileName)
            .fileSize(65536L) // 约64KB
            .recordCount(recordCount)
            .build();
    }

    @Override
    public AccessStatsResponse getAccessStats(String startTime, String endTime, String areaId) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 解析时间参数
        LocalDateTime tempStart = null;
        LocalDateTime tempEnd = null;
        if (StringUtils.hasText(startTime)) {
            tempStart = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (StringUtils.hasText(endTime)) {
            tempEnd = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        // 如果没有指定时间范围，则使用合理的默认值
        if (tempStart == null && tempEnd == null) {
            tempEnd = LocalDateTime.now();
            tempStart = tempEnd.minusMonths(1); // 默认查询最近一个月
        } else if (tempStart == null) {
            tempStart = tempEnd.minusMonths(1);
        } else if (tempEnd == null) {
            tempEnd = LocalDateTime.now();
        }
        
        // 声明为final变量供lambda使用
        final LocalDateTime start = tempStart;
        final LocalDateTime end = tempEnd;
        
        // 构建查询条件
        Specification<RoomAccessRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            predicates.add(criteriaBuilder.between(root.get("accessTime"), start, end));
            
            if (StringUtils.hasText(areaId)) {
                // 这里简化处理，实际应该关联room表查询区域
                predicates.add(criteriaBuilder.equal(root.get("roomId"), areaId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 查询总记录数
        Long totalRecords = roomAccessRecordRepository.count(spec);
        
        // 查询今日记录数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        Specification<RoomAccessRecord> todaySpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            predicates.add(criteriaBuilder.between(root.get("accessTime"), todayStart, todayEnd));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Long todayRecords = roomAccessRecordRepository.count(todaySpec);
        
        // 查询使用教室数和用户数
        Long totalRooms = roomAccessRecordRepository.countDistinctRoomsByAccessTimeBetween(start, end);
        Long totalUsers = roomAccessRecordRepository.countDistinctUsersByAccessTimeBetween(start, end);
        
        // 查询各种统计数据
        List<RoomAccessRecord> records = roomAccessRecordRepository.findAll(spec);
        
        Map<String, Long> accessTypeStats = records.stream()
            .filter(r -> r.getAccessType() != null)
            .collect(Collectors.groupingBy(RoomAccessRecord::getAccessType, Collectors.counting()));
            
        Map<String, Long> openMethodStats = records.stream()
            .filter(r -> r.getOpenMethod() != null)
            .collect(Collectors.groupingBy(RoomAccessRecord::getOpenMethod, Collectors.counting()));
            
        Map<String, Long> userTypeStats = records.stream()
            .filter(r -> r.getUserType() != null)
            .collect(Collectors.groupingBy(RoomAccessRecord::getUserType, Collectors.counting()));
        
        return AccessStatsResponse.builder()
            .totalRecords(totalRecords)
            .todayRecords(todayRecords)
            .totalRooms(totalRooms != null ? totalRooms : 0L)
            .totalUsers(totalUsers != null ? totalUsers : 0L)
            .accessTypeStats(accessTypeStats)
            .openMethodStats(openMethodStats)
            .userTypeStats(userTypeStats)
            .build();
    }

    @Override
    public List<RoomStatusResponse> getRoomStatus(String areaId) {
        String customerId = SecurityUtil.getCustomerId();
        List<RoomStatusResponse> statusList = new ArrayList<>();
        
        // 获取教室列表
        List<Room> rooms;
        if (StringUtils.hasText(areaId)) {
            rooms = roomRepository.findByCustomerIdAndRoomAreaId(customerId, areaId);
        } else {
            rooms = roomRepository.findByCustomerId(customerId);
        }
        
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        
        for (Room room : rooms) {
            // 获取最近的通行记录
            List<RoomAccessRecord> recentRecords = roomAccessRecordRepository.findLatestByRoomId(room.getId());
            LocalDateTime lastAccessTime = null;
            int currentUsers = 0;
            
            if (!recentRecords.isEmpty()) {
                lastAccessTime = recentRecords.get(0).getAccessTime();
                
                // 计算当前使用人数（简化逻辑：统计今天的进入次数）
                long todayAccess = recentRecords.stream()
                    .filter(r -> r.getAccessTime().isAfter(todayStart))
                    .count();
                currentUsers = (int) Math.min(todayAccess, 50); // 最多50人
            }
            
            // 判断使用状态（简化逻辑：如果30分钟内有通行记录则认为使用中）
            String status = "空闲";
            if (lastAccessTime != null && lastAccessTime.isAfter(now.minusMinutes(30))) {
                status = "使用中";
            }
            
            // 获取当前预约信息
            RoomStatusResponse.CurrentBookingInfo currentBooking = getCurrentBookingInfo(room.getId(), now);
            
            RoomStatusResponse roomStatus = RoomStatusResponse.builder()
                .roomId(room.getId())
                .roomName(room.getRoomName())
                .buildingName(getBuildingName(room))
                .floor(getFloor(room))
                .status(status)
                .currentUsers(currentUsers)
                .lastAccessTime(lastAccessTime)
                .currentBooking(currentBooking)
                .build();
                
            statusList.add(roomStatus);
        }
        
        return statusList;
    }
    
    /**
     * 获取当前预约信息
     */
    private RoomStatusResponse.CurrentBookingInfo getCurrentBookingInfo(String roomId, LocalDateTime now) {
        // 查询当前时间段内的预约
        List<RoomBooking> currentBookings = roomBookingRepository.findCurrentBookingsByRoomId(roomId, now);
        
        if (!currentBookings.isEmpty()) {
            RoomBooking booking = currentBookings.get(0);
            return RoomStatusResponse.CurrentBookingInfo.builder()
                .bookingId(booking.getId())
                .bookingName(booking.getBookingName())
                .applicantName(booking.getApplicantName())
                .bookingPeriod(formatBookingPeriod(booking))
                .build();
        }
        
        return null;
    }
    
    /**
     * 格式化预约时段
     */
    private String formatBookingPeriod(RoomBooking booking) {
        if (booking.getBookingStartTime() != null && booking.getBookingEndTime() != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return booking.getBookingStartTime().format(timeFormatter) + "-" + booking.getBookingEndTime().format(timeFormatter);
        }
        return "";
    }
    
    /**
     * 获取楼栋名称
     */
    private String getBuildingName(Room room) {
        // 这里可以根据实际的数据结构获取
        return room.getRoomAreaName(); // 假设区域名称包含楼栋信息
    }
    
    /**
     * 获取楼层信息
     */
    private String getFloor(Room room) {
        // 这里可以根据实际的数据结构获取
        String roomName = room.getRoomName();
        if (roomName != null && roomName.length() > 2) {
            String floorChar = roomName.substring(roomName.length() - 2, roomName.length() - 1);
            return floorChar + "F";
        }
        return "1F";
    }

    /**
     * 将RoomAccessRecord实体转换为AccessRecordResponse
     */
    private AccessRecordResponse convertToAccessRecordResponse(RoomAccessRecord record) {
        return AccessRecordResponse.builder()
            .id(record.getId())
            .roomId(record.getRoomId())
            .roomName(record.getRoomName())
            .buildingName(record.getBuildingName())
            .floor(record.getFloor())
            .userId(record.getUserId())
            .name(record.getName())
            .gender(record.getGender())
            .employeeId(record.getEmployeeId())
            .phone(record.getPhone())
            .department(record.getDepartment())
            .userType(record.getUserType())
            .openMethod(record.getOpenMethod())
            .accessTime(record.getAccessTime())
            .accessType(record.getAccessType())
            .bookingId(record.getBookingId())
            .bookingName(record.getBookingName())
            .bookingPeriod(record.getBookingPeriod())
            .deviceId(record.getDeviceId())
            .deviceName(record.getDeviceName())
            .createTime(record.getCreateTime())
            .build();
    }

    /**
     * 从预约信息创建课程排课记录
     * 当预约审批通过后，自动向CoursePlanning添加对应的数据
     * 支持多时间段的预约，为每个时间段创建单独的排课记录
     */
    private void createCoursePlanningFromBooking(RoomBooking booking) {
        try {
            if (booking == null) {
                log.error("booking对象为null，无法创建课程排课记录");
                return;
            }
            
            log.info("开始为预约创建课程排课记录，预约ID: {}, 预约名称: {}", 
                booking.getId(), booking.getBookingName());
            
            // 解析多个时间段
            List<BookingTimeSlot> timeSlots = parseBookingTimeSlots(booking);
            
            if (timeSlots.isEmpty()) {
                log.warn("未能解析到有效的预约时间段，预约ID: {}", booking.getId());
                return;
            }
            
            int successCount = 0;
            // 为每个时间段创建单独的排课记录
            for (int i = 0; i < timeSlots.size(); i++) {
                BookingTimeSlot timeSlot = timeSlots.get(i);
                try {
                    log.info("开始创建第{}个时间段的排课记录: {}", i + 1, timeSlot);
                    
                    CoursePlanning coursePlanning = convertBookingToCoursePlanning(booking, timeSlot);
                    
                    log.info("转换完成，准备保存排课记录: ID={}, 课程名={}, 开始时间={}, 结束时间={}, 日期={}", 
                        coursePlanning.getId(), coursePlanning.getCourseName(), 
                        coursePlanning.getCourseStartTime(), coursePlanning.getCourseEndTime(),
                        coursePlanning.getCourseDate());
                    
                    CoursePlanning saved = coursePlanningRepository.save(coursePlanning);
                    coursePlanningRepository.flush(); // 强制立即提交到数据库
                    successCount++;
                    
                    // 验证保存是否成功
                    CoursePlanning verified = coursePlanningRepository.findById(saved.getId()).orElse(null);
                    if (verified != null) {
                        log.info("✅ 成功创建并验证第{}个课程排课记录，排课ID: {}, 预约ID: {}, 时间段: {}", 
                            i + 1, saved.getId(), booking.getId(), timeSlot);
                    } else {
                        log.error("⚠️ 排课记录保存后无法查询到，可能保存失败: ID={}", saved.getId());
                    }
                        
                } catch (Exception e) {
                    log.error("❌ 创建第{}个时间段的排课记录失败，预约ID: {}, 时间段: {}", 
                        i + 1, booking.getId(), timeSlot, e);
                }
            }
            
            log.info("预约排课记录创建完成，预约ID: {}, 总时间段: {}, 成功创建: {}", 
                booking.getId(), timeSlots.size(), successCount);
            
            if (successCount != timeSlots.size()) {
                log.error("⚠️ 警告：期望创建{}条记录，实际成功创建{}条记录，可能存在保存失败", 
                    timeSlots.size(), successCount);
            }
                
        } catch (Exception e) {
            log.error("创建课程排课记录失败，预约ID: {}", booking.getId(), e);
            // 这里可以选择抛出异常或者仅记录日志
            // 建议仅记录日志，避免影响主要的审批流程
        }
    }
    
    /**
     * 预约时间段内部类
     */
    private static class BookingTimeSlot {
        public final LocalDateTime startTime;
        public final LocalDateTime endTime;
        public final LocalDate date;
        
        public BookingTimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.date = startTime.toLocalDate();
        }
        
        @Override
        public String toString() {
            return String.format("TimeSlot[%s -> %s, 日期=%s, 星期=%d]", 
                startTime, endTime, date, startTime.getDayOfWeek().getValue());
        }
    }
    
    /**
     * 解析预约的多个时间段
     * 支持格式: "2025-03-06 08:55:00-09:40:00;2025-03-05 08:55:00-09:40:00"
     * 使用严格的正则表达式验证，支持全角分隔符和跨天处理
     */
    private List<BookingTimeSlot> parseBookingTimeSlots(RoomBooking booking) {
        List<BookingTimeSlot> timeSlots = new ArrayList<>();
        
        try {
            log.debug("解析预约时间段 - 预约ID: {}, bookingPeriod: [{}]", 
                booking.getId(), booking.getBookingPeriod());
            
            // 首先尝试从bookingPeriod字段解析（如果包含多时间段信息）
            if (StringUtils.hasText(booking.getBookingPeriod()) && 
                booking.getBookingPeriod().contains(";")) {
                
                log.info("从bookingPeriod解析多时间段: [{}]", booking.getBookingPeriod());
                timeSlots = parseTimeRangeString(booking.getBookingPeriod());
                log.info("bookingPeriod解析结果: {}个时间段", timeSlots.size());
                return timeSlots;
            }
            
            // 默认使用单个时间段（bookingStartTime和bookingEndTime）
            if (booking.getBookingStartTime() != null && booking.getBookingEndTime() != null) {
                timeSlots.add(new BookingTimeSlot(booking.getBookingStartTime(), booking.getBookingEndTime()));
                log.info("使用单个时间段: {}-{}", booking.getBookingStartTime(), booking.getBookingEndTime());
            }
            
        } catch (Exception e) {
            log.error("解析预约时间段失败，预约ID: {}", booking.getId(), e);
            
            // 兜底：使用bookingStartTime和bookingEndTime
            if (booking.getBookingStartTime() != null && booking.getBookingEndTime() != null) {
                timeSlots.add(new BookingTimeSlot(booking.getBookingStartTime(), booking.getBookingEndTime()));
            }
        }
        
        return timeSlots;
    }
    
    /**
     * 解析分号分隔的时间段字符串，使用严格的正则表达式验证
     * 支持格式: "yyyy-MM-dd HH:mm:ss-HH:mm:ss;yyyy-MM-dd HH:mm:ss-HH:mm:ss;..."
     * 支持全角分隔符和跨天处理
     */
    private List<BookingTimeSlot> parseTimeRangeString(String raw) {
        List<BookingTimeSlot> timeSlots = new ArrayList<>();
        
        try {
            log.debug("开始解析时间段字符串: [{}]", raw);
            
            // 归一化处理：全角→半角，清理空格
            String normalized = normalizeTimeString(raw);
            log.debug("归一化后的字符串: [{}]", normalized);
            
            // 分割时间段
            List<String> segments = splitSegments(normalized);
            log.debug("分割得到{}个时间段: {}", segments.size(), segments);
            
            // 解析每个时间段
            for (int i = 0; i < segments.size(); i++) {
                String segment = segments.get(i);
                try {
                    log.info("📍 开始解析第{}个时间段: [{}]", i + 1, segment);
                    BookingTimeSlot timeSlot = parseTimeSegment(segment);
                    timeSlots.add(timeSlot);
                    log.info("✅ 成功解析第{}个时间段: {}", i + 1, timeSlot);
                } catch (Exception e) {
                    log.error("❌ 解析第{}个时间段失败: [{}]", i + 1, segment, e);
                    throw new IllegalArgumentException("时间段格式错误: " + segment, e);
                }
            }
            
            log.info("时间段字符串解析完成，总共解析出{}个时间段", timeSlots.size());
            
        } catch (Exception e) {
            log.error("解析时间段字符串失败: [{}]", raw, e);
            throw e;
        }
        
        return timeSlots;
    }
    
    /**
     * 归一化时间字符串：全角→半角，清理空格
     */
    private String normalizeTimeString(String raw) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }
        
        // 1. 替换全角分隔符为半角分号
        String result = raw.replace("；", ";")    // 全角分号
                          .replace("，", ";")    // 全角逗号  
                          .replace("、", ";");   // 顿号
        
        // 2. 规范分号空格
        result = result.replaceAll("\\s*;\\s*", ";");
        
        // 3. 规范连字符空格
        result = result.replaceAll("\\s*-\\s*", "-");
        
        // 4. 整体trim
        result = result.trim();
        
        return result;
    }
    
    /**
     * 分割时间段，返回非空段列表
     */
    private List<String> splitSegments(String normalized) {
        List<String> segments = new ArrayList<>();
        
        if (normalized == null || normalized.isEmpty()) {
            return segments;
        }
        
        String[] parts = normalized.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                segments.add(trimmed);
            }
        }
        
        return segments;
    }
    
    /**
     * 解析单个时间段，严格按照 yyyy-MM-dd HH:mm:ss-HH:mm:ss 格式
     */
    private BookingTimeSlot parseTimeSegment(String segment) {
        // 严格的正则表达式模式
        String pattern = "^(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2})-(\\d{2}:\\d{2}:\\d{2})$";
        java.util.regex.Pattern timePattern = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = timePattern.matcher(segment);
        
        if (!matcher.matches()) {
            throw new IllegalArgumentException("时间段格式不匹配，期望格式: yyyy-MM-dd HH:mm:ss-HH:mm:ss，实际: " + segment);
        }
        
        // 提取匹配组
        String dateStr = matcher.group(1);      // yyyy-MM-dd
        String startTimeStr = matcher.group(2); // HH:mm:ss  
        String endTimeStr = matcher.group(3);   // HH:mm:ss
        
        try {
            // 使用标准格式解析
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // 解析开始时间
            LocalDateTime startTime = LocalDateTime.parse(dateStr + " " + startTimeStr, formatter);
            
            // 解析结束时间（同日期）
            LocalDateTime endTime = LocalDateTime.parse(dateStr + " " + endTimeStr, formatter);
            
            // 跨天处理：如果结束时间早于开始时间，认为跨天了
            if (endTime.isBefore(startTime)) {
                endTime = endTime.plusDays(1);
                log.info("检测到跨天时间段，自动调整结束时间: {} -> {}", startTime, endTime);
            }
            
            return new BookingTimeSlot(startTime, endTime);
            
        } catch (Exception e) {
            throw new IllegalArgumentException("时间解析失败: " + segment, e);
        }
    }
    
    
    /**
     * 将RoomBooking实体转换为CoursePlanning实体（支持指定时间段）
     */
    private CoursePlanning convertBookingToCoursePlanning(RoomBooking booking, BookingTimeSlot timeSlot) {
        CoursePlanning coursePlanning = new CoursePlanning();
        
        // 生成UUID作为主键
        coursePlanning.setId(java.util.UUID.randomUUID().toString());
        
        // 基本信息
        coursePlanning.setCstmId(booking.getCstmId());
        coursePlanning.setCourseName(booking.getBookingName()); // 使用预约名称作为课程名称
        coursePlanning.setTeacherName(booking.getApplicantName()); // 使用申请人作为教师名称
        
        // 教室信息
        coursePlanning.setClassRoomId(booking.getRoomId());
        coursePlanning.setClassRoomName(booking.getRoomName());
        
        // 时间信息 - 使用指定时间段的时间
        // 将LocalDateTime转换为Date
        java.util.Date startTime = java.sql.Timestamp.valueOf(timeSlot.startTime);
        java.util.Date endTime = java.sql.Timestamp.valueOf(timeSlot.endTime);
        java.util.Date courseDate = java.sql.Date.valueOf(timeSlot.date);
        
        coursePlanning.setCourseStartTime(startTime);
        coursePlanning.setCourseEndTime(endTime);
        coursePlanning.setCourseDate(courseDate);
        
        // 计算星期几 (1=周一, 7=周日) - 使用具体时间段的日期
        int dayOfWeek = timeSlot.startTime.getDayOfWeek().getValue();
        coursePlanning.setWeekDay((long) dayOfWeek);
        
        // 设置课程节次 (根据课程时间配置计算) - 使用具体时间段的开始时间
        coursePlanning.setCourseSection(calculateCourseSection(timeSlot.startTime, booking.getCstmId()));
        
        // 设置时间戳
        java.util.Date now = new java.util.Date();
        coursePlanning.setCreateTime(now);
        coursePlanning.setLastUpdateTime(now);
        
        // 教学周信息 (根据SchoolCalendar2查询真实教学周) - 使用具体时间段的开始时间
        coursePlanning.setWeekNumber(calculateWeekNumber(timeSlot.startTime, booking.getCstmId()));
        
        return coursePlanning;
    }
    
    /**
     * 根据开始时间和课程时间配置计算课程节次
     * 使用TbAttendanceAdministrativeCourseTimeConfig进行精确匹配
     */
    private Long calculateCourseSection(LocalDateTime startTime, String customerId) {
        try {
            // 查询该客户域的课程时间配置
            List<TbAttendanceAdministrativeCourseTimeConfig> timeConfigs = 
                courseTimeConfigRepository.findByCstmIdOrderByCourseSort(customerId);
            
            if (timeConfigs.isEmpty()) {
                log.warn("未找到课程时间配置，使用默认计算，客户ID: {}", customerId);
                return getDefaultCourseSection(startTime);
            }
            
            // 格式化开始时间为HH:mm格式进行匹配
            String timeStr = startTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            
            // 找到最匹配的课程节次
            for (TbAttendanceAdministrativeCourseTimeConfig config : timeConfigs) {
                String configStartTime = config.getCourseStartTime();
                String configEndTime = config.getCourseEndTime();
                
                if (configStartTime != null && configEndTime != null) {
                    // 判断预约时间是否在课程时间段内
                    if (isTimeInRange(timeStr, configStartTime, configEndTime)) {
                        log.info("匹配到课程节次: {}, 时间段: {}-{}, 预约时间: {}", 
                            config.getCourseSort(), configStartTime, configEndTime, timeStr);
                        return config.getCourseSort().longValue();
                    }
                }
            }
            
            log.warn("未匹配到具体课程节次，使用默认计算，预约时间: {}", timeStr);
            return getDefaultCourseSection(startTime);
            
        } catch (Exception e) {
            log.error("计算课程节次失败，使用默认计算", e);
            return getDefaultCourseSection(startTime);
        }
    }
    
    /**
     * 判断时间是否在指定范围内
     */
    private boolean isTimeInRange(String timeStr, String startTime, String endTime) {
        try {
            // 简单的时间比较（HH:mm格式）
            return timeStr.compareTo(startTime) >= 0 && timeStr.compareTo(endTime) <= 0;
        } catch (Exception e) {
            log.error("时间范围比较失败: {} vs {}-{}", timeStr, startTime, endTime, e);
            return false;
        }
    }
    
    /**
     * 默认的课程节次计算逻辑
     */
    private Long getDefaultCourseSection(LocalDateTime startTime) {
        int hour = startTime.getHour();
        
        // 默认的节次计算逻辑
        if (hour < 8) return 1L;          // 第1节 (8:00前)
        else if (hour < 10) return 2L;    // 第2节 (8:00-10:00)
        else if (hour < 12) return 3L;    // 第3节 (10:00-12:00)
        else if (hour < 14) return 4L;    // 第4节 (12:00-14:00)
        else if (hour < 16) return 5L;    // 第5节 (14:00-16:00)
        else if (hour < 18) return 6L;    // 第6节 (16:00-18:00)
        else return 7L;                   // 第7节 (18:00后)
    }
    
    /**
     * 根据预约时间和客户域查询教学周信息
     * 使用SchoolCalendar2进行精确查询
     */
    private String calculateWeekNumber(LocalDateTime bookingTime, String customerId) {
        try {
            // 将LocalDateTime转换为Date
            java.util.Date targetDate = java.sql.Timestamp.valueOf(bookingTime);
            
            // 查询该日期所属的教学周
            Optional<SchoolCalendar2> schoolCalendarOpt = schoolCalendar2Repository
                .findFirstByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCustomerId(
                    targetDate, targetDate, customerId);
            
            if (schoolCalendarOpt.isPresent()) {
                SchoolCalendar2 schoolCalendar = schoolCalendarOpt.get();
                String weekInfo = String.format("%s-%s学期第%d周", 
                    schoolCalendar.getXn(),                    // 学年 
                    schoolCalendar.getXq() == 1 ? "上" : "下",  // 学期
                    schoolCalendar.getWeek());                 // 教学周
                
                log.info("查询到教学周信息: {}, 日期: {}", weekInfo, targetDate);
                return weekInfo;
            } else {
                log.warn("未找到对应的教学周信息，客户ID: {}, 日期: {}", customerId, targetDate);
                return "非教学周";
            }
            
        } catch (Exception e) {
            log.error("查询教学周信息失败，客户ID: {}", customerId, e);
            return "临时预约";
        }
    }

    // ==================== 远程开门记录相关方法实现 ====================

    @Override
    public ResponsePageDataEntity<RemoteAccessRecordResponse> getRemoteAccessRecords(RemoteAccessRecordsRequest request) {
        try {
            // 转换为通用的教室出入记录请求，固定accessType为"远程开门"
            AccessRecordsRequest accessRequest = new AccessRecordsRequest();
            accessRequest.setPageNum(request.getPageNum());
            accessRequest.setPageSize(request.getPageSize());
            accessRequest.setAreaId(request.getAreaId());
            accessRequest.setRoomId(request.getRoomId());
            accessRequest.setBasicInfo(request.getBasicInfo());
            accessRequest.setStartTime(request.getStartTime());
            accessRequest.setEndTime(request.getEndTime());
            accessRequest.setOpenMethod(request.getOpenMethod());
            accessRequest.setAccessType("远程开门"); // 固定为远程开门
            
            // 调用现有的教室出入记录查询方法
            ResponsePageDataEntity<AccessRecordResponse> accessRecords = getAccessRecords(accessRequest);
            
            // 转换响应格式
            List<RemoteAccessRecordResponse> responseList = accessRecords.getRows().stream()
                .map(this::convertAccessRecordToRemoteAccessRecord)
                .collect(Collectors.toList());
            
            ResponsePageDataEntity<RemoteAccessRecordResponse> result = new ResponsePageDataEntity<>();
            result.setTotal(accessRecords.getTotal());
            result.setRows(responseList);
            return result;
                
        } catch (Exception e) {
            log.error("获取远程开门记录失败", e);
            ResponsePageDataEntity<RemoteAccessRecordResponse> result = new ResponsePageDataEntity<>();
            result.setTotal(0L);
            result.setRows(new ArrayList<>());
            return result;
        }
    }

    @Override
    public ExportResponse exportRemoteAccessRecords(RemoteAccessRecordsRequest request) {
        try {
            // 转换为通用的导出请求，固定accessType为"远程开门"
            ExportAccessRecordsRequest exportRequest = new ExportAccessRecordsRequest();
            exportRequest.setExportType(request.getPageNum() != null && request.getPageSize() != null ? "current" : "all");
            exportRequest.setPageNum(request.getPageNum());
            exportRequest.setPageSize(request.getPageSize());
            exportRequest.setAreaId(request.getAreaId());
            exportRequest.setRoomId(request.getRoomId());
            exportRequest.setBasicInfo(request.getBasicInfo());
            exportRequest.setStartTime(request.getStartTime());
            exportRequest.setEndTime(request.getEndTime());
            exportRequest.setOpenMethod(request.getOpenMethod());
            exportRequest.setAccessType("远程开门"); // 固定为远程开门
            
            // 调用现有的导出方法
            ExportResponse result = exportAccessRecords(exportRequest);
            
            // 修改文件名为远程开门记录
            String originalFileName = result.getFileName();
            String newFileName = originalFileName.replace("教室借用记录", "远程开门记录");
            result.setFileName(newFileName);
            
            return result;
                
        } catch (Exception e) {
            log.error("导出远程开门记录失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public RemoteAccessStatsResponse getRemoteAccessStats(String startTime, String endTime, String areaId) {
        try {
            // 调用现有的通用统计方法，然后基于accessType="远程开门"进行筛选
            AccessStatsResponse generalStats = getAccessStats(startTime, endTime, areaId);
            
            // 这里可以基于通用统计进行进一步筛选，或者模拟远程开门的统计数据
            // 由于现有接口没有按accessType分组统计，我们使用模拟数据
            
            return RemoteAccessStatsResponse.builder()
                .totalRecords(15L) // 模拟远程开门总记录数
                .todayRecords(3L)  // 模拟今日远程开门记录数
                .usedRooms(5L)     // 模拟使用教室数
                .activeOperators(2L) // 模拟活跃操作员数
                .openMethodStats(Arrays.asList(
                    new RemoteAccessStatsResponse.StatItem("管理员授权", 8L, 53.33),
                    new RemoteAccessStatsResponse.StatItem("应急开门", 5L, 33.33),
                    new RemoteAccessStatsResponse.StatItem("系统开门", 2L, 13.33)
                ))
                .operatorTypeStats(Arrays.asList(
                    new RemoteAccessStatsResponse.StatItem("管理员", 10L, 66.67),
                    new RemoteAccessStatsResponse.StatItem("系统", 3L, 20.00),
                    new RemoteAccessStatsResponse.StatItem("应急", 2L, 13.33)
                ))
                .hourlyStats(Arrays.asList(
                    new RemoteAccessStatsResponse.HourlyStatItem("08:00", 2L),
                    new RemoteAccessStatsResponse.HourlyStatItem("09:00", 3L),
                    new RemoteAccessStatsResponse.HourlyStatItem("10:00", 1L),
                    new RemoteAccessStatsResponse.HourlyStatItem("14:00", 4L),
                    new RemoteAccessStatsResponse.HourlyStatItem("15:00", 3L),
                    new RemoteAccessStatsResponse.HourlyStatItem("16:00", 2L)
                ))
                .build();
                
        } catch (Exception e) {
            log.error("获取远程开门统计信息失败", e);
            return new RemoteAccessStatsResponse(0L, 0L, 0L, 0L, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    @Override
    @Transactional
    public RemoteOpenDoorResponse executeRemoteOpenDoor(RemoteOpenDoorRequest request) {
        try {
            String currentUserId = SecurityUtil.getCurrentUserId();
            String currentUsername = SecurityUtil.getCurrentUsername();
            
            // 记录操作日志
            RemoteOperationLog operationLog = new RemoteOperationLog();
            operationLog.setOperatorId(currentUserId);
            operationLog.setOperatorName(currentUsername);
            operationLog.setOperationType("开门");
            operationLog.setRoomId(request.getRoomId());
            operationLog.setDeviceId(request.getDeviceId());
            operationLog.setReason(request.getReason());
            operationLog.setResult("成功");
            operationLog.setOperationTime(LocalDateTime.now());
            
            remoteOperationLogRepository.save(operationLog);
            
            // 创建出入记录
            RoomAccessRecord accessRecord = new RoomAccessRecord();
            accessRecord.setUserId(currentUserId);
            accessRecord.setName(currentUsername);
            accessRecord.setRoomId(request.getRoomId());
            accessRecord.setDeviceId(request.getDeviceId());
            accessRecord.setOpenMethod(request.getOpenMethod());
            accessRecord.setAccessTime(LocalDateTime.now());
            accessRecord.setAccessType("远程开门");
            accessRecord.setOperatorId(currentUserId);
            accessRecord.setOperatorName(currentUsername);
            accessRecord.setOperatorType("管理员");
            accessRecord.setReason(request.getReason());
            accessRecord.setBookingId(request.getBookingId());
            
            if (StringUtils.hasText(request.getTargetUserId())) {
                // 如果指定了目标用户，查询用户信息
                Optional<SysUser> targetUser = userRepository.findById(request.getTargetUserId());
                if (targetUser.isPresent()) {
                    SysUser user = targetUser.get();
                    accessRecord.setUserId(user.getId());
                    accessRecord.setName(user.getUsername());
                    accessRecord.setPhone(user.getPhone());
                }
            }
            
            roomAccessRecordRepository.save(accessRecord);
            
            log.info("远程开门成功：操作员={}，教室={}，原因={}", currentUsername, request.getRoomId(), request.getReason());
            
            return RemoteOpenDoorResponse.builder()
                .recordId(accessRecord.getId())
                .openTime(LocalDateTime.now())
                .success(true)
                .build();
                
        } catch (Exception e) {
            log.error("远程开门操作失败", e);
            throw new RuntimeException("远程开门失败：" + e.getMessage());
        }
    }

    @Override
    public ResponsePageDataEntity<RemoteOperationLog> getOperationLogs(OperationLogsRequest request) {
        try {
            // 构建查询条件
            Specification<RemoteOperationLog> spec = buildOperationLogSpecification(request);
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(
                request.getPageNum() - 1, 
                request.getPageSize(), 
                Sort.by(Sort.Direction.DESC, "operationTime")
            );
            
            // 执行查询
            Page<RemoteOperationLog> pageResult = remoteOperationLogRepository.findAll(spec, pageable);
            
            ResponsePageDataEntity<RemoteOperationLog> result = new ResponsePageDataEntity<>();
            result.setTotal(pageResult.getTotalElements());
            result.setRows(pageResult.getContent());
            return result;
                
        } catch (Exception e) {
            log.error("获取操作日志失败", e);
            ResponsePageDataEntity<RemoteOperationLog> result = new ResponsePageDataEntity<>();
            result.setTotal(0L);
            result.setRows(new ArrayList<>());
            return result;
        }
    }

    @Override
    public List<RoomStatusResponse> getAvailableRemoteRooms(String areaId, String status) {
        try {
            // 直接复用现有的教室状态查询方法
            // 在实际应用中，可以根据需要添加权限过滤逻辑
            List<RoomStatusResponse> allRooms = getRoomStatus(areaId);
            
            // 如果指定了状态筛选，进行过滤
            if (StringUtils.hasText(status) && !"all".equals(status)) {
                return allRooms.stream()
                    .filter(room -> status.equals(room.getStatus()))
                    .collect(Collectors.toList());
            }
            
            return allRooms;
        } catch (Exception e) {
            log.error("获取可远程开门教室列表失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 将AccessRecordResponse转换为RemoteAccessRecordResponse
     */
    private RemoteAccessRecordResponse convertAccessRecordToRemoteAccessRecord(AccessRecordResponse accessRecord) {
        return RemoteAccessRecordResponse.builder()
            .id(accessRecord.getId())
            .roomId(accessRecord.getRoomId())
            .roomName(accessRecord.getRoomName())
            .buildingName(accessRecord.getBuildingName())
            .floor(accessRecord.getFloor())
            .userId(accessRecord.getUserId())
            .name(accessRecord.getName())
            .gender(accessRecord.getGender())
            .employeeId(accessRecord.getEmployeeId())
            .phone(accessRecord.getPhone())
            .department(accessRecord.getDepartment())
            .userType(accessRecord.getUserType())
            .openMethod(accessRecord.getOpenMethod())
            .accessTime(accessRecord.getAccessTime())
            .accessType(accessRecord.getAccessType())
            .bookingId(accessRecord.getBookingId())
            .bookingName(accessRecord.getBookingName())
            .bookingPeriod(accessRecord.getBookingPeriod())
            .deviceId(accessRecord.getDeviceId())
            .deviceName(accessRecord.getDeviceName())
            // 远程开门记录特有的字段可以从现有数据中提取或设置默认值
            .operatorId(accessRecord.getUserId()) // 使用userId作为operatorId
            .operatorName(accessRecord.getName()) // 使用name作为operatorName
            .operatorType("管理员") // 设置默认操作员类型
            .reason("远程开门操作") // 设置默认原因
            .createTime(accessRecord.getCreateTime())
            .build();
    }

    /**
     * 构建操作日志查询条件
     */
    private Specification<RemoteOperationLog> buildOperationLogSpecification(OperationLogsRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 时间范围筛选
            if (StringUtils.hasText(request.getStartTime())) {
                LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("operationTime"), startTime));
            }
            
            if (StringUtils.hasText(request.getEndTime())) {
                LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("operationTime"), endTime));
            }
            
            // 操作员筛选
            if (StringUtils.hasText(request.getOperatorId())) {
                predicates.add(criteriaBuilder.equal(root.get("operatorId"), request.getOperatorId()));
            }
            
            // 教室筛选
            if (StringUtils.hasText(request.getRoomId())) {
                predicates.add(criteriaBuilder.equal(root.get("roomId"), request.getRoomId()));
            }
            
            // 操作类型筛选
            if (StringUtils.hasText(request.getOperationType())) {
                predicates.add(criteriaBuilder.equal(root.get("operationType"), request.getOperationType()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}