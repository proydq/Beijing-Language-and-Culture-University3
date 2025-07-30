package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.*;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlacklistServiceImpl implements BlacklistService {

    private final UserBlacklistRepository userBlacklistRepository;
    private final UserViolationRecordRepository userViolationRecordRepository;
    private final SysUserRepository sysUserRepository;
    private final SysOrganizationRepository sysOrganizationRepository;

    @Override
    public ResponsePageDataEntity<UserBlacklistDto> searchBlacklist(SearchBlacklistCondition condition) {
        String customerId = SecurityUtil.getCustomerId();
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createTime"));
        
        Page<UserBlacklist> blacklistPage = userBlacklistRepository.findByCstmIdAndCondition(
            customerId, condition.getName(), condition.getEmployeeId(), condition.getDepartment(), pageable);
        
        List<UserBlacklistDto> dtoList = blacklistPage.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<UserBlacklistDto> result = new ResponsePageDataEntity<>();
        result.setTotal(blacklistPage.getTotalElements());
        result.setRows(dtoList);
        return result;
    }

    @Override
    @Transactional
    public void addToBlacklist(AddBlacklistRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 检查用户是否已在黑名单中
        if (userBlacklistRepository.existsByUserIdAndCstmId(request.getUserId(), customerId)) {
            throw new RuntimeException("用户已在黑名单中");
        }
        
        // 获取用户信息
        Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerIdAndDeletedFalse(request.getUserId(), customerId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        // 计算违规次数
        int violationCount = userViolationRecordRepository.countByUserIdAndCstmId(request.getUserId(), customerId);
        
        UserBlacklist blacklist = new UserBlacklist();
        blacklist.setId(UUID.randomUUID().toString());
        blacklist.setUserId(request.getUserId());
        blacklist.setReason(request.getReason());
        blacklist.setBlacklistType("MANUAL");
        blacklist.setViolationCount(violationCount);
        blacklist.setCstmId(customerId);
        blacklist.setCreateTime(System.currentTimeMillis());
        
        userBlacklistRepository.save(blacklist);
    }

    @Override
    @Transactional
    public void removeFromBlacklist(String blacklistId) {
        String customerId = SecurityUtil.getCustomerId();
        
        UserBlacklist blacklist = userBlacklistRepository.findByIdAndCstmId(blacklistId, customerId);
        if (blacklist == null) {
            throw new RuntimeException("黑名单记录不存在");
        }
        
        userBlacklistRepository.delete(blacklist);
    }

    @Override
    @Transactional
    public void batchRemoveFromBlacklist(List<String> blacklistIds) {
        String customerId = SecurityUtil.getCustomerId();
        
        List<UserBlacklist> blacklistToRemove = userBlacklistRepository.findByIdInAndCstmId(blacklistIds, customerId);
        if (!blacklistToRemove.isEmpty()) {
            userBlacklistRepository.deleteAll(blacklistToRemove);
        }
    }

    @Override
    public boolean isUserInBlacklist(String userId) {
        String customerId = SecurityUtil.getCustomerId();
        return userBlacklistRepository.existsByUserIdAndCstmId(userId, customerId);
    }

    @Override
    public ResponsePageDataEntity<UserViolationRecordDto> getUserViolationRecords(String userId, Integer pageNumber, Integer pageSize) {
        String customerId = SecurityUtil.getCustomerId();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
            Sort.by(Sort.Direction.DESC, "violationTime"));
        
        Page<UserViolationRecord> recordsPage = userViolationRecordRepository.findByUserIdAndCstmId(userId, customerId, pageable);
        
        List<UserViolationRecordDto> dtoList = recordsPage.getContent().stream()
            .map(this::convertToViolationRecordDto)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<UserViolationRecordDto> result = new ResponsePageDataEntity<>();
        result.setTotal(recordsPage.getTotalElements());
        result.setRows(dtoList);
        return result;
    }

    private UserBlacklistDto convertToDto(UserBlacklist blacklist) {
        String customerId = SecurityUtil.getCustomerId();
        
        UserBlacklistDto dto = new UserBlacklistDto();
        dto.setId(blacklist.getId());
        dto.setUserId(blacklist.getUserId());
        dto.setReason(blacklist.getReason());
        dto.setBlacklistType(blacklist.getBlacklistType());
        dto.setViolationCount(blacklist.getViolationCount());
        
        // 获取用户信息
        Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerIdAndDeletedFalse(blacklist.getUserId(), customerId);
        if (userOpt.isPresent()) {
            SysUser user = userOpt.get();
            dto.setName(user.getRealName());
            dto.setEmployeeId(user.getJobNumber());
            
            // 获取部门信息
            if (user.getDepartmentId() != null) {
                Optional<SysOrganization> orgOpt = sysOrganizationRepository.findByIdAndCstmId(user.getDepartmentId(), customerId);
                if (orgOpt.isPresent()) {
                    dto.setDepartment(orgOpt.get().getName());
                }
            }
        }
        
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (blacklist.getCreateTime() != null) {
            LocalDateTime createDateTime = LocalDateTime.ofEpochSecond(
                blacklist.getCreateTime() / 1000, 0, java.time.ZoneOffset.ofHours(8));
            dto.setCreateTime(createDateTime.format(formatter));
        }
        
        return dto;
    }

    private UserViolationRecordDto convertToViolationRecordDto(UserViolationRecord record) {
        UserViolationRecordDto dto = new UserViolationRecordDto();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setRoomId(record.getRoomId());
        dto.setBookingId(record.getBookingId());
        dto.setOvertimeMinutes(record.getOvertimeMinutes());
        
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (record.getViolationTime() != null) {
            LocalDateTime violationDateTime = LocalDateTime.ofEpochSecond(
                record.getViolationTime() / 1000, 0, java.time.ZoneOffset.ofHours(8));
            dto.setViolationTime(violationDateTime.format(formatter));
        }
        
        return dto;
    }
}