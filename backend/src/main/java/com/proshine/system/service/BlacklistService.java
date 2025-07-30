package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;

/**
 * 黑名单服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface BlacklistService {

    /**
     * 分页查询黑名单
     */
    ResponsePageDataEntity<UserBlacklistDto> searchBlacklist(SearchBlacklistCondition condition);

    /**
     * 添加用户到黑名单
     */
    void addToBlacklist(AddBlacklistRequest request);

    /**
     * 从黑名单中移除用户
     */
    void removeFromBlacklist(String blacklistId);

    /**
     * 批量移除黑名单用户
     */
    void batchRemoveFromBlacklist(java.util.List<String> blacklistIds);

    /**
     * 检查用户是否在黑名单中
     */
    boolean isUserInBlacklist(String userId);

    /**
     * 获取用户违规记录
     */
    ResponsePageDataEntity<UserViolationRecordDto> getUserViolationRecords(String userId, Integer pageNumber, Integer pageSize);
}