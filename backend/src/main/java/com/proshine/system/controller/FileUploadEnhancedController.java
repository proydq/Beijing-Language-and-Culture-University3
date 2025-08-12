package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.FileCheckRequest;
import com.proshine.system.dto.FileCheckResponse;
import com.proshine.system.dto.FileUploadEnhancedResponse;
import com.proshine.system.service.FileUploadEnhancedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

/**
 * 增强的文件上传控制器
 * 
 * @author system
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/upload/v2")
public class FileUploadEnhancedController {
    
    @Autowired
    private FileUploadEnhancedService fileUploadService;
    
    /**
     * 检查文件是否已存在（支持秒传）
     * 
     * @param request 文件检查请求
     * @return 文件检查响应
     */
    @PostMapping("/check")
    @PreAuthorize("hasAuthority('FILE_UPLOAD') or hasAuthority('FILE_MANAGE')")
    public ResponseEntity<FileCheckResponse> checkFile(@Valid @RequestBody FileCheckRequest request) {
        log.info("检查文件是否存在: fileName={}, fileSize={}, hash={}", 
                request.getFileName(), request.getFileSize(), request.getFileHash());
        
        FileCheckResponse response = fileUploadService.checkFile(request);
        return ResponseEntity.success(response);
    }
    
    /**
     * 上传文件（支持秒传）
     * 
     * @param file 文件
     * @param fileHash 文件哈希值（可选）
     * @param category 文件分类（avatar/face/document等）
     * @param principal 当前用户
     * @return 上传响应
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('FILE_UPLOAD') or hasAuthority('FILE_MANAGE')")
    public ResponseEntity<FileUploadEnhancedResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "hash", required = false) String fileHash,
            @RequestParam(value = "category", defaultValue = "default") String category,
            Principal principal) {
        
        log.info("上传文件: fileName={}, size={}, category={}", 
                file.getOriginalFilename(), file.getSize(), category);
        
        // 获取当前用户ID（这里简化处理，实际应从认证信息中获取）
        String userId = principal != null ? principal.getName() : "anonymous";
        
        FileUploadEnhancedResponse response = fileUploadService.uploadFile(file, fileHash, category, userId);
        
        if (response.getQuickUpload()) {
            log.info("文件秒传成功: fileId={}", response.getFileId());
        } else {
            log.info("文件上传成功: fileId={}", response.getFileId());
        }
        
        return ResponseEntity.success(response);
    }
    
    /**
     * 更新实体的文件引用
     * 
     * @param fileId 文件ID
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param fieldName 字段名
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/reference")
    @PreAuthorize("hasAuthority('FILE_UPLOAD') or hasAuthority('FILE_MANAGE')")
    public ResponseEntity<Void> updateFileReference(
            @RequestParam("fileId") String fileId,
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") String entityId,
            @RequestParam("fieldName") String fieldName,
            Principal principal) {
        
        log.info("更新文件引用: fileId={}, entityType={}, entityId={}, fieldName={}", 
                fileId, entityType, entityId, fieldName);
        
        String userId = principal != null ? principal.getName() : "anonymous";
        fileUploadService.updateFileReference(fileId, entityType, entityId, fieldName, userId);
        
        return ResponseEntity.success();
    }
    
    /**
     * 获取文件统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('FILE_VIEW') or hasAuthority('FILE_MANAGE')")
    public ResponseEntity<?> getFileStatistics() {
        return ResponseEntity.success(fileUploadService.getFileStatistics());
    }
}