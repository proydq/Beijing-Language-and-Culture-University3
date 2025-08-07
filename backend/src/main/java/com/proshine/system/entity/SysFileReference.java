package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件引用关系实体，记录文件被哪些实体引用
 * 
 * @author system
 * @since 1.0.0
 */
@Entity
@Table(name = "sys_file_reference")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysFileReference {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(64) COMMENT '主键UUID'")
    private String id;

    @Column(name = "file_id", nullable = false, columnDefinition = "VARCHAR(32) COMMENT '文件ID'")
    private String fileId;

    @Column(name = "entity_type", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '引用实体类型(USER/PRODUCT等)'")
    private String entityType;

    @Column(name = "entity_id", nullable = false, columnDefinition = "VARCHAR(32) COMMENT '引用实体ID'")
    private String entityId;

    @Column(name = "field_name", columnDefinition = "VARCHAR(50) COMMENT '引用字段名(avatar/faceImage等)'")
    private String fieldName;

    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "create_user_id", columnDefinition = "VARCHAR(32) COMMENT '创建用户ID'")
    private String createUserId;

    /**
     * 创建文件引用
     */
    public static SysFileReference create(String fileId, String entityType, String entityId, String fieldName, String userId) {
        SysFileReference reference = new SysFileReference();
        reference.setId(UUID.randomUUID().toString().replace("-", ""));
        reference.setFileId(fileId);
        reference.setEntityType(entityType);
        reference.setEntityId(entityId);
        reference.setFieldName(fieldName);
        reference.setCreateTime(LocalDateTime.now());
        reference.setCreateUserId(userId);
        return reference;
    }
}