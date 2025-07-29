package com.proshine.system.repository;

import com.proshine.system.entity.BookingTimeRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 预约时间规则Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface BookingTimeRuleRepository extends JpaRepository<BookingTimeRule, String> {

    /**
     * 根据客户域ID和删除状态查询规则列表
     */
    @Query("SELECT r FROM BookingTimeRule r WHERE r.customerId = :customerId AND r.deleted = false")
    Page<BookingTimeRule> findByCustomerIdAndDeletedFalse(@Param("customerId") String customerId, Pageable pageable);

    /**
     * 根据规则名称模糊查询
     */
    @Query("SELECT r FROM BookingTimeRule r WHERE r.customerId = :customerId AND r.deleted = false " +
           "AND (:ruleName IS NULL OR r.ruleName LIKE %:ruleName%)")
    Page<BookingTimeRule> findByConditions(@Param("customerId") String customerId, 
                                          @Param("ruleName") String ruleName, 
                                          Pageable pageable);

    /**
     * 根据ID和客户域ID查询
     */
    @Query("SELECT r FROM BookingTimeRule r WHERE r.id = :id AND r.customerId = :customerId AND r.deleted = false")
    Optional<BookingTimeRule> findByIdAndCustomerIdAndDeletedFalse(@Param("id") String id, @Param("customerId") String customerId);

    /**
     * 根据用户ID查询适用的规则
     */
    @Query("SELECT DISTINCT r FROM BookingTimeRule r " +
           "JOIN r.applicableUsers u " +
           "WHERE u.userId = :userId AND r.customerId = :customerId AND r.deleted = false AND u.deleted = false")
    List<BookingTimeRule> findByUserIdAndCustomerId(@Param("userId") String userId, @Param("customerId") String customerId);

    /**
     * 检查规则名称是否已存在
     */
    @Query("SELECT COUNT(r) > 0 FROM BookingTimeRule r WHERE r.ruleName = :ruleName AND r.customerId = :customerId " +
           "AND r.deleted = false AND (:excludeId IS NULL OR r.id != :excludeId)")
    boolean existsByRuleNameAndCustomerId(@Param("ruleName") String ruleName, 
                                         @Param("customerId") String customerId, 
                                         @Param("excludeId") String excludeId);
}