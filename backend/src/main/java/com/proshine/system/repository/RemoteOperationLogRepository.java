package com.proshine.system.repository;

import com.proshine.system.entity.RemoteOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 远程操作日志Repository接口
 * 
 * @author system
 * @date 2024-08-05
 */
@Repository
public interface RemoteOperationLogRepository extends JpaRepository<RemoteOperationLog, String>, JpaSpecificationExecutor<RemoteOperationLog> {

    /**
     * 根据操作员ID查询操作日志
     */
    Page<RemoteOperationLog> findByOperatorIdOrderByOperationTimeDesc(String operatorId, Pageable pageable);

    /**
     * 根据教室ID查询操作日志
     */
    Page<RemoteOperationLog> findByRoomIdOrderByOperationTimeDesc(String roomId, Pageable pageable);

    /**
     * 根据时间范围查询操作日志
     */
    @Query("SELECT log FROM RemoteOperationLog log WHERE log.operationTime BETWEEN :startTime AND :endTime ORDER BY log.operationTime DESC")
    List<RemoteOperationLog> findByOperationTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计今日操作次数
     */
    @Query("SELECT COUNT(log) FROM RemoteOperationLog log WHERE DATE(log.operationTime) = CURRENT_DATE")
    Long countTodayOperations();

    /**
     * 统计指定时间范围内的操作次数
     */
    @Query("SELECT COUNT(log) FROM RemoteOperationLog log WHERE log.operationTime BETWEEN :startTime AND :endTime")
    Long countOperationsBetween(@Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);
}