package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for file upload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String url;
}
