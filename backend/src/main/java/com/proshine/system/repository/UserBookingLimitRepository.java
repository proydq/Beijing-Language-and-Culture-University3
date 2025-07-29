package com.proshine.system.repository;

import com.proshine.system.entity.UserBookingLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户预约限制Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface UserBookingLimitRepository extends JpaRepository<UserBookingLimit, String> {

    /**
     * 根据用户ID和客户域ID查询预约限制
     */
    @Query("SELECT l FROM UserBookingLimit l WHERE l.userId = :userId AND l.customerId = :customerId AND l.deleted = false")
    Optional<UserBookingLimit> findByUserIdAndCustomerIdAndDeletedFalse(@Param("userId") String userId, @Param("customerId") String customerId);

    /**
     * 根据用户ID列表查询预约限制
     */
    @Query("SELECT l FROM UserBookingLimit l WHERE l.userId IN :userIds AND l.customerId = :customerId AND l.deleted = false")
    List<UserBookingLimit> findByUserIdsAndCustomerIdAndDeletedFalse(@Param("userIds") List<String> userIds, @Param("customerId") String customerId);

    /**
     * 根据客户域ID查询所有预约限制
     */
    @Query("SELECT l FROM UserBookingLimit l WHERE l.customerId = :customerId AND l.deleted = false")
    List<UserBookingLimit> findByCustomerIdAndDeletedFalse(@Param("customerId") String customerId);

    /**
     * 检查用户是否已有预约限制设置
     */
    @Query("SELECT COUNT(l) > 0 FROM UserBookingLimit l WHERE l.userId = :userId AND l.customerId = :customerId " +
           "AND l.deleted = false AND (:excludeId IS NULL OR l.id != :excludeId)")
    boolean existsByUserIdAndCustomerId(@Param("userId") String userId, 
                                       @Param("customerId") String customerId, 
                                       @Param("excludeId") String excludeId);
}