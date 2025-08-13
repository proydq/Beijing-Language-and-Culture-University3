package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import java.util.List;

/**
 * 管理员管理服务接口
 */
public interface AdminManagementService {
    
    /**
     * 分页查询管理员列表
     *
     * @param condition 查询条件
     * @return 分页管理员信息
     */
    ResponsePageDataEntity<AdminVo> searchAdmins(SearchAdminCondition condition);
    
    /**
     * 添加管理员
     *
     * @param request 添加管理员请求
     * @return 添加结果
     */
    AddAdminResponse addAdmins(AddAdminRequest request);
    
    /**
     * 更新管理员角色
     *
     * @param userId 用户ID
     * @param request 更新角色请求
     */
    void updateAdminRoles(String userId, UpdateAdminRolesRequest request);
    
    /**
     * 删除管理员
     *
     * @param userId 用户ID
     */
    void deleteAdmin(String userId);
    
    /**
     * 批量删除管理员
     *
     * @param request 批量删除请求
     */
    void batchDeleteAdmins(BatchDeleteAdminRequest request);
    
    /**
     * 更新管理员状态
     *
     * @param userId 用户ID
     * @param request 更新状态请求
     */
    void updateAdminStatus(String userId, UpdateAdminStatusRequest request);
    
    /**
     * 获取可分配角色列表
     *
     * @return 可分配角色列表
     */
    List<AvailableRoleVo> getAvailableRoles();
}