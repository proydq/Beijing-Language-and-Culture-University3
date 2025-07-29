package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.BatchUpdateContinuousBookingRequest;
import com.proshine.system.dto.ContinuousBookingSettingDto;
import com.proshine.system.dto.ContinuousBookingSettingWithRoomDto;
import com.proshine.system.dto.RoomVo;
import com.proshine.system.dto.SearchContinuousBookingSettingCondition;
import com.proshine.system.dto.SearchRoomCondition;
import com.proshine.system.entity.Area;
import com.proshine.system.entity.ContinuousBookingSetting;
import com.proshine.system.repository.AreaRepository;
import com.proshine.system.repository.ContinuousBookingSettingRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.ContinuousBookingSettingService;
import com.proshine.system.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 连续预约设置服务实现类
 */
@Service
@Slf4j
public class ContinuousBookingSettingServiceImpl implements ContinuousBookingSettingService {

    @Autowired
    private ContinuousBookingSettingRepository continuousBookingSettingRepository;

    @Autowired
    private AreaRepository areaRepository;
    
    @Autowired
    private RoomService roomService;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ContinuousBookingSetting getByRoomId(String roomId) {
        String cstmId = SecurityUtil.getCustomerId();
        Optional<ContinuousBookingSetting> optional = continuousBookingSettingRepository.findByRoomIdAndCstmId(roomId, cstmId);
        
        if (optional.isPresent()) {
            return optional.get();
        } else {
            // 如果不存在，创建默认设置
            return createDefaultSetting(roomId);
        }
    }

    @Override
    public List<ContinuousBookingSetting> getAllSettings() {
        String cstmId = SecurityUtil.getCustomerId();
        return continuousBookingSettingRepository.findByCstmId(cstmId);
    }
    
