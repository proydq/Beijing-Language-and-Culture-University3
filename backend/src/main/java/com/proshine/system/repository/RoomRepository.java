package com.proshine.system.repository;

import com.proshine.system.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * 房间数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, String>, JpaSpecificationExecutor<Room> {

    /**
     * 根据客户域ID查询房间列表
     * 
     * @param cstmId 客户域ID
     * @return 房间列表
     */
    List<Room> findByCstmId(String cstmId);

    /**
     * 根据ID和客户域ID查询房间
     * 
     * @param id 房间ID
     * @param cstmId 客户域ID
     * @return 房间信息
     */
    Optional<Room> findByIdAndCstmId(String id, String cstmId);

    /**
     * 根据房屋名称和房间号码查询房间（用于唯一性校验）
     * 
     * @param roomName 房屋名称
     * @param roomNo 房间号码
     * @param cstmId 客户域ID
     * @return 房间信息
     */
    Optional<Room> findByRoomNameAndRoomNoAndCstmId(String roomName, String roomNo, String cstmId);

    /**
     * 根据房屋名称和房间号码查询房间（排除指定ID，用于编辑时唯一性校验）
     * 
     * @param roomName 房屋名称
     * @param roomNo 房间号码
     * @param cstmId 客户域ID
     * @param excludeId 排除的房间ID
     * @return 房间信息
     */
    Optional<Room> findByRoomNameAndRoomNoAndCstmIdAndIdNot(String roomName, String roomNo, String cstmId, String excludeId);

    /**
     * 根据房间区域ID查询房间数量
     * 
     * @param roomAreaId 房间区域ID
     * @param cstmId 客户域ID
     * @return 房间数量
     */
    long countByRoomAreaIdAndCstmId(String roomAreaId, String cstmId);

    /**
     * 根据房屋类型名称查询房间列表
     * 
     * @param roomTypeName 房屋类型名称
     * @param cstmId 客户域ID
     * @return 房间列表
     */
    List<Room> findByRoomTypeNameAndCstmId(String roomTypeName, String cstmId);

    /**
     * 删除指定客户域的房间
     * 
     * @param id 房间ID
     * @param cstmId 客户域ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Room r WHERE r.id = :id AND r.cstmId = :cstmId")
    void deleteByIdAndCstmId(@Param("id") String id, @Param("cstmId") String cstmId);

    /**
     * 根据房间ID列表和客户域ID查询房间列表
     * 
     * @param ids 房间ID列表
     * @param cstmId 客户域ID
     * @return 房间列表
     */
    @Query("SELECT r FROM Room r WHERE r.id IN :ids AND r.cstmId = :cstmId")
    List<Room> findByIdsAndCstmId(@Param("ids") List<String> ids, @Param("cstmId") String cstmId);

    /**
     * 批量删除指定客户域的房间
     * 
     * @param ids 房间ID列表
     * @param cstmId 客户域ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Room r WHERE r.id IN :ids AND r.cstmId = :cstmId")
    void deleteByIdsAndCstmId(@Param("ids") List<String> ids, @Param("cstmId") String cstmId);

    /**
     * 逻辑删除指定客户域的房间
     *
     * @param ids    房间ID列表
     * @param cstmId 客户域ID
     * @param deleteTime 删除时间戳
     */
    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.isDeleted = true, r.deleteTime = :deleteTime WHERE r.id IN :ids AND r.cstmId = :cstmId")
    void logicalDeleteByIdsAndCstmId(@Param("ids") List<String> ids, @Param("cstmId") String cstmId, @Param("deleteTime") Long deleteTime);

    // ========== 逻辑删除相关方法 ==========

    /**
     * 查询所有已删除的房间（分页）
     * 
     * @param pageable 分页参数
     * @return 已删除房间分页数据
     */
    Page<Room> findByIsDeletedTrue(Pageable pageable);

    /**
     * 查询所有已删除的房间
     * 
     * @return 已删除房间列表
     */
    List<Room> findByIsDeletedTrue();

    /**
     * 根据条件查询已删除的房间
     * 
     * @param keyword 关键词
     * @param roomName 房间名称
     * @param roomAreaId 房间区域ID
     * @param pageable 分页参数
     * @return 已删除房间分页数据
     */
    @Query("SELECT r FROM Room r WHERE r.isDeleted = true " +
           "AND (:keyword IS NULL OR :keyword = '' OR r.roomName LIKE %:keyword% OR r.roomNo LIKE %:keyword%) " +
           "AND (:roomName IS NULL OR :roomName = '' OR r.roomName LIKE %:roomName%) " +
           "AND (:roomAreaId IS NULL OR :roomAreaId = '' OR r.roomAreaId = :roomAreaId)")
    Page<Room> findDeletedRoomsWithConditions(@Param("keyword") String keyword, 
                                            @Param("roomName") String roomName,
                                            @Param("roomAreaId") String roomAreaId, 
                                            Pageable pageable);

    /**
     * 统计已删除房间数量
     * 
     * @return 已删除房间数量
     */
    long countByIsDeletedTrue();

    /**
     * 统计指定时间段内删除的房间数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 删除房间数量
     */
    long countByIsDeletedTrueAndDeleteTimeBetween(Long startTime, Long endTime);

    /**
     * 统计指定时间之后删除的房间数量
     * 
     * @param startTime 开始时间
     * @return 删除房间数量
     */
    long countByIsDeletedTrueAndDeleteTimeGreaterThanEqual(Long startTime);

    /**
     * 查询未删除的房间（用于正常业务查询）
     * 
     * @param cstmId 客户域ID
     * @return 房间列表
     */
    @Query("SELECT r FROM Room r WHERE r.cstmId = :cstmId AND (r.isDeleted IS NULL OR r.isDeleted = false)")
    List<Room> findActiveByCstmId(@Param("cstmId") String cstmId);

    /**
     * 查询未删除的房间（用于正常业务查询）
     * 
     * @param id 房间ID
     * @param cstmId 客户域ID
     * @return 房间信息
     */
    @Query("SELECT r FROM Room r WHERE r.id = :id AND r.cstmId = :cstmId AND (r.isDeleted IS NULL OR r.isDeleted = false)")
    Optional<Room> findActiveByIdAndCstmId(@Param("id") String id, @Param("cstmId") String cstmId);

    /**
     * 分页查询指定房间类型的未删除房间（用于违规设置）
     * 
     * @param cstmId 客户域ID
     * @param roomTypeName 房间类型名称
     * @param roomName 房间名称（可选）
     * @param roomAreaId 房间区域ID（可选）
     * @param pageable 分页参数
     * @return 房间分页数据
     */
    @Query("SELECT r FROM Room r WHERE r.cstmId = :cstmId " +
           "AND r.roomTypeName = :roomTypeName " +
           "AND (r.isDeleted IS NULL OR r.isDeleted = false) " +
           "AND (:roomName IS NULL OR :roomName = '' OR r.roomName LIKE %:roomName%) " +
           "AND (:roomAreaId IS NULL OR :roomAreaId = '' OR r.roomAreaId = :roomAreaId)")
    Page<Room> findActiveRoomsByTypeAndCondition(@Param("cstmId") String cstmId,
                                               @Param("roomTypeName") String roomTypeName,
                                               @Param("roomName") String roomName,
                                               @Param("roomAreaId") String roomAreaId,
                                               Pageable pageable);
}