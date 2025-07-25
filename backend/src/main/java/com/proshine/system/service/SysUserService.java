package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchUserCondition;
import com.proshine.system.dto.UserVo;
import com.proshine.system.entity.SysUser;

import java.util.List;

/**
 * 系统用户服务接口
 */
public interface SysUserService {
    
    /**
     * 分页查询用户信息
     *
     * @param condition 查询条件
     * @return 分页用户信息
     */
    ResponsePageDataEntity<UserVo> searchUsers(SearchUserCondition condition);
    
    /**
     * 根据ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVo findById(String id);
    
    /**
     * 保存用户信息
     *
     * @param user 用户实体
     * @return 保存后的用户信息
     */
    SysUser save(SysUser user);
    
    /**
     * 更新用户信息
     *
     * @param user 用户实体
     * @return 更新后的用户信息
     */
    SysUser update(SysUser user);
    
    /**
     * 删除用户（逻辑删除）
     *
     * @param ids 用户ID列表
     */
    void delete(List<String> ids);
    
    /**
     * 批量移动到回收站
     *
     * @param ids 用户ID列表
     */
    void moveToRecycleBin(List<String> ids);
    
    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void changeStatus(String userId, SysUser.Status status);
    
    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(String userId, String newPassword);
    
    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(String userId, List<String> roleIds);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @param customerId 客户域ID
     * @return 是否存在
     */
    boolean existsByUsername(String username, String customerId);
    
    /**
     * 检查工号是否存在
     *
     * @param jobNumber 工号
     * @param customerId 客户域ID
     * @return 是否存在
     */
    boolean existsByJobNumber(String jobNumber, String customerId);
}