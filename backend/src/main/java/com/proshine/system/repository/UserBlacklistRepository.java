package com.proshine.system.repository;

import com.proshine.system.entity.UserBlacklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户黑名单Repository
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface UserBlacklistRepository extends JpaRepository<UserBlacklist, String> {

    /**
     * 根据用户ID和客户域ID查找生效的黑名单记录
     */
    Optional<UserBlacklist> findByUserIdAndCstmIdAndIsActiveTrue(String userId, String cstmId);

    /**
     * 检查用户是否在黑名单中
     */
    boolean existsByUserIdAndCstmIdAndIsActiveTrue(String userId, String cstmId);

    /**
     * 根据客户域ID查找所有生效的黑名单记录
     */
    List<UserBlacklist> findByCstmIdAndIsActiveTrueOrderByCreateTimeDesc(String cstmId);

    /**
     * 分页查询黑名单记录
     */
    @Query("SELECT b FROM UserBlacklist b " +
           "LEFT JOIN SysUser u ON b.userId = u.id " +
           "WHERE b.cstmId = :cstmId AND b.isActive = true " +
           "AND (:name IS NULL OR u.realName LIKE %:name%) " +
           "AND (:employeeId IS NULL OR u.username LIKE %:employeeId%) " +
           "AND (:department IS NULL OR u.departmentName LIKE %:department%) " +
           "ORDER BY b.createTime DESC")
    Page<UserBlacklist> findBlacklistWithConditions(
            @Param("cstmId") String cstmId,
            @Param("name") String name,
            @Param("employeeId") String employeeId,
            @Param("department") String department,
            Pageable pageable);

    /**
     * 根据黑名单类型统计数量
     */
    @Query("SELECT COUNT(b) FROM UserBlacklist b WHERE b.cstmId = :cstmId AND b.blacklistType = :blacklistType AND b.isActive = true")
    Long countByBlacklistType(@Param("cstmId") String cstmId, @Param("blacklistType") String blacklistType);

    /**
     * 移除用户出黑名单（设置为不生效）
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserBlacklist b SET b.isActive = false WHERE b.userId = :userId AND b.cstmId = :cstmId AND b.isActive = true")
    void deactivateByUserId(@Param("userId") String userId, @Param("cstmId") String cstmId);

    /**
     * 统计时间范围内加入黑名单的用户数
     */
    @Query("SELECT COUNT(b) FROM UserBlacklist b WHERE b.cstmId = :cstmId AND b.createTime BETWEEN :startTime AND :endTime")
    Long countBlacklistAdditionsByTimeRange(@Param("cstmId") String cstmId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 获取黑名单用户详细信息（包含用户信息）
     */
    @Query("SELECT b, u FROM UserBlacklist b " +
           "LEFT JOIN SysUser u ON b.userId = u.id " +
           "WHERE b.cstmId = :cstmId AND b.isActive = true " +
           "ORDER BY b.createTime DESC")
    List<Object[]> findBlacklistWithUserInfo(@Param("cstmId") String cstmId);

    /**
     * 根据条件查询黑名单（分页）
     */
    @Query("SELECT b FROM UserBlacklist b " +
           "LEFT JOIN SysUser u ON b.userId = u.id " +
           "WHERE b.cstmId = :cstmId " +
           "AND (:name IS NULL OR :name = '' OR u.realName LIKE %:name%) " +
           "AND (:employeeId IS NULL OR :employeeId = '' OR u.jobNumber LIKE %:employeeId%) " +
           "AND (:department IS NULL OR :department = '' OR u.departmentName LIKE %:department%) " +
           "ORDER BY b.createTime DESC")
    Page<UserBlacklist> findByCstmIdAndCondition(@Param("cstmId") String cstmId,
                                                @Param("name") String name,
                                                @Param("employeeId") String employeeId,
                                                @Param("department") String department,
                                                Pageable pageable);

    /**
     * 检查用户是否在黑名单中
     */
    boolean existsByUserIdAndCstmId(String userId, String cstmId);

    /**
     * 根据ID和客户域ID查找黑名单记录
     */
    UserBlacklist findByIdAndCstmId(String id, String cstmId);

    /**
     * 根据ID列表和客户域ID查找黑名单记录
     */
    List<UserBlacklist> findByIdInAndCstmId(List<String> ids, String cstmId);
}