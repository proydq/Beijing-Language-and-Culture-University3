package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件检查响应DTO
 * 
 * @author system
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCheckResponse {
    
    /**
     * 文件是否已存在
     */
    private Boolean exists;
    
    /**
     * 文件ID（如果存在）
     */
    private String fileId;
    
    /**
     * 文件访问URL（如果存在）
     */
    private String url;
    
    /**
     * 文件信息（如果存在）
     */
    private FileInfo fileInfo;
    
    /**
     * 创建不存在的响应
     */
    public static FileCheckResponse notExists() {
        return new FileCheckResponse(false, null, null, null);
    }
    
    /**
     * 创建已存在的响应
     */
    public static FileCheckResponse exists(String fileId, String url, FileInfo fileInfo) {
        return new FileCheckResponse(true, fileId, url, fileInfo);
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private String originalName;
        private Long fileSize;
        private String mimeType;
        private String uploadTime;
        private Integer referenceCount;
    }
}