package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.BatchUpdateContinuousBookingRequest;
import com.proshine.system.dto.ContinuousBookingSettingDto;
import com.proshine.system.dto.ContinuousBookingSettingWithRoomDto;
import com.proshine.system.dto.SearchContinuousBookingSettingCondition;
import com.proshine.system.entity.Area;
import com.proshine.system.entity.ContinuousBookingSetting;
import com.proshine.system.service.ContinuousBookingSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 连续预约设置控制器
 */
@RestController
@RequestMapping("/continuous-booking-settings")
@Slf4j
public class ContinuousBookingSettingController {

    @Autowired
    private ContinuousBookingSettingService continuousBookingSettingService;

    /**
     * 获取教室连续预约设置列表
     *
     * @return 连续预约设置列表
     */
    @GetMapping
    public ResponseEntity<List<ContinuousBookingSetting>> getAllSettings() {
        try {
            log.info("==========/continuous-booking-settings [GET]=============");
            List<ContinuousBookingSetting> settings = continuousBookingSettingService.getAllSettings();
            return ResponseEntity.success(settings);
        } catch (Exception e) {
            log.error("获取教室连续预约设置列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 分页查询连续预约设置（包含房间信息）
     *
     * @param condition 查询条件
     * @return 分页结果
     */
    @PostMapping("/list")
    public ResponseEntity<ResponsePageDataEntity<ContinuousBookingSettingWithRoomDto>> searchContinuousBookingSettings(
            @RequestBody SearchContinuousBookingSettingCondition condition) {
        try {
            log.info("==========/continuous-booking-settings/list [POST]=============areaId:{}, pageNumber:{}, pageSize:{}", 
                    condition.getAreaId(), condition.getPageNumber(), condition.getPageSize());
            ResponsePageDataEntity<ContinuousBookingSettingWithRoomDto> result = 
                    continuousBookingSettingService.searchContinuousBookingSettings(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("分页查询连续预约设置失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 根据房间ID获取连续预约设置
     *
     * @param roomId 房间ID
     * @return 连续预约设置
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ContinuousBookingSetting> getByRoomId(@PathVariable String roomId) {
        try {
            log.info("==========/continuous-booking-settings/{} [GET]=============roomId:{}", roomId, roomId);
            ContinuousBookingSetting setting = continuousBookingSettingService.getByRoomId(roomId);
            return ResponseEntity.success(setting);
        } catch (Exception e) {
            log.error("获取房间连续预约设置失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 更新单个教室连续预约设置
     *
     * @param roomId 房间ID
     * @param dto 设置信息
     * @return 更新结果
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<Void> updateSetting(@PathVariable String roomId, @RequestBody ContinuousBookingSettingDto dto) {
        try {
            log.info("==========/continuous-booking-settings/{} [PUT]=============roomId:{}, continuousDays:{}", 
                    roomId, roomId, dto.getContinuousDays());
            continuousBookingSettingService.updateSetting(roomId, dto);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("更新教室连续预约设置失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 批量更新教室连续预约设置
     *
     * @param request 批量更新请求
     * @return 更新结果
     */
    @PostMapping("/batch-update")
    public ResponseEntity<Void> batchUpdateSettings(@RequestBody BatchUpdateContinuousBookingRequest request) {
        try {
            log.info("==========/continuous-booking-settings/batch-update [POST]=============roomIds:{}, continuousDays:{}", 
                    request.getRoomIds(), request.getContinuousDays());
            continuousBookingSettingService.batchUpdateSettings(request);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("批量更新教室连续预约设置失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取楼层选项列表
     *
     * @return 楼层选项列表
     */
    @GetMapping("/floor-options")
    public ResponseEntity<List<Area>> getFloorOptions() {
        try {
            log.info("==========/continuous-booking-settings/floor-options [GET]=============");
            List<Area> floors = continuousBookingSettingService.getFloorOptions();
            return ResponseEntity.success(floors);
        } catch (Exception e) {
            log.error("获取楼层选项列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 删除房间的连续预约设置
     *
     * @param roomId 房间ID
     * @return 删除结果
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteByRoomId(@PathVariable String roomId) {
        try {
            log.info("==========/continuous-booking-settings/{} [DELETE]=============roomId:{}", roomId, roomId);
            continuousBookingSettingService.deleteByRoomId(roomId);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除房间连续预约设置失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}