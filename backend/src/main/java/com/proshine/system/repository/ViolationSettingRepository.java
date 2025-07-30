package com.proshine.system.repository;

import com.proshine.system.entity.ViolationSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 违规设置Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface ViolationSettingRepository extends JpaRepository<ViolationSetting, String> {

    /**
     * 根据教室ID和客户域ID查找违规设置
     */
    Optional<ViolationSetting> findByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 根据教室ID列表和客户域ID查找违规设置
     */
    List<ViolationSetting> findByRoomIdInAndCstmId(List<String> roomIds, String cstmId);

    /**
     * 根据客户域ID查找所有违规设置
     */
    List<ViolationSetting> findByCstmId(String cstmId);

    /**
     * 检查教室是否已有违规设置
     */
    boolean existsByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 根据条件查询违规设置（分页）
     */
    @Query("SELECT v FROM ViolationSetting v " +
           "LEFT JOIN Room r ON v.roomId = r.id " +
           "WHERE v.cstmId = :cstmId " +
           "AND (:roomName IS NULL OR :roomName = '' OR r.roomName LIKE %:roomName%) " +
           "AND (:roomAreaId IS NULL OR :roomAreaId = '' OR r.roomAreaId = :roomAreaId)")
    Page<ViolationSetting> findByCstmIdAndCondition(@Param("cstmId") String cstmId,
                                                   @Param("roomName") String roomName,
                                                   @Param("roomAreaId") String roomAreaId,
                                                   Pageable pageable);

    /**
     * 根据教室ID和客户域ID删除违规设置
     */
    @Modifying
    @Transactional
    void deleteByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 批量删除违规设置
     */
    @Modifying
    @Transactional
    void deleteByRoomIdInAndCstmId(List<String> roomIds, String cstmId);
}