package com.proshine.system.repository;

import com.proshine.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统角色数据访问层
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, String>, JpaSpecificationExecutor<SysRole> {
    
    /**
     * 根据角色编码查询角色
     *
     * @param code 角色编码
     * @param customerId 客户域ID
     * @return 角色信息
     */
    Optional<SysRole> findByCodeAndCustomerIdAndDeletedFalse(String code, String customerId);
    
    /**
     * 根据客户域查询所有角色
     *
     * @param customerId 客户域ID
     * @return 角色列表
     */
    List<SysRole> findByCustomerIdAndDeletedFalse(String customerId);
    
    /**
     * 检查角色编码是否存在
     *
     * @param code 角色编码
     * @param customerId 客户域ID
     * @return 是否存在
     */
    boolean existsByCodeAndCustomerIdAndDeletedFalse(String code, String customerId);
    
    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("SELECT r FROM SysRole r JOIN SysUserRole ur ON r.id = ur.roleId WHERE ur.userId = :userId AND r.deleted = false")
    List<SysRole> findByUserId(@Param("userId") String userId);
}