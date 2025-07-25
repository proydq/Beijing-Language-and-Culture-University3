package com.proshine.system.service;

import com.proshine.system.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    FileUploadResponse uploadImage(MultipartFile file);
}
