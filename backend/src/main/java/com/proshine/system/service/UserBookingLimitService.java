package com.proshine.system.service;

import com.proshine.system.dto.UserBookingLimitDto;

import java.util.List;

/**
 * 用户预约限制Service接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface UserBookingLimitService {

    /**
     * 根据用户ID获取预约限制设置
     * 
     * @param userId 用户ID
     * @return 预约限制设置
     */
    UserBookingLimitDto getUserBookingLimit(String userId);

    /**
     * 设置用户预约限制
     * 
     * @param dto 预约限制信息
     * @return 设置后的预约限制
     */
    UserBookingLimitDto setUserBookingLimit(UserBookingLimitDto dto);

    /**
     * 批量设置用户预约限制
     * 
     * @param dtoList 预约限制信息列表
     * @return 设置后的预约限制列表
     */
    List<UserBookingLimitDto> batchSetUserBookingLimits(List<UserBookingLimitDto> dtoList);

    /**
     * 删除用户预约限制设置
     * 
     * @param userId 用户ID
     */
    void deleteUserBookingLimit(String userId);

    /**
     * 获取所有用户的预约限制设置
     * 
     * @return 所有用户的预约限制设置
     */
    List<UserBookingLimitDto> getAllUserBookingLimits();
}