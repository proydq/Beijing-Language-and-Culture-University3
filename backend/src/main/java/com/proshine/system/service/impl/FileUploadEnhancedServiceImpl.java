package com.proshine.system.service.impl;

import com.proshine.common.enums.ErrorCode;
import com.proshine.common.exception.BusinessException;
import com.proshine.system.dto.FileCheckRequest;
import com.proshine.system.dto.FileCheckResponse;
import com.proshine.system.dto.FileUploadEnhancedResponse;
import com.proshine.system.entity.SysFileEnhanced;
import com.proshine.system.entity.SysFileReference;
import com.proshine.system.repository.SysFileEnhancedRepository;
import com.proshine.system.repository.SysFileReferenceRepository;
import com.proshine.system.service.FileUploadEnhancedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 增强的文件上传服务实现
 * 
 * @author system
 * @since 1.0.0
 */
@Slf4j
@Service
@Transactional
public class FileUploadEnhancedServiceImpl implements FileUploadEnhancedService {
    
    @Value("${file.upload.path:./uploads}")
    private String uploadBasePath;
    
    @Value("${file.upload.max-size:10485760}") // 默认10MB
    private Long maxFileSize;
    
    @Value("${file.upload.allowed-types:image/jpeg,image/png,image/gif}")
    private String allowedTypes;
    
    @Autowired
    private SysFileEnhancedRepository fileRepository;
    
    @Autowired
    private SysFileReferenceRepository referenceRepository;
    
    @Override
    public FileCheckResponse checkFile(FileCheckRequest request) {
        // 根据文件哈希和大小查找
        Optional<SysFileEnhanced> fileOpt = fileRepository.findByFileHashAndFileSizeAndIsDeletedFalse(
                request.getFileHash(), request.getFileSize());
        
        if (fileOpt.isPresent()) {
            SysFileEnhanced file = fileOpt.get();
            FileCheckResponse.FileInfo fileInfo = new FileCheckResponse.FileInfo(
                    file.getOriginalName(),
                    file.getFileSize(),
                    file.getMimeType(),
                    file.getUploadTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    file.getReferenceCount()
            );
            return FileCheckResponse.exists(file.getId(), file.getAccessUrl(), fileInfo);
        }
        
        return FileCheckResponse.notExists();
    }
    
