package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;

import java.util.Map;

/**
 * 违规设置服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface ViolationSettingService {

    /**
     * 分页查询违规设置
     */
    ResponsePageDataEntity<ViolationSettingDto> searchViolationSettings(SearchViolationCondition condition);

    /**
     * 获取单个教室违规设置详情
     */
    ViolationSettingDto getViolationSettingByRoomId(String roomId);

    /**
     * 更新单个教室违规设置
     */
    void updateViolationSetting(String roomId, ViolationSettingUpdateRequest request);

    /**
     * 批量更新教室违规设置
     */
    Map<String, Object> batchUpdateViolationSettings(ViolationSettingBatchUpdateRequest request);

    /**
     * 创建或更新违规设置
     */
    void createOrUpdateViolationSetting(String roomId, Integer startTime, Integer violationCount);

    /**
     * 删除违规设置
     */
    void deleteViolationSetting(String roomId);

    /**
     * 批量删除违规设置
     */
    void batchDeleteViolationSettings(java.util.List<String> roomIds);

    /**
     * 检查用户是否应该加入黑名单
     */
    boolean shouldAddToBlacklist(String userId, String roomId);

    /**
     * 处理用户违规（记录违规并检查是否需要加入黑名单）
     */
    void processUserViolation(String userId, String roomId, String bookingId, Integer overtimeMinutes);
}