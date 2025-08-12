package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.service.ViolationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 违规设置控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("violation-settings")
@RequiredArgsConstructor
public class ViolationSettingController {

    private final ViolationSettingService violationSettingService;

    /**
     * 分页查询违规设置
     */
    @PostMapping("search")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<ResponsePageDataEntity<ViolationSettingDto>> searchViolationSettings(
            @Valid @RequestBody SearchViolationCondition condition) {
        ResponsePageDataEntity<ViolationSettingDto> result = violationSettingService.searchViolationSettings(condition);
        return ResponseEntity.success(result);
    }

    /**
     * 获取单个教室违规设置详情
     */
    @GetMapping("{roomId}")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<ViolationSettingDto> getViolationSettingByRoomId(@PathVariable String roomId) {
        ViolationSettingDto result = violationSettingService.getViolationSettingByRoomId(roomId);
        return ResponseEntity.success(result);
    }

    /**
     * 更新单个教室违规设置
     */
    @PutMapping("{roomId}")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Void> updateViolationSetting(
            @PathVariable String roomId,
            @Valid @RequestBody ViolationSettingUpdateRequest request) {
        violationSettingService.updateViolationSetting(roomId, request);
        return ResponseEntity.success();
    }

    /**
     * 批量更新教室违规设置
     */
    @PutMapping("batch")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Map<String, Object>> batchUpdateViolationSettings(
            @Valid @RequestBody ViolationSettingBatchUpdateRequest request) {
        Map<String, Object> result = violationSettingService.batchUpdateViolationSettings(request);
        return ResponseEntity.success(result);
    }

    /**
     * 创建或更新违规设置
     */
    @PostMapping
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Void> createOrUpdateViolationSetting(
            @RequestParam String roomId,
            @RequestParam Integer startTime,
            @RequestParam Integer violationCount) {
        violationSettingService.createOrUpdateViolationSetting(roomId, startTime, violationCount);
        return ResponseEntity.success();
    }

    /**
     * 删除违规设置
     */
    @DeleteMapping("{roomId}")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Void> deleteViolationSetting(@PathVariable String roomId) {
        violationSettingService.deleteViolationSetting(roomId);
        return ResponseEntity.success();
    }

    /**
     * 批量删除违规设置
     */
    @DeleteMapping("batch")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Void> batchDeleteViolationSettings(@RequestBody List<String> roomIds) {
        violationSettingService.batchDeleteViolationSettings(roomIds);
        return ResponseEntity.success();
    }

    /**
     * 检查用户是否应该加入黑名单
     */
    @GetMapping("check-blacklist")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Boolean> shouldAddToBlacklist(
            @RequestParam String userId,
            @RequestParam String roomId) {
        boolean result = violationSettingService.shouldAddToBlacklist(userId, roomId);
        return ResponseEntity.success(result);
    }

    /**
     * 处理用户违规
     */
    @PostMapping("process-violation")
    @PreAuthorize("hasAuthority('VIOLATION_MANAGE') or hasAuthority('SYSTEM_CONFIG')")
    public ResponseEntity<Void> processUserViolation(
            @RequestParam String userId,
            @RequestParam String roomId,
            @RequestParam String bookingId,
            @RequestParam Integer overtimeMinutes) {
        violationSettingService.processUserViolation(userId, roomId, bookingId, overtimeMinutes);
        return ResponseEntity.success();
    }
}