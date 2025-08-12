package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 黑名单管理控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    /**
     * 分页查询黑名单
     */
    @PostMapping("search")
    @PreAuthorize("hasAuthority('BLACKLIST_VIEW') or hasAuthority('BLACKLIST_MANAGE')")
    public ResponseEntity<ResponsePageDataEntity<UserBlacklistDto>> searchBlacklist(
            @Valid @RequestBody SearchBlacklistCondition condition) {
        ResponsePageDataEntity<UserBlacklistDto> result = blacklistService.searchBlacklist(condition);
        return ResponseEntity.success(result);
    }

    /**
     * 添加用户到黑名单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('BLACKLIST_ADD') or hasAuthority('BLACKLIST_MANAGE')")
    public ResponseEntity<Void> addToBlacklist(@Valid @RequestBody AddBlacklistRequest request) {
        blacklistService.addToBlacklist(request);
        return ResponseEntity.success();
    }

    /**
     * 从黑名单中移除用户
     */
    @DeleteMapping("{blacklistId}")
    @PreAuthorize("hasAuthority('BLACKLIST_DELETE') or hasAuthority('BLACKLIST_MANAGE')")
    public ResponseEntity<Void> removeFromBlacklist(@PathVariable String blacklistId) {
        blacklistService.removeFromBlacklist(blacklistId);
        return ResponseEntity.success();
    }

    /**
     * 批量移除黑名单用户
     */
    @DeleteMapping("batch")
    @PreAuthorize("hasAuthority('BLACKLIST_DELETE') or hasAuthority('BLACKLIST_MANAGE')")
    public ResponseEntity<Void> batchRemoveFromBlacklist(@RequestBody List<String> blacklistIds) {
        blacklistService.batchRemoveFromBlacklist(blacklistIds);
        return ResponseEntity.success();
    }

    /**
     * 检查用户是否在黑名单中
     */
    @GetMapping("check/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isUserInBlacklist(@PathVariable String userId) {
        boolean result = blacklistService.isUserInBlacklist(userId);
        return ResponseEntity.success(result);
    }

    /**
     * 获取用户违规记录
     */
    @GetMapping("violation-records/{userId}")
    @PreAuthorize("hasAuthority('BLACKLIST_VIEW') or hasAuthority('BLACKLIST_MANAGE')")
    public ResponseEntity<ResponsePageDataEntity<UserViolationRecordDto>> getUserViolationRecords(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponsePageDataEntity<UserViolationRecordDto> result = blacklistService.getUserViolationRecords(userId, pageNumber, pageSize);
        return ResponseEntity.success(result);
    }
}