package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.BookingTimeRuleDto;
import com.proshine.system.dto.SearchBookingTimeRuleCondition;
import com.proshine.system.entity.BookingTimeRule;
import com.proshine.system.entity.BookingTimeRuleUser;
import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.BookingTimeRuleRepository;
import com.proshine.system.repository.BookingTimeRuleUserRepository;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.BookingTimeRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预约时间规则服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class BookingTimeRuleServiceImpl implements BookingTimeRuleService {

    @Autowired
    private BookingTimeRuleRepository ruleRepository;

    @Autowired
    private BookingTimeRuleUserRepository ruleUserRepository;

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public ResponsePageDataEntity<BookingTimeRuleDto> getBookingTimeRuleList(SearchBookingTimeRuleCondition condition) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(
                condition.getPageNumber() - 1, 
                condition.getPageSize(), 
                Sort.by(Sort.Direction.DESC, "createTime")
            );
            
            // 查询数据
            Page<BookingTimeRule> rulePage;
            if (StringUtils.hasText(condition.getRuleName())) {
                rulePage = ruleRepository.findByConditions(customerId, condition.getRuleName(), pageable);
            } else {
                rulePage = ruleRepository.findByCustomerIdAndDeletedFalse(customerId, pageable);
            }
            
            // 转换为DTO
            List<BookingTimeRuleDto> dtoList = rulePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            ResponsePageDataEntity<BookingTimeRuleDto> result = new ResponsePageDataEntity<>();
            result.setRows(dtoList);
            result.setTotal(rulePage.getTotalElements());
            
            return result;
        } catch (Exception e) {
            log.error("查询预约时间规则列表失败", e);
            throw new RuntimeException("查询预约时间规则列表失败: " + e.getMessage());
        }
    }

    @Override
    public BookingTimeRuleDto getBookingTimeRuleById(String id) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            Optional<BookingTimeRule> ruleOpt = ruleRepository.findByIdAndCustomerIdAndDeletedFalse(id, customerId);
            
            if (!ruleOpt.isPresent()) {
                throw new RuntimeException("预约时间规则不存在或已删除");
            }
            
            return convertToDto(ruleOpt.get());
        } catch (Exception e) {
            log.error("查询预约时间规则详情失败, id: {}", id, e);
            throw new RuntimeException("查询预约时间规则详情失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public BookingTimeRuleDto createBookingTimeRule(BookingTimeRuleDto dto) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            String currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserName = SecurityUtil.getCurrentUsername();
            
            // 检查规则名称是否已存在
            if (ruleRepository.existsByRuleNameAndCustomerId(dto.getRuleName(), customerId, null)) {
                throw new RuntimeException("规则名称已存在");
            }
            
            // 创建规则实体
            BookingTimeRule rule = new BookingTimeRule();
            rule.setRuleName(dto.getRuleName());
            rule.setAdvanceBookingDays(dto.getAdvanceBookingDays());
            rule.setCreatorId(currentUserId);
            rule.setCreatorName(currentUserName);
            rule.setCustomerId(customerId);
            rule.setCreateTime(LocalDateTime.now());
            rule.setUpdateTime(LocalDateTime.now());
            rule.setDeleted(false);
            
            // 保存规则
            rule = ruleRepository.save(rule);
            
            // 保存用户关联关系
            if (dto.getApplicableUserIds() != null && !dto.getApplicableUserIds().isEmpty()) {
                saveRuleUsers(rule.getId(), dto.getApplicableUserIds());
            }
            
            return convertToDto(rule);
        } catch (Exception e) {
            log.error("创建预约时间规则失败", e);
            throw new RuntimeException("创建预约时间规则失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public BookingTimeRuleDto updateBookingTimeRule(String id, BookingTimeRuleDto dto) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            Optional<BookingTimeRule> ruleOpt = ruleRepository.findByIdAndCustomerIdAndDeletedFalse(id, customerId);
            
            if (!ruleOpt.isPresent()) {
                throw new RuntimeException("预约时间规则不存在或已删除");
            }
            
            BookingTimeRule rule = ruleOpt.get();
            
            // 检查规则名称是否已存在（排除当前规则）
            if (ruleRepository.existsByRuleNameAndCustomerId(dto.getRuleName(), customerId, id)) {
                throw new RuntimeException("规则名称已存在");
            }
            
            // 更新规则信息
            rule.setRuleName(dto.getRuleName());
            rule.setAdvanceBookingDays(dto.getAdvanceBookingDays());
            rule.setUpdateTime(LocalDateTime.now());
            
            // 保存规则
            rule = ruleRepository.save(rule);
            
            // 删除原有用户关联关系
            ruleUserRepository.deleteByRuleId(id);
            
            // 保存新的用户关联关系
            if (dto.getApplicableUserIds() != null && !dto.getApplicableUserIds().isEmpty()) {
                saveRuleUsers(rule.getId(), dto.getApplicableUserIds());
            }
            
            return convertToDto(rule);
        } catch (Exception e) {
            log.error("更新预约时间规则失败, id: {}", id, e);
            throw new RuntimeException("更新预约时间规则失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteBookingTimeRule(String id) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            Optional<BookingTimeRule> ruleOpt = ruleRepository.findByIdAndCustomerIdAndDeletedFalse(id, customerId);
            
            if (!ruleOpt.isPresent()) {
                throw new RuntimeException("预约时间规则不存在或已删除");
            }
            
            BookingTimeRule rule = ruleOpt.get();
            rule.setDeleted(true);
            rule.setUpdateTime(LocalDateTime.now());
            
            ruleRepository.save(rule);
            
            // 删除用户关联关系
            ruleUserRepository.deleteByRuleId(id);
            
            log.info("删除预约时间规则成功, id: {}", id);
        } catch (Exception e) {
            log.error("删除预约时间规则失败, id: {}", id, e);
            throw new RuntimeException("删除预约时间规则失败: " + e.getMessage());
        }
    }

    @Override
    public List<BookingTimeRuleDto> getBookingTimeRulesByUserId(String userId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            List<BookingTimeRule> rules = ruleRepository.findByUserIdAndCustomerId(userId, customerId);
            
            return rules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户适用的预约时间规则失败, userId: {}", userId, e);
            throw new RuntimeException("查询用户适用的预约时间规则失败: " + e.getMessage());
        }
    }

    /**
     * 保存规则用户关联关系
     */
    private void saveRuleUsers(String ruleId, List<String> userIds) {
        List<BookingTimeRuleUser> ruleUsers = new ArrayList<>();
        
        for (String userId : userIds) {
            // 检查关联关系是否已存在
            if (!ruleUserRepository.existsByRuleIdAndUserId(ruleId, userId)) {
                Optional<SysUser> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    SysUser user = userOpt.get();
                    
                    BookingTimeRuleUser ruleUser = new BookingTimeRuleUser();
                    ruleUser.setRuleId(ruleId);
                    ruleUser.setUserId(userId);
                    ruleUser.setUserName(user.getRealName());
                    ruleUser.setCreateTime(LocalDateTime.now());
                    ruleUser.setDeleted(false);
                    
                    ruleUsers.add(ruleUser);
                }
            }
        }
        
        if (!ruleUsers.isEmpty()) {
            ruleUserRepository.saveAll(ruleUsers);
        }
    }

    /**
     * 转换为DTO
     */
    private BookingTimeRuleDto convertToDto(BookingTimeRule rule) {
        BookingTimeRuleDto dto = new BookingTimeRuleDto();
        dto.setId(rule.getId());
        dto.setRuleName(rule.getRuleName());
        dto.setAdvanceBookingDays(rule.getAdvanceBookingDays());
        dto.setCreatorId(rule.getCreatorId());
        dto.setCreatorName(rule.getCreatorName());
        dto.setCreateTime(rule.getCreateTime());
        dto.setUpdateTime(rule.getUpdateTime());
        
        // 查询关联的用户
        List<BookingTimeRuleUser> ruleUsers = ruleUserRepository.findByRuleIdAndDeletedFalse(rule.getId());
        List<String> userIds = ruleUsers.stream()
            .map(BookingTimeRuleUser::getUserId)
            .collect(Collectors.toList());
        
        // 查询用户详细信息，包含工号
        List<BookingTimeRuleDto.UserInfo> users = ruleUsers.stream()
            .map(ru -> {
                Optional<SysUser> userOpt = userRepository.findByIdAndDeletedFalse(ru.getUserId());
                if (userOpt.isPresent()) {
                    SysUser user = userOpt.get();
                    return new BookingTimeRuleDto.UserInfo(ru.getUserId(), user.getRealName(), user.getJobNumber());
                } else {
                    // 如果用户不存在，使用默认值
                    return new BookingTimeRuleDto.UserInfo(ru.getUserId(), ru.getUserName(), "");
                }
            })
            .collect(Collectors.toList());
        
        dto.setApplicableUserIds(userIds);
        dto.setApplicableUsers(users);
        dto.setBindingUserCount(users.size()); // 设置绑定人数
        
        return dto;
    }
}