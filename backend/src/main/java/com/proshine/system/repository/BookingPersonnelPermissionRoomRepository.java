package com.proshine.system.repository;

import com.proshine.system.entity.BookingPersonnelPermissionRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 预约人员权限配置房间关联数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface BookingPersonnelPermissionRoomRepository extends JpaRepository<BookingPersonnelPermissionRoom, String> {

    /**
     * 根据权限配置ID查询关联房间
     * 
     * @param permissionId 权限配置ID
     * @return 关联房间列表
     */
    List<BookingPersonnelPermissionRoom> findByPermissionIdAndDeletedFalse(String permissionId);

    /**
     * 根据权限配置ID删除所有关联房间
     * 
     * @param permissionId 权限配置ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM BookingPersonnelPermissionRoom pr WHERE pr.permissionId = :permissionId")
    void deleteByPermissionId(@Param("permissionId") String permissionId);

    /**
     * 根据房间ID查询权限配置关联
     * 
     * @param roomId 房间ID
     * @return 权限配置关联列表
     */
    List<BookingPersonnelPermissionRoom> findByRoomIdAndDeletedFalse(String roomId);
}