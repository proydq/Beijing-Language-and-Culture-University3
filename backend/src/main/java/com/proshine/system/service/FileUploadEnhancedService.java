package com.proshine.system.service;

import com.proshine.system.dto.FileCheckRequest;
import com.proshine.system.dto.FileCheckResponse;
import com.proshine.system.dto.FileUploadEnhancedResponse;
import com.proshine.system.entity.SysFileEnhanced;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 增强的文件上传服务接口
 * 
 * @author system
 * @since 1.0.0
 */
public interface FileUploadEnhancedService {
    
    /**
     * 检查文件是否已存在
     * 
     * @param request 文件检查请求
     * @return 文件检查响应
     */
    FileCheckResponse checkFile(FileCheckRequest request);
    
    /**
     * 上传文件（支持秒传）
     * 
     * @param file 文件
     * @param fileHash 文件哈希值（可选）
     * @param category 文件分类
     * @param userId 用户ID
     * @return 上传响应
     */
    FileUploadEnhancedResponse uploadFile(MultipartFile file, String fileHash, String category, String userId);
    
    /**
     * 创建文件引用
     * 
     * @param fileId 文件ID
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param fieldName 字段名
     * @param userId 用户ID
     */
    void createFileReference(String fileId, String entityType, String entityId, String fieldName, String userId);
    
    /**
     * 移除文件引用
     * 
     * @param fileId 文件ID
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param fieldName 字段名
     */
    void removeFileReference(String fileId, String entityType, String entityId, String fieldName);
    
    /**
     * 更新实体的文件引用（先删除旧的，再创建新的）
     * 
     * @param newFileId 新文件ID
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param fieldName 字段名
     * @param userId 用户ID
     */
    void updateFileReference(String newFileId, String entityType, String entityId, String fieldName, String userId);
    
    /**
     * 清理未使用的文件
     * 
     * @param days 超过多少天未使用
     * @return 清理的文件数量
     */
    int cleanUnusedFiles(int days);
    
    /**
     * 获取文件信息
     * 
     * @param fileId 文件ID
     * @return 文件信息
     */
    SysFileEnhanced getFileInfo(String fileId);
    
    /**
     * 计算文件哈希值
     * 
     * @param file 文件
     * @return SHA256哈希值
     */
    String calculateFileHash(MultipartFile file);
    
    /**
     * 获取文件统计信息
     * 
     * @return 统计信息
     */
    List<Object[]> getFileStatistics();
}