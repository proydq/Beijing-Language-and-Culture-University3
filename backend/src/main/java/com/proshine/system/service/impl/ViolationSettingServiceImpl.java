package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.*;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.ViolationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViolationSettingServiceImpl implements ViolationSettingService {

    private final ViolationSettingRepository violationSettingRepository;
    private final UserViolationRecordRepository userViolationRecordRepository;
    private final UserBlacklistRepository userBlacklistRepository;
    private final RoomRepository roomRepository;
    private final SysUserRepository sysUserRepository;

    @Override
    public ResponsePageDataEntity<ViolationSettingDto> searchViolationSettings(SearchViolationCondition condition) {
        String customerId = SecurityUtil.getCustomerId();
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());
        
        // 查询所有类型为"教室"的房间
        Page<Room> roomsPage = roomRepository.findActiveRoomsByTypeAndCondition(
            customerId, "教室", condition.getRoomName(), condition.getRoomAreaId(), pageable);
        
        // 获取所有房间的违规设置（如果不存在则使用默认值）
        List<ViolationSettingDto> dtoList = roomsPage.getContent().stream()
            .map(room -> convertRoomToViolationSettingDto(room, customerId))
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<ViolationSettingDto> result = new ResponsePageDataEntity<>();
        result.setTotal(roomsPage.getTotalElements());
        result.setRows(dtoList);
        return result;
    }

    @Override
    public ViolationSettingDto getViolationSettingByRoomId(String roomId) {
        String customerId = SecurityUtil.getCustomerId();
        ViolationSetting setting = violationSettingRepository.findByRoomIdAndCstmId(roomId, customerId)
            .orElse(createDefaultSetting(roomId, customerId));
        return convertToDto(setting);
    }

    @Override
    @Transactional
    public void updateViolationSetting(String roomId, ViolationSettingUpdateRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        
        ViolationSetting setting = violationSettingRepository.findByRoomIdAndCstmId(roomId, customerId)
            .orElse(createDefaultSetting(roomId, customerId));
        
        setting.setStartTimeMinutes(request.getStartTime());
        setting.setViolationCountThreshold(request.getViolationCount());
        setting.setLastUpdateTime(System.currentTimeMillis());
        
        violationSettingRepository.save(setting);
    }

    @Override
    @Transactional
    public Map<String, Object> batchUpdateViolationSettings(ViolationSettingBatchUpdateRequest request) {
        String customerId = SecurityUtil.getCustomerId();
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failedRoomIds = new ArrayList<>();
        
        for (String roomId : request.getRoomIds()) {
            try {
                ViolationSetting setting = violationSettingRepository.findByRoomIdAndCstmId(roomId, customerId)
                    .orElse(createDefaultSetting(roomId, customerId));
                
                setting.setStartTimeMinutes(request.getStartTime());
                setting.setViolationCountThreshold(request.getViolationCount());
                setting.setLastUpdateTime(System.currentTimeMillis());
                
                violationSettingRepository.save(setting);
                successCount++;
            } catch (Exception e) {
                failCount++;
                failedRoomIds.add(roomId);
            }
        }
        
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failedRoomIds", failedRoomIds);
        return result;
    }

    @Override
    @Transactional
    public void createOrUpdateViolationSetting(String roomId, Integer startTime, Integer violationCount) {
        String customerId = SecurityUtil.getCustomerId();
        
        ViolationSetting setting = violationSettingRepository.findByRoomIdAndCstmId(roomId, customerId)
            .orElse(createDefaultSetting(roomId, customerId));
        
        setting.setStartTimeMinutes(startTime);
        setting.setViolationCountThreshold(violationCount);
        setting.setLastUpdateTime(System.currentTimeMillis());
        
        violationSettingRepository.save(setting);
    }

    @Override
    @Transactional
    public void deleteViolationSetting(String roomId) {
        String customerId = SecurityUtil.getCustomerId();
        violationSettingRepository.deleteByRoomIdAndCstmId(roomId, customerId);
    }

    @Override
    @Transactional
    public void batchDeleteViolationSettings(List<String> roomIds) {
        String customerId = SecurityUtil.getCustomerId();
        violationSettingRepository.deleteByRoomIdInAndCstmId(roomIds, customerId);
    }

    @Override
    public boolean shouldAddToBlacklist(String userId, String roomId) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 检查用户是否已在黑名单中
        if (userBlacklistRepository.existsByUserIdAndCstmId(userId, customerId)) {
            return false;
        }
        
        // 获取该房间的违规设置
        ViolationSetting setting = violationSettingRepository.findByRoomIdAndCstmId(roomId, customerId)
            .orElse(createDefaultSetting(roomId, customerId));
        
        // 计算用户在该房间的违规次数
        int violationCount = userViolationRecordRepository.countByUserIdAndRoomIdAndCstmId(userId, roomId, customerId);
        
        return violationCount >= setting.getViolationCountThreshold();
    }

    @Override
    @Transactional
    public void processUserViolation(String userId, String roomId, String bookingId, Integer overtimeMinutes) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 记录违规
        UserViolationRecord record = new UserViolationRecord();
        record.setId(UUID.randomUUID().toString());
        record.setUserId(userId);
        record.setRoomId(roomId);
        record.setBookingId(bookingId);
        record.setOvertimeMinutes(overtimeMinutes);
        record.setViolationTime(System.currentTimeMillis());
        record.setCstmId(customerId);
        record.setCreateTime(System.currentTimeMillis());
        
        userViolationRecordRepository.save(record);
        
        // 检查是否需要加入黑名单
        if (shouldAddToBlacklist(userId, roomId)) {
            addUserToBlacklist(userId, roomId, "累计违规达到阈值，自动加入黑名单");
        }
    }

    private void addUserToBlacklist(String userId, String roomId, String reason) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 检查是否已存在
        if (userBlacklistRepository.existsByUserIdAndCstmId(userId, customerId)) {
            return;
        }
        
        // 获取用户信息
        Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerIdAndDeletedFalse(userId, customerId);
        if (!userOpt.isPresent()) {
            return;
        }
        
        // 计算违规次数
        int violationCount = userViolationRecordRepository.countByUserIdAndCstmId(userId, customerId);
        
        UserBlacklist blacklist = new UserBlacklist();
        blacklist.setId(UUID.randomUUID().toString());
        blacklist.setUserId(userId);
        blacklist.setReason(reason);
        blacklist.setBlacklistType("VIOLATION");
        blacklist.setViolationCount(violationCount);
        blacklist.setCstmId(customerId);
        blacklist.setCreateTime(System.currentTimeMillis());
        
        userBlacklistRepository.save(blacklist);
    }

    private ViolationSetting createDefaultSetting(String roomId, String customerId) {
        ViolationSetting setting = new ViolationSetting();
        setting.setId(UUID.randomUUID().toString());
        setting.setRoomId(roomId);
        setting.setStartTimeMinutes(30); // 默认30分钟
        setting.setViolationCountThreshold(3); // 默认3次
        setting.setCstmId(customerId);
        long now = System.currentTimeMillis();
        setting.setCreateTime(now);
        setting.setLastUpdateTime(now);
        return setting;
    }

    private ViolationSettingDto convertToDto(ViolationSetting setting) {
        String customerId = SecurityUtil.getCustomerId();
        
        ViolationSettingDto dto = new ViolationSettingDto();
        dto.setId(setting.getId());
        dto.setRoomId(setting.getRoomId());
        dto.setStartTime(setting.getStartTimeMinutes());
        dto.setViolationCount(setting.getViolationCountThreshold());
        
        // 获取房间信息
        Optional<Room> roomOpt = roomRepository.findByIdAndCstmId(setting.getRoomId(), customerId);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            dto.setRoomName(room.getRoomName());
            dto.setRoomNo(room.getRoomNo());
            dto.setRoomAreaName(room.getRoomAreaName());
        }
        
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (setting.getLastUpdateTime() != null) {
            LocalDateTime updateDateTime = LocalDateTime.ofEpochSecond(
                setting.getLastUpdateTime() / 1000, 0, java.time.ZoneOffset.ofHours(8));
            dto.setLastUpdateTime(updateDateTime.format(formatter));
        }
        
        return dto;
    }

    private ViolationSettingDto convertRoomToViolationSettingDto(Room room, String customerId) {
        ViolationSettingDto dto = new ViolationSettingDto();
        dto.setRoomId(room.getId());
        dto.setRoomName(room.getRoomName());
        dto.setRoomNo(room.getRoomNo());
        dto.setRoomAreaName(room.getRoomAreaName());
        
        // 查找该房间的违规设置，如果不存在则使用默认值
        Optional<ViolationSetting> settingOpt = violationSettingRepository.findByRoomIdAndCstmId(room.getId(), customerId);
        if (settingOpt.isPresent()) {
            ViolationSetting setting = settingOpt.get();
            dto.setId(setting.getId());
            dto.setStartTime(setting.getStartTimeMinutes());
            dto.setViolationCount(setting.getViolationCountThreshold());
            
            // 格式化时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (setting.getLastUpdateTime() != null) {
                LocalDateTime updateDateTime = LocalDateTime.ofEpochSecond(
                    setting.getLastUpdateTime() / 1000, 0, java.time.ZoneOffset.ofHours(8));
                dto.setLastUpdateTime(updateDateTime.format(formatter));
            }
        } else {
            // 使用默认值
            dto.setId(null); // 表示还未创建违规设置
            dto.setStartTime(30); // 默认30分钟
            dto.setViolationCount(3); // 默认3次
            dto.setLastUpdateTime("未设置");
        }
        
        return dto;
    }
}