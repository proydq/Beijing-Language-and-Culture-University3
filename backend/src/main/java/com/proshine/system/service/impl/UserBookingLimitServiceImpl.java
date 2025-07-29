package com.proshine.system.service.impl;

import com.proshine.system.dto.UserBookingLimitDto;
import com.proshine.system.entity.SysUser;
import com.proshine.system.entity.UserBookingLimit;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.repository.UserBookingLimitRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.UserBookingLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户预约限制服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class UserBookingLimitServiceImpl implements UserBookingLimitService {

    @Autowired
    private UserBookingLimitRepository limitRepository;

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public UserBookingLimitDto getUserBookingLimit(String userId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            Optional<UserBookingLimit> limitOpt = limitRepository.findByUserIdAndCustomerIdAndDeletedFalse(userId, customerId);
            
            if (limitOpt.isPresent()) {
                return convertToDto(limitOpt.get());
            } else {
                // 如果没有设置，返回默认值
                UserBookingLimitDto dto = new UserBookingLimitDto();
                dto.setUserId(userId);
                
                // 查询用户名
                Optional<SysUser> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    dto.setUserName(userOpt.get().getRealName());
                }
                
                dto.setMaxAdvanceDays(7); // 默认7天
                return dto;
            }
        } catch (Exception e) {
            log.error("查询用户预约限制失败, userId: {}", userId, e);
            throw new RuntimeException("查询用户预约限制失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserBookingLimitDto setUserBookingLimit(UserBookingLimitDto dto) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            String currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserName = SecurityUtil.getCurrentUsername();
            
            // 查询用户信息
            Optional<SysUser> userOpt = userRepository.findById(dto.getUserId());
            if (!userOpt.isPresent()) {
                throw new RuntimeException("用户不存在");
            }
            SysUser user = userOpt.get();
            
            // 查询是否已有设置
            Optional<UserBookingLimit> existingLimitOpt = limitRepository.findByUserIdAndCustomerIdAndDeletedFalse(dto.getUserId(), customerId);
            
            UserBookingLimit limit;
            if (existingLimitOpt.isPresent()) {
                // 更新现有设置
                limit = existingLimitOpt.get();
                limit.setMaxAdvanceDays(dto.getMaxAdvanceDays());
                limit.setUpdateTime(LocalDateTime.now());
            } else {
                // 创建新设置
                limit = new UserBookingLimit();
                limit.setUserId(dto.getUserId());
                limit.setUserName(user.getRealName());
                limit.setMaxAdvanceDays(dto.getMaxAdvanceDays());
                limit.setCreatorId(currentUserId);
                limit.setCreatorName(currentUserName);
                limit.setCustomerId(customerId);
                limit.setCreateTime(LocalDateTime.now());
                limit.setUpdateTime(LocalDateTime.now());
                limit.setDeleted(false);
            }
            
            limit = limitRepository.save(limit);
            return convertToDto(limit);
        } catch (Exception e) {
            log.error("设置用户预约限制失败", e);
            throw new RuntimeException("设置用户预约限制失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<UserBookingLimitDto> batchSetUserBookingLimits(List<UserBookingLimitDto> dtoList) {
        try {
            List<UserBookingLimitDto> resultList = new ArrayList<>();
            
            for (UserBookingLimitDto dto : dtoList) {
                UserBookingLimitDto result = setUserBookingLimit(dto);
                resultList.add(result);
            }
            
            return resultList;
        } catch (Exception e) {
            log.error("批量设置用户预约限制失败", e);
            throw new RuntimeException("批量设置用户预约限制失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUserBookingLimit(String userId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            Optional<UserBookingLimit> limitOpt = limitRepository.findByUserIdAndCustomerIdAndDeletedFalse(userId, customerId);
            
            if (limitOpt.isPresent()) {
                UserBookingLimit limit = limitOpt.get();
                limit.setDeleted(true);
                limit.setUpdateTime(LocalDateTime.now());
                limitRepository.save(limit);
                
                log.info("删除用户预约限制成功, userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("删除用户预约限制失败, userId: {}", userId, e);
            throw new RuntimeException("删除用户预约限制失败: " + e.getMessage());
        }
    }

    @Override
    public List<UserBookingLimitDto> getAllUserBookingLimits() {
        try {
            String customerId = SecurityUtil.getCustomerId();
            List<UserBookingLimit> limits = limitRepository.findByCustomerIdAndDeletedFalse(customerId);
            
            return limits.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有用户预约限制失败", e);
            throw new RuntimeException("查询所有用户预约限制失败: " + e.getMessage());
        }
    }

    /**
     * 转换为DTO
     */
    private UserBookingLimitDto convertToDto(UserBookingLimit limit) {
        UserBookingLimitDto dto = new UserBookingLimitDto();
        dto.setId(limit.getId());
        dto.setUserId(limit.getUserId());
        dto.setUserName(limit.getUserName());
        dto.setMaxAdvanceDays(limit.getMaxAdvanceDays());
        dto.setCreatorId(limit.getCreatorId());
        dto.setCreatorName(limit.getCreatorName());
        dto.setCreateTime(limit.getCreateTime());
        dto.setUpdateTime(limit.getUpdateTime());
        return dto;
    }
}