    @Override
    public FileUploadEnhancedResponse uploadFile(MultipartFile file, String fileHash, String category, String userId) {
        // 验证文件
        validateFile(file);
        
        // 计算文件哈希（如果前端没有提供）
        if (!StringUtils.hasText(fileHash)) {
            fileHash = calculateFileHash(file);
        }
        
        // 检查文件是否已存在
        Optional<SysFileEnhanced> existingFile = fileRepository.findByFileHashAndFileSizeAndIsDeletedFalse(
                fileHash, file.getSize());
        
        if (existingFile.isPresent()) {
            // 文件已存在，直接返回（秒传）
            SysFileEnhanced sysFile = existingFile.get();
            log.info("文件已存在，执行秒传: fileId={}, hash={}", sysFile.getId(), fileHash);
            
            return FileUploadEnhancedResponse.builder()
                    .fileId(sysFile.getId())
                    .url(sysFile.getAccessUrl())
                    .originalName(file.getOriginalFilename())
                    .fileSize(sysFile.getFileSize())
                    .fileSizeFormatted(FileUploadEnhancedResponse.formatFileSize(sysFile.getFileSize()))
                    .mimeType(sysFile.getMimeType())
                    .fileHash(sysFile.getFileHash())
                    .quickUpload(true)
                    .uploadTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        }
        
        // 文件不存在，执行实际上传
        try {
            SysFileEnhanced sysFile = saveNewFile(file, fileHash, category, userId);
            
            return FileUploadEnhancedResponse.builder()
                    .fileId(sysFile.getId())
                    .url(sysFile.getAccessUrl())
                    .originalName(sysFile.getOriginalName())
                    .fileSize(sysFile.getFileSize())
                    .fileSizeFormatted(FileUploadEnhancedResponse.formatFileSize(sysFile.getFileSize()))
                    .mimeType(sysFile.getMimeType())
                    .fileHash(sysFile.getFileHash())
                    .quickUpload(false)
                    .uploadTime(sysFile.getUploadTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
    
    @Override
    public void createFileReference(String fileId, String entityType, String entityId, String fieldName, String userId) {
        // 检查文件是否存在
        SysFileEnhanced file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
        
        // 检查是否已存在引用
        Optional<SysFileReference> existingRef = referenceRepository.findByFileIdAndEntityTypeAndEntityIdAndFieldName(
                fileId, entityType, entityId, fieldName);
        
        if (!existingRef.isPresent()) {
            // 创建新引用
            SysFileReference reference = SysFileReference.create(fileId, entityType, entityId, fieldName, userId);
            referenceRepository.save(reference);
            
            // 增加引用计数
            fileRepository.incrementReferenceCount(fileId);
            log.info("创建文件引用: fileId={}, entityType={}, entityId={}, fieldName={}", 
                    fileId, entityType, entityId, fieldName);
        }
    }
    
    @Override
    public void removeFileReference(String fileId, String entityType, String entityId, String fieldName) {
        Optional<SysFileReference> reference = referenceRepository.findByFileIdAndEntityTypeAndEntityIdAndFieldName(
                fileId, entityType, entityId, fieldName);
        
        if (reference.isPresent()) {
            // 删除引用
            referenceRepository.delete(reference.get());
            
            // 减少引用计数
            fileRepository.decrementReferenceCount(fileId);
            log.info("删除文件引用: fileId={}, entityType={}, entityId={}, fieldName={}", 
                    fileId, entityType, entityId, fieldName);
        }
    }
    
    @Override
    @Transactional
    public void updateFileReference(String newFileId, String entityType, String entityId, String fieldName, String userId) {
        // 查找旧的引用
        Optional<SysFileReference> oldReference = referenceRepository.findByEntityTypeAndEntityIdAndFieldName(
                entityType, entityId, fieldName);
        
        // 删除旧引用
        if (oldReference.isPresent()) {
            String oldFileId = oldReference.get().getFileId();
            removeFileReference(oldFileId, entityType, entityId, fieldName);
        }
        
        // 创建新引用
        if (StringUtils.hasText(newFileId)) {
            createFileReference(newFileId, entityType, entityId, fieldName, userId);
        }
    }
    
    @Override
    public int cleanUnusedFiles(int days) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(days);
        List<SysFileEnhanced> unusedFiles = fileRepository.findUnusedFiles(beforeDate);
        
        int count = 0;
        for (SysFileEnhanced file : unusedFiles) {
            try {
                // 删除物理文件
                Path filePath = Paths.get(file.getAbsolutePath());
                Files.deleteIfExists(filePath);
                
                // 标记为已删除
                file.setIsDeleted(true);
                fileRepository.save(file);
                
                count++;
                log.info("清理未使用文件: fileId={}, path={}", file.getId(), file.getAbsolutePath());
            } catch (Exception e) {
                log.error("清理文件失败: fileId={}", file.getId(), e);
            }
        }
        
        return count;
    }
    
    @Override
    public SysFileEnhanced getFileInfo(String fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    }
    
    @Override
    public String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("计算文件哈希失败", e);
            throw new BusinessException("9001", "计算文件哈希失败");
        }
    }
    
    @Override
    public List<Object[]> getFileStatistics() {
        return fileRepository.statisticsByCategory();
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件不能为空");
        }
        
        // 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEED);
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !allowedTypes.contains(contentType)) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
    }
    
    /**
     * 保存新文件
     */
    private SysFileEnhanced saveNewFile(MultipartFile file, String fileHash, String category, String userId) throws IOException {
        // 生成存储路径和文件名
        LocalDate now = LocalDate.now();
        String relativePath = String.format("%d/%02d/%02d/%s/",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 
                StringUtils.hasText(category) ? category : "default");
        
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(extension)) {
            storedName += "." + extension;
        }
        
        // 创建目录
        File dir = new File(uploadBasePath, relativePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 保存文件
        File dest = new File(dir, storedName);
        file.transferTo(dest);
        
        // 创建文件记录
        SysFileEnhanced sysFile = new SysFileEnhanced();
        sysFile.setId(UUID.randomUUID().toString().replace("-", ""));
        sysFile.setOriginalName(file.getOriginalFilename());
        sysFile.setStoredName(storedName);
        sysFile.setFileHash(fileHash);
        sysFile.setFileSize(file.getSize());
        sysFile.setMimeType(file.getContentType());
        sysFile.setExtension(extension);
        sysFile.setRelativePath(relativePath + storedName);
        sysFile.setAbsolutePath(dest.getAbsolutePath());
        sysFile.setAccessUrl("/uploads/" + relativePath + storedName);
        sysFile.setReferenceCount(0);
        sysFile.setUploadUserId(userId);
        sysFile.setUploadTime(LocalDateTime.now());
        sysFile.setLastAccessTime(LocalDateTime.now());
        sysFile.setFileCategory(category);
        sysFile.setIsDeleted(false);
        
        return fileRepository.save(sysFile);
    }
}