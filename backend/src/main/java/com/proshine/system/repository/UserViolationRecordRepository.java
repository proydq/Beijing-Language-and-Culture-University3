package com.proshine.system.repository;

import com.proshine.system.entity.UserViolationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户违规记录Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface UserViolationRecordRepository extends JpaRepository<UserViolationRecord, String> {

    /**
     * 根据用户ID和客户域ID查找违规记录
     */
    List<UserViolationRecord> findByUserIdAndCstmIdOrderByViolationTimeDesc(String userId, String cstmId);

    /**
     * 根据用户ID统计未处理的违规次数
     */
    @Query("SELECT COUNT(v) FROM UserViolationRecord v WHERE v.userId = :userId AND v.cstmId = :cstmId AND v.isProcessed = false")
    Long countUnprocessedViolationsByUserId(@Param("userId") String userId, @Param("cstmId") String cstmId);

    /**
     * 根据教室ID统计违规次数
     */
    @Query("SELECT COUNT(v) FROM UserViolationRecord v WHERE v.roomId = :roomId AND v.cstmId = :cstmId AND v.violationTime BETWEEN :startTime AND :endTime")
    Long countViolationsByRoomIdAndTimeRange(@Param("roomId") String roomId, @Param("cstmId") String cstmId, 
                                           @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 分页查询违规记录
     */
    @Query("SELECT v FROM UserViolationRecord v WHERE v.cstmId = :cstmId " +
           "AND (:userId IS NULL OR v.userId = :userId) " +
           "AND (:roomId IS NULL OR v.roomId = :roomId) " +
           "AND (:startTime IS NULL OR v.violationTime >= :startTime) " +
           "AND (:endTime IS NULL OR v.violationTime <= :endTime) " +
           "ORDER BY v.violationTime DESC")
    Page<UserViolationRecord> findViolationRecordsWithConditions(
            @Param("cstmId") String cstmId,
            @Param("userId") String userId,
            @Param("roomId") String roomId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            Pageable pageable);

    /**
     * 获取用户未处理的违规记录
     */
    List<UserViolationRecord> findByUserIdAndCstmIdAndIsProcessedFalseOrderByViolationTimeAsc(String userId, String cstmId);

    /**
     * 批量标记违规记录为已处理
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserViolationRecord v SET v.isProcessed = true WHERE v.userId = :userId AND v.cstmId = :cstmId AND v.isProcessed = false")
    void markViolationsAsProcessed(@Param("userId") String userId, @Param("cstmId") String cstmId);

    /**
     * 统计时间范围内的违规总数
     */
    @Query("SELECT COUNT(v) FROM UserViolationRecord v WHERE v.cstmId = :cstmId AND v.violationTime BETWEEN :startTime AND :endTime")
    Long countViolationsByTimeRange(@Param("cstmId") String cstmId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 获取违规最多的教室排行
     */
    @Query("SELECT v.roomId, COUNT(v) as violationCount FROM UserViolationRecord v WHERE v.cstmId = :cstmId " +
           "AND v.violationTime BETWEEN :startTime AND :endTime GROUP BY v.roomId ORDER BY violationCount DESC")
    List<Object[]> findTopViolationRooms(@Param("cstmId") String cstmId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 根据用户ID和教室ID统计违规次数
     */
    int countByUserIdAndRoomIdAndCstmId(String userId, String roomId, String cstmId);

    /**
     * 根据用户ID统计违规次数
     */
    int countByUserIdAndCstmId(String userId, String cstmId);

    /**
     * 根据用户ID查询违规记录（分页）
     */
    Page<UserViolationRecord> findByUserIdAndCstmId(String userId, String cstmId, Pageable pageable);
}