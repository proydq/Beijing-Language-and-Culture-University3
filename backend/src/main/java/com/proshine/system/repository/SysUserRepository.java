package com.proshine.system.repository;

import com.proshine.system.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统用户数据访问层
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, String>, JpaSpecificationExecutor<SysUser> {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    Optional<SysUser> findByUsernameAndDeletedFalse(String username);
    
    /**
     * 根据用户名和客户域查询用户
     *
     * @param username 用户名
     * @param customerId 客户域ID
     * @return 用户信息
     */
    Optional<SysUser> findByUsernameAndCustomerIdAndDeletedFalse(String username, String customerId);
    
    /**
     * 根据客户域查询所有用户
     *
     * @param customerId 客户域ID
     * @return 用户列表
     */
    List<SysUser> findByCustomerIdAndDeletedFalse(String customerId);
    
    /**
     * 根据部门ID查询用户
     *
     * @param departmentId 部门ID
     * @param customerId 客户域ID
     * @return 用户列表
     */
    List<SysUser> findByDepartmentIdAndCustomerIdAndDeletedFalse(String departmentId, String customerId);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @param customerId 客户域ID
     * @return 是否存在
     */
    boolean existsByUsernameAndCustomerIdAndDeletedFalse(String username, String customerId);
    
    /**
     * 检查工号是否存在
     *
     * @param jobNumber 工号
     * @param customerId 客户域ID
     * @return 是否存在
     */
    boolean existsByJobNumberAndCustomerIdAndDeletedFalse(String jobNumber, String customerId);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @param customerId 客户域ID
     * @return 用户信息
     */
    Optional<SysUser> findByPhoneAndCustomerIdAndDeletedFalse(String phone, String customerId);

    Optional<SysUser> findByIdAndDeletedFalse(String userId);
}