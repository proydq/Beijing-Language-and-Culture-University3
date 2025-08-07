package com.proshine.system.repository;

import com.proshine.system.entity.SysFileEnhanced;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 增强文件信息仓库接口
 * 
 * @author system
 * @since 1.0.0
 */
@Repository
public interface SysFileEnhancedRepository extends JpaRepository<SysFileEnhanced, String> {
    
    /**
     * 根据文件哈希值查找文件
     */
    Optional<SysFileEnhanced> findByFileHashAndIsDeletedFalse(String fileHash);
    
    /**
     * 根据文件哈希和大小查找文件
     */
    Optional<SysFileEnhanced> findByFileHashAndFileSizeAndIsDeletedFalse(String fileHash, Long fileSize);
    
    /**
     * 增加文件引用计数
     */
    @Modifying
    @Query("UPDATE SysFileEnhanced f SET f.referenceCount = f.referenceCount + 1 WHERE f.id = :fileId")
    void incrementReferenceCount(@Param("fileId") String fileId);
    
    /**
     * 减少文件引用计数
     */
    @Modifying
    @Query("UPDATE SysFileEnhanced f SET f.referenceCount = f.referenceCount - 1 WHERE f.id = :fileId AND f.referenceCount > 0")
    void decrementReferenceCount(@Param("fileId") String fileId);
    
    /**
     * 更新最后访问时间
     */
    @Modifying
    @Query("UPDATE SysFileEnhanced f SET f.lastAccessTime = :accessTime WHERE f.id = :fileId")
    void updateLastAccessTime(@Param("fileId") String fileId, @Param("accessTime") LocalDateTime accessTime);
    
    /**
     * 查找未使用的文件（引用计数为0且超过指定天数）
     */
    @Query("SELECT f FROM SysFileEnhanced f WHERE f.referenceCount = 0 AND f.isDeleted = false " +
           "AND f.uploadTime < :beforeDate")
    List<SysFileEnhanced> findUnusedFiles(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 按类别统计文件
     */
    @Query("SELECT f.fileCategory, COUNT(f), SUM(f.fileSize) FROM SysFileEnhanced f " +
           "WHERE f.isDeleted = false GROUP BY f.fileCategory")
    List<Object[]> statisticsByCategory();
    
    /**
     * 按用户统计文件
     */
    @Query("SELECT f.uploadUserId, COUNT(f), SUM(f.fileSize) FROM SysFileEnhanced f " +
           "WHERE f.isDeleted = false GROUP BY f.uploadUserId")
    List<Object[]> statisticsByUser();
}