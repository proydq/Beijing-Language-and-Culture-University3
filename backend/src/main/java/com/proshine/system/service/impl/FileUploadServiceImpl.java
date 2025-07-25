package com.proshine.system.service.impl;

import com.proshine.system.dto.FileUploadResponse;
import com.proshine.system.entity.SysFile;
import com.proshine.system.repository.SysFileRepository;
import com.proshine.system.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired(required = false)
    private SysFileRepository fileRepository;

    @Override
    public FileUploadResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("file empty");
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String newName = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(extension)) {
            newName += "." + extension;
        }
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, newName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("upload fail", e);
        }
        String url = "/uploads/" + newName;
        if (fileRepository != null) {
            SysFile f = new SysFile();
            f.setFileName(newName);
            f.setUrl(url);
            f.setUploadTime(LocalDateTime.now());
            fileRepository.save(f);
        }
        return new FileUploadResponse(url);
    }
}
