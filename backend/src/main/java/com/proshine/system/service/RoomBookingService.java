package com.proshine.system.service;

import com.proshine.system.dto.*;
import com.proshine.system.dto.request.AccessRecordsRequest;
import com.proshine.system.dto.request.ExportAccessRecordsRequest;
import com.proshine.system.dto.response.AccessRecordResponse;
import com.proshine.system.dto.response.AccessStatsResponse;
import com.proshine.system.dto.response.ExportResponse;
import com.proshine.system.dto.response.RoomStatusResponse;
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

    /**
     * 获取待审批列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<ApprovalListResponse> getPendingApprovals(ApprovalListRequest request);

    /**
     * 获取全部审批列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<ApprovalListResponse> getAllApprovals(ApprovalListRequest request);

    /**
     * 审批预约
     * 
     * @param bookingId 预约ID
     * @param request 审批请求参数
     * @return 审批结果
     */
    ApprovalResponse approveBooking(String bookingId, ApprovalRequest request);

    /**
     * 获取审批历史
     * 
     * @param bookingId 预约ID
     * @return 审批历史列表
     */
    List<ApprovalHistoryResponse> getApprovalHistory(String bookingId);

    /**
     * 批量审批
     * 
     * @param request 批量审批请求参数
     * @return 批量审批结果
     */
    BatchApprovalResponse batchApprove(BatchApprovalRequest request);

    /**
     * 获取预约详情
     * 
     * @param bookingId 预约ID
     * @return 预约详情
     */
    BookingDetailResponse getBookingDetail(String bookingId);

    /**
     * 取消预约
     * 
     * @param bookingId 预约ID
     * @param request 取消预约请求参数
     * @return 操作结果消息
     */
    String cancelBooking(String bookingId, CancelBookingRequest request);
    
    /**
     * 获取已通过审批列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<ApprovalListResponse> getApprovedApprovals(ApprovalListRequest request);

    /**
     * 获取已拒绝审批列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<ApprovalListResponse> getRejectedApprovals(ApprovalListRequest request);

    /**
     * 获取教室预约统计列表
     * 
     * @param request 查询请求参数
     * @return 教室预约统计分页数据
     */
    ResponsePageDataEntity<RoomBookingStatsResponse> getRoomBookingStats(RoomBookingStatsRequest request);

    /**
     * 获取教室预约详情
     * 
     * @param request 查询请求参数
     * @return 教室预约详情数据
     */
    RoomBookingDetailsResponse getRoomBookingDetails(RoomBookingDetailsRequest request);

    // ==================== 教室借用记录相关接口 ====================
    
    /**
     * 获取教室借用记录列表
     * 
     * @param request 查询请求参数
     * @return 分页结果
     */
    ResponsePageDataEntity<AccessRecordResponse> getAccessRecords(AccessRecordsRequest request);

    /**
     * 导出教室借用记录
     * 
     * @param request 导出请求参数
     * @return 导出结果
     */
    ExportResponse exportAccessRecords(ExportAccessRecordsRequest request);

    /**
     * 获取教室借用统计信息
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param areaId 区域ID（可选）
     * @return 统计信息
     */
    AccessStatsResponse getAccessStats(String startTime, String endTime, String areaId);

    /**
     * 获取教室实时使用状态
     * 
     * @param areaId 区域ID（可选）
     * @return 教室状态列表
     */
    List<RoomStatusResponse> getRoomStatus(String areaId);
}