package com.proshine.system.repository;

import com.proshine.system.entity.BookingPersonnelPermissionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 预约人员权限配置用户关联数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface BookingPersonnelPermissionUserRepository extends JpaRepository<BookingPersonnelPermissionUser, String> {

    /**
     * 根据权限配置ID查询关联用户
     * 
     * @param permissionId 权限配置ID
     * @return 关联用户列表
     */
    List<BookingPersonnelPermissionUser> findByPermissionIdAndDeletedFalse(String permissionId);

    /**
     * 根据权限配置ID删除所有关联用户
     * 
     * @param permissionId 权限配置ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BookingPersonnelPermissionUser pu WHERE pu.permissionId = :permissionId")
    void deleteByPermissionId(@Param("permissionId") String permissionId);

    /**
     * 根据用户ID查询权限配置关联
     * 
     * @param userId 用户ID
     * @return 权限配置关联列表
     */
    List<BookingPersonnelPermissionUser> findByUserIdAndDeletedFalse(String userId);
}