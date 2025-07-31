package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.user.dto.UserVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 预约人员权限配置服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface BookingPersonnelPermissionService {

    /**
     * 分页查询预约人员权限配置列表
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<BookingPersonnelPermissionDto> searchPermissions(SearchBookingPersonnelPermissionCondition condition);

    /**
     * 新增预约人员权限配置
     * 
     * @param request 保存请求
     */
    void savePermission(BookingPersonnelPermissionSaveRequest request);

    /**
     * 编辑预约人员权限配置
     * 
     * @param id 权限配置ID
     * @param request 保存请求
     */
    void updatePermission(String id, BookingPersonnelPermissionSaveRequest request);

    /**
     * 删除预约人员权限配置
     * 
     * @param id 权限配置ID
     */
    void deletePermission(String id);

    /**
     * 导出预约人员权限配置列表
     * 
     * @param keyword 搜索关键词
     * @param response HTTP响应
     */
    void exportPermissions(String keyword, HttpServletResponse response);

    /**
     * 获取可选用户列表
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<UserVO> getAvailableUsers(SearchAvailableUserCondition condition);

    /**
     * 获取可选房间列表
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<RoomVo> getAvailableRooms(SearchAvailableRoomCondition condition);

    /**
     * 检查用户是否有指定房间的预约权限
     * 
     * @param userId 用户ID
     * @param roomId 房间ID
     * @return 是否有权限
     */
    boolean hasBookingPermission(String userId, String roomId);

    BookingPersonnelPermission getUserPermission(String userId, String roomId);

    /**
     * 根据用户ID获取可预约的房间列表
     * 
     * @param userId 用户ID
     * @return 房间列表
     */
    List<RoomVo> getBookableRoomsByUserId(String userId);
}