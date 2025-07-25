package com.proshine.system.repository;

import com.proshine.system.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色关联数据访问层
 */
@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, String> {
    
    /**
     * 根据用户ID查询用户角色关联
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    List<SysUserRole> findByUserId(String userId);
    
    /**
     * 根据角色ID查询用户角色关联
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    List<SysUserRole> findByRoleId(String roleId);
    
    /**
     * 根据用户ID和角色ID查询关联关系
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 用户角色关联
     */
    SysUserRole findByUserIdAndRoleId(String userId, String roleId);
    
    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否存在关联
     */
    boolean existsByUserIdAndRoleId(String userId, String roleId);
    
    /**
     * 根据用户ID删除所有角色关联
     *
     * @param userId 用户ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SysUserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);
    
    /**
     * 根据角色ID删除所有用户关联
     *
     * @param roleId 角色ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SysUserRole ur WHERE ur.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") String roleId);
    
    /**
     * 批量保存用户角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sys_user_role (id, user_id, role_id, create_time) VALUES (REPLACE(UUID(), '-', ''), :userId, :roleId, NOW())", nativeQuery = true)
    void batchInsertUserRoles(@Param("userId") String userId, @Param("roleId") String roleId);
}