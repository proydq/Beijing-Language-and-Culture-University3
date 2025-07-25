package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;

import java.util.List;

/**
 * 角色管理服务接口
 */
public interface RoleManagementService {

    /**
     * 分页查询角色列表
     */
    ResponsePageDataEntity<RoleVo> searchRoles(SearchRoleCondition condition);

    /**
     * 根据ID查询角色详情
     */
    RoleDto findById(String id);

    /**
     * 新增或编辑角色
     */
    void save(RoleDto roleDto);

    /**
     * 逻辑删除角色
     */
    void delete(String id);

    /**
     * 获取权限树结构
     */
    List<PermissionNode> getPermissionTree();

    /**
     * 重新分配角色权限
     */
    void assignPermissions(AssignPermissionRequest request);
}
