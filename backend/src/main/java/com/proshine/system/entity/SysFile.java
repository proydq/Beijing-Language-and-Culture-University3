package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * File entity for storing uploaded file info
 */
@Entity
@Table(name = "sys_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysFile {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(64) COMMENT '主键UUID'")
    private String id;

    @Column(name = "file_name", columnDefinition = "VARCHAR(255) COMMENT '文件名'")
    private String fileName;

    @Column(name = "url", columnDefinition = "VARCHAR(255) COMMENT '访问地址'")
    private String url;

    @Column(name = "upload_time", columnDefinition = "DATETIME COMMENT '上传时间'")
    private LocalDateTime uploadTime;
}