    @Override
    public ResponsePageDataEntity<ContinuousBookingSettingWithRoomDto> searchContinuousBookingSettings(SearchContinuousBookingSettingCondition condition) {
        // 构建房间查询条件
        SearchRoomCondition roomCondition = new SearchRoomCondition();
        roomCondition.setPageNumber(condition.getPageNumber());
        roomCondition.setPageSize(condition.getPageSize());
        roomCondition.setRoomAreaId(condition.getAreaId());
        roomCondition.setRoomName(condition.getRoomName());
        roomCondition.setRoomNo(condition.getRoomNo());
        roomCondition.setRoomTypeName(condition.getRoomTypeName());
        roomCondition.setRoomAreaName(condition.getRoomAreaName());
        roomCondition.setRoomCode(condition.getRoomCode());
        
        // 查询房间列表
        ResponsePageDataEntity<RoomVo> roomPage = roomService.searchRooms(roomCondition);
        
        // 获取房间ID列表
        List<String> roomIds = roomPage.getRows().stream()
                .map(RoomVo::getId)
                .collect(Collectors.toList());
        
        // 查询这些房间的连续预约设置
        String cstmId = SecurityUtil.getCustomerId();
        List<ContinuousBookingSetting> settings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomIds)) {
            settings = continuousBookingSettingRepository.findByRoomIdInAndCstmId(roomIds, cstmId);
        }
        
        // 构建设置映射
        Map<String, ContinuousBookingSetting> settingMap = new HashMap<>();
        for (ContinuousBookingSetting setting : settings) {
            settingMap.put(setting.getRoomId(), setting);
        }
        
        // 转换为结果DTO
        List<ContinuousBookingSettingWithRoomDto> resultList = new ArrayList<>();
        for (RoomVo room : roomPage.getRows()) {
            ContinuousBookingSettingWithRoomDto dto = convertToDto(room, settingMap.get(room.getId()));
            resultList.add(dto);
        }
        
        // 构建返回结果
        ResponsePageDataEntity<ContinuousBookingSettingWithRoomDto> result = new ResponsePageDataEntity<>();
        result.setTotal(roomPage.getTotal());
        result.setRows(resultList);
        
        return result;
    }

    @Override
    @Transactional
    public ContinuousBookingSetting updateSetting(String roomId, ContinuousBookingSettingDto dto) {
        String cstmId = SecurityUtil.getCustomerId();
        Optional<ContinuousBookingSetting> optional = continuousBookingSettingRepository.findByRoomIdAndCstmId(roomId, cstmId);
        
        ContinuousBookingSetting setting;
        if (optional.isPresent()) {
            setting = optional.get();
        } else {
            // 如果不存在，创建新的设置
            setting = new ContinuousBookingSetting();
            setting.setRoomId(roomId);
            setting.setCstmId(cstmId);
        }
        
        // 更新设置信息
        if (dto.getContinuousDays() != null) {
            setting.setContinuousDays(dto.getContinuousDays());
        }
        if (dto.getContinuousType() != null) {
            setting.setContinuousType(dto.getContinuousType());
        }
        if (dto.getIsUnlimited() != null) {
            setting.setIsUnlimited(dto.getIsUnlimited());
        }
        if (dto.getCanContinuous() != null) {
            setting.setCanContinuous(dto.getCanContinuous());
        }
        
        return continuousBookingSettingRepository.save(setting);
    }

    @Override
    @Transactional
    public void batchUpdateSettings(BatchUpdateContinuousBookingRequest request) {
        if (CollectionUtils.isEmpty(request.getRoomIds())) {
            throw new RuntimeException("房间ID列表不能为空");
        }
        
        String cstmId = SecurityUtil.getCustomerId();
        Long updateTime = System.currentTimeMillis();
        
        // 查询已存在的设置
        List<ContinuousBookingSetting> existingSettings = continuousBookingSettingRepository
                .findByRoomIdInAndCstmId(request.getRoomIds(), cstmId);
        
        List<String> existingRoomIds = new ArrayList<>();
        for (ContinuousBookingSetting setting : existingSettings) {
            existingRoomIds.add(setting.getRoomId());
        }
        
        // 批量更新已存在的设置
        if (!CollectionUtils.isEmpty(existingRoomIds)) {
            continuousBookingSettingRepository.batchUpdateByRoomIds(
                    existingRoomIds,
                    request.getContinuousDays(),
                    request.getContinuousType(),
                    request.getIsUnlimited(),
                    request.getCanContinuous(),
                    cstmId,
                    updateTime
            );
        }
        
        // 为不存在设置的房间创建新设置
        List<ContinuousBookingSetting> newSettings = new ArrayList<>();
        for (String roomId : request.getRoomIds()) {
            if (!existingRoomIds.contains(roomId)) {
                ContinuousBookingSetting newSetting = new ContinuousBookingSetting();
                newSetting.setRoomId(roomId);
                newSetting.setContinuousDays(request.getContinuousDays());
                newSetting.setContinuousType(request.getContinuousType());
                newSetting.setIsUnlimited(request.getIsUnlimited());
                newSetting.setCanContinuous(request.getCanContinuous());
                newSetting.setCstmId(cstmId);
                newSettings.add(newSetting);
            }
        }
        
        if (!CollectionUtils.isEmpty(newSettings)) {
            continuousBookingSettingRepository.saveAll(newSettings);
        }
    }

    @Override
    public List<Area> getFloorOptions() {
        String cstmId = SecurityUtil.getCustomerId();
        // 查询类型为楼层的区域
        return areaRepository.findByCstmIdAndType(cstmId, "floor");
    }

    @Override
    @Transactional
    public void deleteByRoomId(String roomId) {
        String cstmId = SecurityUtil.getCustomerId();
        continuousBookingSettingRepository.deleteByRoomIdAndCstmId(roomId, cstmId);
    }
    
    /**
     * 转换房间信息和连续预约设置为DTO
     */
    private ContinuousBookingSettingWithRoomDto convertToDto(RoomVo room, ContinuousBookingSetting setting) {
        ContinuousBookingSettingWithRoomDto dto = new ContinuousBookingSettingWithRoomDto();
        
        // 设置房间信息
        dto.setRoomId(room.getId());
        dto.setRoomName(room.getRoomName());
        dto.setRoomNo(room.getRoomNo());
        dto.setRoomTypeName(room.getRoomTypeName());
        dto.setRoomAreaId(room.getRoomAreaId());
        dto.setRoomAreaName(room.getRoomAreaName());
        dto.setRoomVolume(room.getRoomVolume());
        dto.setRoomCode(room.getRoomCode());
        dto.setRemark(room.getRemark());
        
        // 解析区域信息
        if (StringUtils.hasText(room.getRoomAreaName())) {
            String[] areaParts = room.getRoomAreaName().split("-");
            if (areaParts.length >= 1) {
                dto.setAreaName(areaParts[0].trim());
            }
            if (areaParts.length >= 2) {
                dto.setBuildingName(areaParts[1].trim());
            }
            if (areaParts.length >= 3) {
                dto.setFloorName(areaParts[2].trim());
            }
        }
        
        // 设置连续预约设置信息
        if (setting != null) {
            dto.setContinuousDays(setting.getContinuousDays());
            dto.setContinuousType(setting.getContinuousType());
            dto.setIsUnlimited(setting.getIsUnlimited());
            dto.setCanContinuous(setting.getCanContinuous());
            
            // 格式化时间
            if (setting.getLastUpdateTime() != null) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(setting.getLastUpdateTime()), 
                    ZoneId.systemDefault());
                dto.setLastUpdateTime(dateTime.format(DATE_TIME_FORMATTER));
            }
        }
        
        // 格式化连续预约天数显示
        dto.formatContinuousDaysDisplay();
        
        return dto;
    }

    @Override
    @Transactional
    public ContinuousBookingSetting createDefaultSetting(String roomId) {
        String cstmId = SecurityUtil.getCustomerId();
        
        ContinuousBookingSetting setting = new ContinuousBookingSetting();
        setting.setRoomId(roomId);
        setting.setContinuousDays(7); // 默认7天
        setting.setContinuousType(ContinuousBookingSetting.ContinuousType.DAYS);
        setting.setIsUnlimited(false);
        setting.setCanContinuous(true);
        setting.setCstmId(cstmId);
        
        return continuousBookingSettingRepository.save(setting);
    }
}