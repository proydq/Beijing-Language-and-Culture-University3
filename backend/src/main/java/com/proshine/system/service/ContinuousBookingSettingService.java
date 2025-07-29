package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.BatchUpdateContinuousBookingRequest;
import com.proshine.system.dto.ContinuousBookingSettingDto;
import com.proshine.system.dto.ContinuousBookingSettingWithRoomDto;
import com.proshine.system.dto.SearchContinuousBookingSettingCondition;
import com.proshine.system.entity.Area;
import com.proshine.system.entity.ContinuousBookingSetting;

import java.util.List;

/**
 * 连续预约设置服务接口
 */
public interface ContinuousBookingSettingService {

    /**
     * 根据房间ID获取连续预约设置
     *
     * @param roomId 房间ID
     * @return 连续预约设置
     */
    ContinuousBookingSetting getByRoomId(String roomId);

    /**
     * 获取所有连续预约设置列表
     *
     * @return 连续预约设置列表
     */
    List<ContinuousBookingSetting> getAllSettings();
    
    /**
     * 分页查询连续预约设置（包含房间信息）
     *
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<ContinuousBookingSettingWithRoomDto> searchContinuousBookingSettings(SearchContinuousBookingSettingCondition condition);

    /**
     * 更新单个教室连续预约设置
     *
     * @param roomId 房间ID
     * @param dto 设置信息
     * @return 更新后的设置
     */
    ContinuousBookingSetting updateSetting(String roomId, ContinuousBookingSettingDto dto);

    /**
     * 批量更新教室连续预约设置
     *
     * @param request 批量更新请求
     */
    void batchUpdateSettings(BatchUpdateContinuousBookingRequest request);

    /**
     * 获取楼层选项列表
     *
     * @return 楼层选项列表
     */
    List<Area> getFloorOptions();

    /**
     * 删除房间的连续预约设置
     *
     * @param roomId 房间ID
     */
    void deleteByRoomId(String roomId);

    /**
     * 为新房间创建默认连续预约设置
     *
     * @param roomId 房间ID
     * @return 创建的设置
     */
    ContinuousBookingSetting createDefaultSetting(String roomId);
}