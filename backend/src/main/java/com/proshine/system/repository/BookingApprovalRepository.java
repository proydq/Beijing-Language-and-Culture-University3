package com.proshine.system.repository;

import com.proshine.system.entity.BookingApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预约审批记录Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface BookingApprovalRepository extends JpaRepository<BookingApproval, String>, JpaSpecificationExecutor<BookingApproval> {
    
    /**
     * 根据预约ID查询审批历史记录
     * 
     * @param bookingId 预约ID
     * @return 审批历史记录列表
     */
    List<BookingApproval> findByBookingIdOrderByApprovalTimeAsc(String bookingId);
    
    /**
     * 根据预约ID查询审批记录，按审批层级排序
     * 
     * @param bookingId 预约ID
     * @return 审批记录列表，按审批层级升序排列
     */
    List<BookingApproval> findByBookingIdOrderByApprovalLevel(String bookingId);
    
    /**
     * 根据预约ID和客户域ID查询审批历史记录
     * 
     * @param bookingId 预约ID
     * @param cstmId 客户域ID
     * @return 审批历史记录列表
     */
    List<BookingApproval> findByBookingIdAndCstmIdOrderByApprovalTimeAsc(String bookingId, String cstmId);
    
    /**
     * 根据审批人ID查询待审批记录数量
     * 
     * @param approverId 审批人ID
     * @param cstmId 客户域ID
     * @return 待审批记录数量
     */
    @Query("SELECT COUNT(ba) FROM BookingApproval ba WHERE ba.approverId = :approverId AND ba.cstmId = :cstmId")
    Long countPendingApprovalsByApprover(@Param("approverId") String approverId, @Param("cstmId") String cstmId);
}