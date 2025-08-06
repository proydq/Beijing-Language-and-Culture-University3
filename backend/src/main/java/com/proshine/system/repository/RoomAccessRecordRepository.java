package com.proshine.system.repository;

import com.proshine.system.entity.RoomAccessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教室借用记录仓库接口
 * 
 * @author system
 * @date 2024-08-04
 */
@Repository
public interface RoomAccessRecordRepository extends JpaRepository<RoomAccessRecord, String>, JpaSpecificationExecutor<RoomAccessRecord> {

    /**
     * 根据时间范围统计记录数
     */
    @Query("SELECT COUNT(r) FROM RoomAccessRecord r WHERE r.accessTime BETWEEN :startTime AND :endTime")
    Long countByAccessTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据时间范围和区域统计记录数
     */
    @Query("SELECT COUNT(r) FROM RoomAccessRecord r WHERE r.accessTime BETWEEN :startTime AND :endTime AND r.roomId IN " +
           "(SELECT room.id FROM Room room WHERE room.roomAreaId = :areaId)")
    Long countByAccessTimeBetweenAndAreaId(@Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime, 
                                          @Param("areaId") String areaId);

    /**
     * 统计使用的教室数量
     */
    @Query("SELECT COUNT(DISTINCT r.roomId) FROM RoomAccessRecord r WHERE r.accessTime BETWEEN :startTime AND :endTime")
    Long countDistinctRoomsByAccessTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计使用的用户数量
     */
    @Query("SELECT COUNT(DISTINCT r.userId) FROM RoomAccessRecord r WHERE r.accessTime BETWEEN :startTime AND :endTime")
    Long countDistinctUsersByAccessTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据教室ID获取最新的通行记录
     */
    @Query("SELECT r FROM RoomAccessRecord r WHERE r.roomId = :roomId ORDER BY r.accessTime DESC")
    List<RoomAccessRecord> findLatestByRoomId(@Param("roomId") String roomId);

    // 注意：远程开门记录相关的查询已经通过现有的通用查询方法实现
    // 只需要在查询时设置 accessType = "远程开门" 即可
}