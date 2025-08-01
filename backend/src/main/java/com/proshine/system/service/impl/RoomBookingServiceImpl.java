package com.proshine.system.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
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

        validateBookingRules(userId, request);

        RoomBooking booking = buildBookingEntity(request, customerId, userId);

        processApprovalFlow(booking, request);

        roomBookingRepository.save(booking);
        return buildCreateResponse(booking);
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
            return;
        }
        
        // 保存预约记录以获取ID
        roomBookingRepository.save(booking);
        
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
                    String[] approverIds = level.getApproverIds().split(",");
                    String[] approverNames = level.getApproverNames().split(",");
                    
                    // 为每个预设审批人创建审批记录
                    for (int i = 0; i < approverIds.length; i++) {
                        BookingApproval nextApproval = new BookingApproval();
                        nextApproval.setCstmId(customerId);
                        nextApproval.setBookingId(booking.getId());
                        nextApproval.setApprovalLevel(level.getLevelNumber());
                        nextApproval.setIsFinalApproval(level.getLevelNumber().equals(maxLevel));
                        
                        nextApproval.setApproverId(approverIds[i].trim());
                        nextApproval.setApproverName(i < approverNames.length ? approverNames[i].trim() : "未知");
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
                    String[] approverIds = level.getApproverIds().split(",");
                    String[] approverNames = level.getApproverNames().split(",");
                    
                    // 为每个预设审批人创建审批记录
                    for (int i = 0; i < approverIds.length; i++) {
                        BookingApproval approval = new BookingApproval();
                        approval.setCstmId(customerId);
                        approval.setBookingId(booking.getId());
                        approval.setApprovalLevel(level.getLevelNumber());
                        approval.setIsFinalApproval(level.getLevelNumber().equals(maxLevel));
                        
                        approval.setApproverId(approverIds[i].trim());
                        approval.setApproverName(i < approverNames.length ? approverNames[i].trim() : "未知");
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
        String currentUserId = SecurityUtil.getCurrentUserId();
        
        // 构建分页参数
        Pageable pageable = PageRequest.of(
            request.getPageNumber() - 1, 
            request.getPageSize(), 
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 先查询当前用户审批过的记录（审批时间不为空表示已审批）
        Specification<BookingApproval> approvalSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 当前用户ID过滤
            predicates.add(criteriaBuilder.equal(root.get("approverId"), currentUserId));
            
            // 审批时间不为空（表示已审批）
            predicates.add(criteriaBuilder.isNotNull(root.get("approvalTime")));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 查询当前用户审批过的所有审批记录
        List<BookingApproval> userApprovals = bookingApprovalRepository.findAll(approvalSpec);
        
        // 提取预约ID列表
        Set<String> bookingIds = userApprovals.stream()
            .map(BookingApproval::getBookingId)
            .collect(Collectors.toSet());
        
        if (bookingIds.isEmpty()) {
            // 如果当前用户没有审批过任何记录，返回空结果
            ResponsePageDataEntity<ApprovalListResponse> result = new ResponsePageDataEntity<>();
            result.setRows(new ArrayList<>());
            result.setTotal(0L);
            return result;
        }
        
        // 根据预约ID查询对应的RoomBooking记录
        Specification<RoomBooking> bookingSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域ID过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 预约ID在用户审批过的列表中
            predicates.add(root.get("id").in(bookingIds));
            
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
            response.setApprovalAction(approval.getApprovalAction().getDisplayName());
            response.setApprovalComment(approval.getApprovalComment());
            response.setApprovalTime(approval.getApprovalTime());
            response.setApprovalLevel(approval.getApprovalLevel());
            response.setIsFinalApproval(approval.getIsFinalApproval());
            
            // 设置前端显示字段
            response.setLevelName(approval.getApprovalLevel() == 1 ? "一级审批" : "自动审批");
            response.setApprovers(new String[]{approval.getApproverName()});
            response.setConfirmedApprover(approval.getApproverName());
            
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
}