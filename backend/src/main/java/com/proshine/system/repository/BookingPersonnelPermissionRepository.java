package com.proshine.system.repository;

import com.proshine.system.entity.BookingPersonnelPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 预约人员权限配置数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface BookingPersonnelPermissionRepository extends JpaRepository<BookingPersonnelPermission, String>, JpaSpecificationExecutor<BookingPersonnelPermission> {

    /**
     * 根据客户域查询权限配置列表
     * 
     * @param customerId 客户域ID
     * @return 权限配置列表
     */
    List<BookingPersonnelPermission> findByCustomerIdAndDeletedFalse(String customerId);

    /**
     * 根据ID和客户域查询权限配置
     * 
     * @param id 权限配置ID
     * @param customerId 客户域ID
     * @return 权限配置
     */
    Optional<BookingPersonnelPermission> findByIdAndCustomerIdAndDeletedFalse(String id, String customerId);

    /**
     * 根据用户ID查询该用户拥有的权限配置
     * 
     * @param userId 用户ID
     * @param customerId 客户域ID
     * @return 权限配置列表
     */
    @Query("SELECT DISTINCT p FROM BookingPersonnelPermission p " +
           "JOIN BookingPersonnelPermissionUser pu ON p.id = pu.permissionId " +
           "WHERE pu.userId = :userId AND p.customerId = :customerId AND p.deleted = false AND pu.deleted = false")
    List<BookingPersonnelPermission> findByUserId(@Param("userId") String userId, @Param("customerId") String customerId);

    /**
     * 根据房间ID查询该房间的权限配置
     * 
     * @param roomId 房间ID
     * @param customerId 客户域ID
     * @return 权限配置列表
     */
    @Query("SELECT DISTINCT p FROM BookingPersonnelPermission p " +
           "JOIN BookingPersonnelPermissionRoom pr ON p.id = pr.permissionId " +
           "WHERE pr.roomId = :roomId AND p.customerId = :customerId AND p.deleted = false AND pr.deleted = false")
    List<BookingPersonnelPermission> findByRoomId(@Param("roomId") String roomId, @Param("customerId") String customerId);

    /**
     * 检查用户是否有指定房间的预约权限
     * 
     * @param userId 用户ID
     * @param roomId 房间ID
     * @param customerId 客户域ID
     * @return 是否有权限
     */
    @Query("SELECT COUNT(p) > 0 FROM BookingPersonnelPermission p " +
           "JOIN BookingPersonnelPermissionUser pu ON p.id = pu.permissionId " +
           "JOIN BookingPersonnelPermissionRoom pr ON p.id = pr.permissionId " +
           "WHERE pu.userId = :userId AND pr.roomId = :roomId AND p.customerId = :customerId " +
           "AND p.deleted = false AND pu.deleted = false AND pr.deleted = false")
    boolean hasBookingPermission(@Param("userId") String userId, @Param("roomId") String roomId, @Param("customerId") String customerId);

    /**
     * 逻辑删除权限配置
     * 
     * @param id 权限配置ID
     * @param customerId 客户域ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE BookingPersonnelPermission p SET p.deleted = true WHERE p.id = :id AND p.customerId = :customerId")
    void logicalDeleteById(@Param("id") String id, @Param("customerId") String customerId);
}