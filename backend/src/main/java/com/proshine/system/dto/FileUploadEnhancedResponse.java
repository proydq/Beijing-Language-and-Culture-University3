package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 增强的文件上传响应DTO
 * 
 * @author system
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadEnhancedResponse {
    
    /**
     * 文件ID
     */
    private String fileId;
    
    /**
     * 访问URL
     */
    private String url;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件大小（格式化）
     */
    private String fileSizeFormatted;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 文件哈希值
     */
    private String fileHash;
    
    /**
     * 是否秒传（文件已存在）
     */
    private Boolean quickUpload;
    
    /**
     * 上传时间
     */
    private String uploadTime;
    
    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}