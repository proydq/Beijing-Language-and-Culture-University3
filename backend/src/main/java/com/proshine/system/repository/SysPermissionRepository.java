package com.proshine.system.repository;

import com.proshine.system.entity.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统权限数据访问层
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, String>, JpaSpecificationExecutor<SysPermission> {
    
    /**
     * 根据权限编码查询权限
     *
     * @param code 权限编码
     * @param customerId 客户域ID
     * @return 权限信息
     */
    Optional<SysPermission> findByCodeAndCustomerIdAndDeletedFalse(String code, String customerId);
    
    /**
     * 根据客户域查询所有权限
     *
     * @param customerId 客户域ID
     * @return 权限列表
     */
    List<SysPermission> findByCustomerIdAndDeletedFalse(String customerId);
    
    /**
     * 根据父级ID查询子权限
     *
     * @param parentId 父级ID
     * @param customerId 客户域ID
     * @return 权限列表
     */
    List<SysPermission> findByParentIdAndCustomerIdAndDeletedFalse(String parentId, String customerId);
    
    /**
     * 根据权限类型查询权限
     *
     * @param type 权限类型
     * @param customerId 客户域ID
     * @return 权限列表
     */
    List<SysPermission> findByTypeAndCustomerIdAndDeletedFalse(SysPermission.Type type, String customerId);
    
    /**
     * 检查权限编码是否存在
     *
     * @param code 权限编码
     * @param customerId 客户域ID
     * @return 是否存在
     */
    boolean existsByCodeAndCustomerIdAndDeletedFalse(String code, String customerId);
    
    /**
     * 根据用户ID查询用户权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Query("SELECT DISTINCT p FROM SysPermission p " +
           "JOIN SysRolePermission rp ON p.id = rp.permissionId " +
           "JOIN SysUserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND p.deleted = false")
    List<SysPermission> findByUserId(@Param("userId") String userId);
    
    /**
     * 根据角色ID查询角色权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Query("SELECT p FROM SysPermission p JOIN SysRolePermission rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId AND p.deleted = false")
    List<SysPermission> findByRoleId(@Param("roleId") String roleId);
}