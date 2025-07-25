package com.proshine.system.repository;

import com.proshine.system.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色权限关联数据访问层
 */
@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, String> {
    
    /**
     * 根据角色ID查询角色权限关联
     *
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    List<SysRolePermission> findByRoleId(String roleId);
    
    /**
     * 根据权限ID查询角色权限关联
     *
     * @param permissionId 权限ID
     * @return 角色权限关联列表
     */
    List<SysRolePermission> findByPermissionId(String permissionId);
    
    /**
     * 根据角色ID和权限ID查询关联关系
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 角色权限关联
     */
    SysRolePermission findByRoleIdAndPermissionId(String roleId, String permissionId);
    
    /**
     * 检查角色是否拥有指定权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否存在关联
     */
    boolean existsByRoleIdAndPermissionId(String roleId, String permissionId);
    
    /**
     * 根据角色ID删除所有权限关联
     *
     * @param roleId 角色ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SysRolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") String roleId);
    
    /**
     * 根据权限ID删除所有角色关联
     *
     * @param permissionId 权限ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SysRolePermission rp WHERE rp.permissionId = :permissionId")
    void deleteByPermissionId(@Param("permissionId") String permissionId);
    
    /**
     * 批量保存角色权限关联
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sys_role_permission (id, role_id, permission_id, create_time) VALUES (REPLACE(UUID(), '-', ''), :roleId, :permissionId, NOW())", nativeQuery = true)
    void batchInsertRolePermissions(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
}