package com.proshine.system.service;

import com.proshine.system.dto.*;
import com.proshine.common.response.ResponsePageDataEntity;

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

    /**
     * 获取我的预约列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<BookingListResponse> getMyBookings(MyBookingsRequest request);

    /**
     * 获取全部借用列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<BookingListResponse> getAllBookings(AllBookingsRequest request);

    /**
     * 获取可预约房间列表
     * 
     * @param request 查询请求参数
     * @return 房间列表
     */
    List<AvailableRoomResponse> getAvailableRooms(AvailableRoomsRequest request);

    /**
     * 创建房间预约
     * 
     * @param request 预约请求参数
     * @return 创建结果
     */
    CreateBookingResponse createBooking(CreateBookingRequest request);
}