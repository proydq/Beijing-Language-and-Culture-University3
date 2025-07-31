package com.proshine.system.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.Room;
import com.proshine.system.entity.RoomBooking;
import com.proshine.system.repository.RoomBookingRepository;
import com.proshine.system.repository.RoomRepository;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.BookingPersonnelPermissionService;
import com.proshine.system.service.RoomBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
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
        
        // 转换为响应DTO
        return rooms.stream()
            .map(this::convertToAvailableRoomResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        String userId = SecurityUtil.getCurrentUserId();
        
        // 创建预约实体
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
        
        // 设置紧急程度
        if (StringUtils.hasText(request.getUrgency())) {
            booking.setUrgency(RoomBooking.Urgency.valueOf(request.getUrgency()));
        }
        
        // 处理参与人详情（存储到extend1）
        if (request.getParticipantDetails() != null && !request.getParticipantDetails().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String participantsJson = mapper.writeValueAsString(request.getParticipantDetails());
                booking.setExtend1(participantsJson);
            } catch (Exception e) {
                log.warn("Failed to serialize participant details", e);
            }
        }
        
        // 处理审批人详情（存储到extend2）
        if (request.getApproverDetails() != null && !request.getApproverDetails().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String approversJson = mapper.writeValueAsString(request.getApproverDetails());
                booking.setExtend2(approversJson);
            } catch (Exception e) {
                log.warn("Failed to serialize approver details", e);
            }
        }
        
        // 保存预约
        RoomBooking savedBooking = roomBookingRepository.save(booking);
        
        // 构建响应
        CreateBookingResponse response = new CreateBookingResponse();
        response.setBookingId(savedBooking.getId());
        response.setBookingName(savedBooking.getBookingName());
        response.setApprovalStatus(savedBooking.getApprovalStatus().getDisplayName());
        response.setCreateTime(savedBooking.getCreateTime());
        
        return response;
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
    private AvailableRoomResponse convertToAvailableRoomResponse(Room room) {
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
        response.setAvailable(true); // 简化为都可用，实际需要根据预约情况判断
        
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
}