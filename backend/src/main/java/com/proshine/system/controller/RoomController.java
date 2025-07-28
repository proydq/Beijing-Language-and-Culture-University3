package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.RoomSaveVo;
import com.proshine.system.dto.RoomTypeVo;
import com.proshine.system.dto.RoomVo;
import com.proshine.system.dto.SearchRoomCondition;
import com.proshine.system.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 房间管理控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/api/room")
@Slf4j
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 分页查询房间列表
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<RoomVo>> searchRooms(@RequestBody SearchRoomCondition condition) {
        try {
            log.info("==========/api/room/search=============roomName:{}, roomNo:{}, roomTypeName:{}, pageNumber:{}, pageSize:{}", 
                    condition.getRoomName(), condition.getRoomNo(), condition.getRoomTypeName(), 
                    condition.getPageNumber(), condition.getPageSize());
            ResponsePageDataEntity<RoomVo> result = roomService.searchRooms(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("分页查询房间列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 根据ID查询房间详情
     * 
     * @param id 房间ID
     * @return 房间详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomVo> getRoomById(@PathVariable String id) {
        try {
            log.info("==========/api/room/{} [GET]=============id:{}", id, id);
            RoomVo result = roomService.getRoomById(id);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("查询房间详情失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 保存房间（新增或编辑）
     * 
     * @param roomSaveVo 房间保存VO
     * @return 保存后的房间信息
     */
    @PostMapping
    public ResponseEntity<RoomVo> saveRoom(@Valid @RequestBody RoomSaveVo roomSaveVo) {
        try {
            log.info("==========/api/room [POST]=============roomName:{}, roomNo:{}, roomTypeName:{}", 
                    roomSaveVo.getRoomName(), roomSaveVo.getRoomNo(), roomSaveVo.getRoomTypeName());
            RoomVo result = roomService.saveRoom(roomSaveVo);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("保存房间失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 编辑房间
     * 
     * @param id 房间ID
     * @param roomSaveVo 房间保存VO
     * @return 编辑后的房间信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomVo> updateRoom(@PathVariable String id, @Valid @RequestBody RoomSaveVo roomSaveVo) {
        try {
            log.info("==========/api/room/{} [PUT]=============id:{}, roomName:{}, roomNo:{}", 
                    id, id, roomSaveVo.getRoomName(), roomSaveVo.getRoomNo());
            roomSaveVo.setId(id); // 确保ID正确
            RoomVo result = roomService.saveRoom(roomSaveVo);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("编辑房间失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 删除房间
     * 
     * @param id 房间ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        try {
            log.info("==========/api/room/{} [DELETE]=============id:{}", id, id);
            roomService.deleteRoom(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除房间失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 批量删除房间
     * 
     * @param ids 房间ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteRooms(@RequestBody List<String> ids) {
        try {
            log.info("==========/api/room/batch [DELETE]=============ids:{}", ids);
            roomService.batchDeleteRooms(ids);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("批量删除房间失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取房屋类型列表
     * 
     * @return 房屋类型列表
     */
    @GetMapping("/types")
    public ResponseEntity<List<RoomTypeVo>> getRoomTypes() {
        try {
            log.info("==========/api/room/types [GET]=============");
            List<RoomTypeVo> result = roomService.getRoomTypes();
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取房屋类型列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 校验房间唯一性
     * 
     * @param roomName 房屋名称
     * @param roomNo 房间号码
     * @param excludeId 排除的房间ID（编辑时使用）
     * @return 是否唯一
     */
    @GetMapping("/check-uniqueness")
    public ResponseEntity<Boolean> checkRoomUniqueness(
            @RequestParam String roomName,
            @RequestParam String roomNo,
            @RequestParam(required = false) String excludeId) {
        try {
            log.info("==========/api/room/check-uniqueness [GET]=============roomName:{}, roomNo:{}, excludeId:{}", 
                    roomName, roomNo, excludeId);
            boolean result = roomService.checkRoomUniqueness(roomName, roomNo, excludeId);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("校验房间唯一性失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 导入房间数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/import")
    public ResponseEntity<String> importRooms(@RequestParam("file") MultipartFile file) {
        try {
            log.info("==========/api/room/import [POST]=============fileName:{}", file.getOriginalFilename());
            String result = roomService.importRooms(file);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("导入房间数据失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 导出房间数据
     * 
     * @param condition 查询条件
     * @param response HTTP响应
     */
    @PostMapping("/export")
    public void exportRooms(@RequestBody SearchRoomCondition condition, HttpServletResponse response) {
        try {
            log.info("==========/api/room/export [POST]=============roomName:{}, roomNo:{}", 
                    condition.getRoomName(), condition.getRoomNo());
            roomService.exportRooms(condition, response);
        } catch (Exception e) {
            log.error("导出房间数据失败：", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}