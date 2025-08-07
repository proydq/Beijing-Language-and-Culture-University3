package com.proshine.system.repository;

import com.proshine.system.entity.SysFileReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文件引用关系仓库接口
 * 
 * @author system
 * @since 1.0.0
 */
@Repository
public interface SysFileReferenceRepository extends JpaRepository<SysFileReference, String> {
    
    /**
     * 查找特定实体的文件引用
     */
    List<SysFileReference> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * 查找特定文件的所有引用
     */
    List<SysFileReference> findByFileId(String fileId);
    
    /**
     * 查找特定引用
     */
    Optional<SysFileReference> findByFileIdAndEntityTypeAndEntityIdAndFieldName(
            String fileId, String entityType, String entityId, String fieldName);
    
    /**
     * 删除特定实体的所有文件引用
     */
    void deleteByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * 删除特定文件的所有引用
     */
    void deleteByFileId(String fileId);
    
    /**
     * 统计文件的引用数量
     */
    @Query("SELECT COUNT(r) FROM SysFileReference r WHERE r.fileId = :fileId")
    Long countByFileId(@Param("fileId") String fileId);
    
    /**
     * 查找特定实体特定字段的文件引用
     */
    Optional<SysFileReference> findByEntityTypeAndEntityIdAndFieldName(
            String entityType, String entityId, String fieldName);
}