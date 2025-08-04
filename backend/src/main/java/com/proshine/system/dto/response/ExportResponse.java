package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导出响应DTO
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件下载地址
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 导出记录数
     */
    private Integer recordCount;
}