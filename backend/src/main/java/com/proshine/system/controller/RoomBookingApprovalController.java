package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.service.RoomBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间预约审批管理控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/api/room-booking")
@Slf4j
public class RoomBookingApprovalController {

    @Autowired
    private RoomBookingService roomBookingService;

    /**
     * 获取待审批列表
     * 
     * @param request 查询请求参数
     * @return 待审批列表
     */
    @PostMapping("/approvals/pending")
    public ResponseEntity<ResponsePageDataEntity<ApprovalListResponse>> getPendingApprovals(
            @RequestBody ApprovalListRequest request) {
        try {
            log.info("获取待审批列表，请求参数: {}", request);
            ResponsePageDataEntity<ApprovalListResponse> result = roomBookingService.getPendingApprovals(request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取待审批列表失败", e);
            return ResponseEntity.fail("获取待审批列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取全部审批列表
     * 
     * @param request 查询请求参数
     * @return 全部审批列表
     */
    @PostMapping("/approvals/all")
    public ResponseEntity<ResponsePageDataEntity<ApprovalListResponse>> getAllApprovals(
            @RequestBody ApprovalListRequest request) {
        try {
            log.info("获取全部审批列表，请求参数: {}", request);
            ResponsePageDataEntity<ApprovalListResponse> result = roomBookingService.getAllApprovals(request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取全部审批列表失败", e);
            return ResponseEntity.fail("获取全部审批列表失败: " + e.getMessage());
        }
    }

    /**
     * 审批预约
     * 
     * @param bookingId 预约ID
     * @param request 审批请求参数
     * @return 审批结果
     */
    @PostMapping("/approve/{bookingId}")
    public ResponseEntity<ApprovalResponse> approveBooking(
            @PathVariable String bookingId,
            @RequestBody ApprovalRequest request) {
        try {
            log.info("审批预约，预约ID: {}, 请求参数: {}", bookingId, request);
            ApprovalResponse result = roomBookingService.approveBooking(bookingId, request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("审批预约失败，预约ID: {}", bookingId, e);
            return ResponseEntity.fail("审批预约失败: " + e.getMessage());
        }
    }

    /**
     * 获取预约详情（审批视图）
     * 
     * @param bookingId 预约ID
     * @return 预约详情
     */
    @GetMapping("/approval/detail/{bookingId}")
    public ResponseEntity<BookingDetailResponse> getBookingDetail(@PathVariable String bookingId) {
        try {
            log.info("获取预约详情，预约ID: {}", bookingId);
            BookingDetailResponse result = roomBookingService.getBookingDetail(bookingId);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取预约详情失败，预约ID: {}", bookingId, e);
            return ResponseEntity.fail("获取预约详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取审批历史
     * 
     * @param bookingId 预约ID
     * @return 审批历史列表
     */
    @GetMapping("/approval-history/{bookingId}")
    public ResponseEntity<List<ApprovalHistoryResponse>> getApprovalHistory(@PathVariable String bookingId) {
        try {
            log.info("获取审批历史，预约ID: {}", bookingId);
            List<ApprovalHistoryResponse> result = roomBookingService.getApprovalHistory(bookingId);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取审批历史失败，预约ID: {}", bookingId, e);
            return ResponseEntity.fail("获取审批历史失败: " + e.getMessage());
        }
    }

    /**
     * 批量审批
     * 
     * @param request 批量审批请求参数
     * @return 批量审批结果
     */
    @PostMapping("/batch-approve")
    public ResponseEntity<BatchApprovalResponse> batchApprove(@RequestBody BatchApprovalRequest request) {
        try {
            log.info("批量审批，请求参数: {}", request);
            BatchApprovalResponse result = roomBookingService.batchApprove(request);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("批量审批失败", e);
            return ResponseEntity.fail("批量审批失败: " + e.getMessage());
        }
    }
}