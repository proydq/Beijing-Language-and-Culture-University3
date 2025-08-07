package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 增强的文件实体，包含完整的文件元数据和去重机制
 * 
 * @author system
 * @since 1.0.0
 */
@Entity
@Table(name = "sys_file_enhanced")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysFileEnhanced {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(64) COMMENT '主键UUID'")
    private String id;

    @Column(name = "original_name", columnDefinition = "VARCHAR(255) COMMENT '原始文件名'")
    private String originalName;

    @Column(name = "stored_name", columnDefinition = "VARCHAR(255) COMMENT '存储文件名'")
    private String storedName;

    @Column(name = "file_hash", unique = true, columnDefinition = "VARCHAR(64) COMMENT '文件哈希值(SHA256)'")
    private String fileHash;

    @Column(name = "file_size", columnDefinition = "BIGINT COMMENT '文件大小（字节）'")
    private Long fileSize;

    @Column(name = "mime_type", columnDefinition = "VARCHAR(100) COMMENT 'MIME类型'")
    private String mimeType;

    @Column(name = "extension", columnDefinition = "VARCHAR(20) COMMENT '文件扩展名'")
    private String extension;

    @Column(name = "relative_path", columnDefinition = "VARCHAR(500) COMMENT '相对路径'")
    private String relativePath;

    @Column(name = "absolute_path", columnDefinition = "VARCHAR(500) COMMENT '绝对路径'")
    private String absolutePath;

    @Column(name = "access_url", columnDefinition = "VARCHAR(500) COMMENT '访问URL'")
    private String accessUrl;

    @Column(name = "reference_count", columnDefinition = "INT DEFAULT 0 COMMENT '引用计数'")
    private Integer referenceCount = 0;

    @Column(name = "upload_user_id", columnDefinition = "VARCHAR(32) COMMENT '上传用户ID'")
    private String uploadUserId;

    @Column(name = "upload_time", columnDefinition = "DATETIME COMMENT '上传时间'")
    private LocalDateTime uploadTime;

    @Column(name = "last_access_time", columnDefinition = "DATETIME COMMENT '最后访问时间'")
    private LocalDateTime lastAccessTime;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否删除'")
    private Boolean isDeleted = false;

    @Column(name = "file_category", columnDefinition = "VARCHAR(50) COMMENT '文件分类(avatar/face/document等)'")
    private String fileCategory;

    @Column(name = "description", columnDefinition = "VARCHAR(500) COMMENT '文件描述'")
    private String description;

    /**
     * 增加引用计数
     */
    public void incrementReference() {
        if (this.referenceCount == null) {
            this.referenceCount = 0;
        }
        this.referenceCount++;
    }

    /**
     * 减少引用计数
     */
    public void decrementReference() {
        if (this.referenceCount != null && this.referenceCount > 0) {
            this.referenceCount--;
        }
    }

    /**
     * 是否可以删除（引用计数为0）
     */
    public boolean canDelete() {
        return this.referenceCount == null || this.referenceCount <= 0;
    }
}