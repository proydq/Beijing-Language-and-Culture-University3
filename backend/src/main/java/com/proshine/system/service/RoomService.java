package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.RoomSaveVo;
import com.proshine.system.dto.RoomTypeVo;
import com.proshine.system.dto.RoomVo;
import com.proshine.system.dto.SearchRoomCondition;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 房间管理服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface RoomService {

    /**
     * 分页查询房间列表
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<RoomVo> searchRooms(SearchRoomCondition condition);

    /**
     * 根据ID查询房间详情
     * 
     * @param id 房间ID
     * @return 房间详情
     */
    RoomVo getRoomById(String id);

    /**
     * 保存房间（新增或编辑）
     * 
     * @param roomSaveVo 房间保存VO
     * @return 保存后的房间信息
     */
    RoomVo saveRoom(RoomSaveVo roomSaveVo);

    /**
     * 删除房间
     * 
     * @param id 房间ID
     */
    void deleteRoom(String id);

    /**
     * 批量删除房间
     * 
     * @param ids 房间ID列表
     */
    void batchDeleteRooms(List<String> ids);

    /**
     * 导入房间数据
     * 
     * @param file Excel文件
     * @return 导入结果信息
     */
    String importRooms(MultipartFile file);

    /**
     * 导出房间数据
     * 
     * @param condition 查询条件
     * @param response HTTP响应
     */
    void exportRooms(SearchRoomCondition condition, HttpServletResponse response);

    /**
     * 获取房屋类型列表
     * 
     * @return 房屋类型列表
     */
    List<RoomTypeVo> getRoomTypes();

    /**
     * 校验房间唯一性
     * 
     * @param roomName 房屋名称
     * @param roomNo 房间号码
     * @param excludeId 排除的房间ID（编辑时使用）
     * @return 是否唯一
     */
    boolean checkRoomUniqueness(String roomName, String roomNo, String excludeId);
}