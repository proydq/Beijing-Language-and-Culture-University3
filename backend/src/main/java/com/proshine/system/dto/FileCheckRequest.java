package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件检查请求DTO
 * 
 * @author system
 * @since 1.0.0
 */
@Data
public class FileCheckRequest {
    
    /**
     * 文件哈希值（SHA256）
     */
    @NotBlank(message = "文件哈希值不能为空")
    private String fileHash;
    
    /**
     * 文件大小（字节）
     */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;
    
    /**
     * 原始文件名
     */
    @NotBlank(message = "文件名不能为空")
    private String fileName;
    
    /**
     * 文件MIME类型
     */
    private String mimeType;
    
    /**
     * 文件分类
     */
    private String fileCategory;
}