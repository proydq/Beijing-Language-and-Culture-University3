package com.proshine.system.repository;

import com.proshine.system.entity.BookingTimeRuleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 预约时间规则用户关联Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface BookingTimeRuleUserRepository extends JpaRepository<BookingTimeRuleUser, String> {

    /**
     * 根据规则ID查询关联的用户
     */
    @Query("SELECT u FROM BookingTimeRuleUser u WHERE u.ruleId = :ruleId AND u.deleted = false")
    List<BookingTimeRuleUser> findByRuleIdAndDeletedFalse(@Param("ruleId") String ruleId);

    /**
     * 根据用户ID查询关联的规则
     */
    @Query("SELECT u FROM BookingTimeRuleUser u WHERE u.userId = :userId AND u.deleted = false")
    List<BookingTimeRuleUser> findByUserIdAndDeletedFalse(@Param("userId") String userId);

    /**
     * 根据规则ID删除关联关系（逻辑删除）
     */
    @Modifying
    @Transactional
    @Query("UPDATE BookingTimeRuleUser u SET u.deleted = true WHERE u.ruleId = :ruleId")
    void deleteByRuleId(@Param("ruleId") String ruleId);

    /**
     * 根据规则ID和用户ID列表删除关联关系（逻辑删除）
     */
    @Modifying
    @Transactional
    @Query("UPDATE BookingTimeRuleUser u SET u.deleted = true WHERE u.ruleId = :ruleId AND u.userId IN :userIds")
    void deleteByRuleIdAndUserIds(@Param("ruleId") String ruleId, @Param("userIds") List<String> userIds);

    /**
     * 检查规则和用户的关联关系是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM BookingTimeRuleUser u WHERE u.ruleId = :ruleId AND u.userId = :userId AND u.deleted = false")
    boolean existsByRuleIdAndUserId(@Param("ruleId") String ruleId, @Param("userId") String userId);
}