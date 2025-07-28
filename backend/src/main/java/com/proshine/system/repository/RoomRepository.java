package com.proshine.system.repository;

import com.proshine.system.entity.Room;
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
     * 批量删除指定客户域的房间
     * 
     * @param ids 房间ID列表
     * @param cstmId 客户域ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Room r WHERE r.id IN :ids AND r.cstmId = :cstmId")
    void deleteByIdsAndCstmId(@Param("ids") List<String> ids, @Param("cstmId") String cstmId);
}