package com.proshine.system.repository;

import com.proshine.system.entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 房屋借用预约数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, String>, JpaSpecificationExecutor<RoomBooking> {

    /**
     * 根据客户域ID查询预约列表
     * 
     * @param cstmId 客户域ID
     * @return 预约列表
     */
    List<RoomBooking> findByCstmId(String cstmId);

    /**
     * 统计总借用次数
     * 
     * @param cstmId 客户域ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 总借用次数
     */
    @Query("SELECT COUNT(rb) FROM RoomBooking rb WHERE rb.cstmId = :cstmId " +
           "AND (:startTime IS NULL OR rb.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR rb.createTime <= :endTime) " +
           "AND rb.approvalStatus = 'APPROVED'")
    Long countTotalBookings(@Param("cstmId") String cstmId, 
                           @Param("startTime") LocalDateTime startTime, 
                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计教师借用次数
     * 
     * @param cstmId 客户域ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 教师借用次数
     */
    @Query("SELECT COUNT(rb) FROM RoomBooking rb WHERE rb.cstmId = :cstmId " +
           "AND rb.applicantType = '教师' " +
           "AND (:startTime IS NULL OR rb.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR rb.createTime <= :endTime) " +
           "AND rb.approvalStatus = 'APPROVED'")
    Long countTeacherBookings(@Param("cstmId") String cstmId, 
                             @Param("startTime") LocalDateTime startTime, 
                             @Param("endTime") LocalDateTime endTime);

    /**
     * 统计学生借用次数
     * 
     * @param cstmId 客户域ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 学生借用次数
     */
    @Query("SELECT COUNT(rb) FROM RoomBooking rb WHERE rb.cstmId = :cstmId " +
           "AND rb.applicantType = '学生' " +
           "AND (:startTime IS NULL OR rb.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR rb.createTime <= :endTime) " +
           "AND rb.approvalStatus = 'APPROVED'")
    Long countStudentBookings(@Param("cstmId") String cstmId, 
                             @Param("startTime") LocalDateTime startTime, 
                             @Param("endTime") LocalDateTime endTime);

    /**
     * 统计待审批数量
     * 
     * @param cstmId 客户域ID
     * @return 待审批数量
     */
    @Query("SELECT COUNT(rb) FROM RoomBooking rb WHERE rb.cstmId = :cstmId " +
           "AND rb.approvalStatus = 'PENDING'")
    Long countPendingApprovals(@Param("cstmId") String cstmId);

    /**
     * 获取借用数据分布（按申请人类型分组）
     * 
     * @param cstmId 客户域ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分布数据
     */
    @Query("SELECT rb.applicantType as type, COUNT(rb) as count " +
           "FROM RoomBooking rb WHERE rb.cstmId = :cstmId " +
           "AND (:startTime IS NULL OR rb.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR rb.createTime <= :endTime) " +
           "AND rb.approvalStatus = 'APPROVED' " +
           "GROUP BY rb.applicantType")
    List<Object[]> getBookingDistribution(@Param("cstmId") String cstmId, 
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 获取借用趋势数据（按日期分组）
     * 
     * @param cstmId 客户域ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势数据
     */
    @Query("SELECT DATE(rb.createTime) as date, rb.applicantType as type, COUNT(rb) as count " +
           "FROM RoomBooking rb WHERE rb.cstmId = :cstmId " +
           "AND rb.createTime >= :startTime " +
           "AND rb.createTime <= :endTime " +
           "AND rb.approvalStatus = 'APPROVED' " +
           "GROUP BY DATE(rb.createTime), rb.applicantType " +
           "ORDER BY DATE(rb.createTime)")
    List<Object[]> getBookingTrend(@Param("cstmId") String cstmId, 
                                  @Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 根据房间ID查询预约列表
     * 
     * @param roomId 房间ID
     * @param cstmId 客户域ID
     * @return 预约列表
     */
    List<RoomBooking> findByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 根据申请人ID查询预约列表
     * 
     * @param applicantId 申请人ID
     * @param cstmId 客户域ID
     * @return 预约列表
     */
    List<RoomBooking> findByApplicantIdAndCstmId(String applicantId, String cstmId);

    /**
     * 查询指定时间段内的预约冲突
     * 
     * @param roomId 房间ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param cstmId 客户域ID
     * @param excludeId 排除的预约ID（编辑时使用）
     * @return 冲突的预约列表
     */
    @Query("SELECT rb FROM RoomBooking rb WHERE rb.roomId = :roomId " +
           "AND rb.cstmId = :cstmId " +
           "AND rb.approvalStatus IN ('PENDING', 'APPROVED') " +
           "AND rb.usageStatus != 'CANCELLED' " +
           "AND (:excludeId IS NULL OR rb.id != :excludeId) " +
           "AND ((rb.bookingStartTime <= :startTime AND rb.bookingEndTime > :startTime) " +
           "OR (rb.bookingStartTime < :endTime AND rb.bookingEndTime >= :endTime) " +
           "OR (rb.bookingStartTime >= :startTime AND rb.bookingEndTime <= :endTime))")
    List<RoomBooking> findConflictingBookings(@Param("roomId") String roomId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("cstmId") String cstmId,
                                             @Param("excludeId") String excludeId);
}