package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 */
@Entity
@Table(name = "sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "VARCHAR(32) COMMENT '主键UUID，无横杠'")
    private String id;
    
    @Column(name = "username", columnDefinition = "VARCHAR(50) COMMENT '登录用户名'")
    private String username;
    
    @Column(name = "password", columnDefinition = "VARCHAR(100) COMMENT '密码（加密存储）'")
    private String password;
    
    @Column(name = "real_name", columnDefinition = "VARCHAR(50) COMMENT '姓名'")
    private String realName;
    
    @Column(name = "gender", columnDefinition = "VARCHAR(10) COMMENT '性别'")
    private String gender;
    
    @Column(name = "phone", columnDefinition = "VARCHAR(20) COMMENT '手机号'")
    private String phone;
    
    @Column(name = "job_number", columnDefinition = "VARCHAR(30) COMMENT '工号'")
    private String jobNumber;
    
    @Column(name = "department_id", columnDefinition = "VARCHAR(32) COMMENT '所属部门ID（UUID外键）'")
    private String departmentId;
    
    @Column(name = "department_name", columnDefinition = "VARCHAR(100) COMMENT '部门名称（冗余）'")
    private String departmentName;
    
    @Column(name = "position_id", columnDefinition = "VARCHAR(32) COMMENT '职务ID（UUID外键）'")
    private String positionId;
    
    @Column(name = "position_name", columnDefinition = "VARCHAR(100) COMMENT '职务名称（冗余）'")
    private String positionName;
    
    @Column(name = "title_id", columnDefinition = "VARCHAR(32) COMMENT '职称ID（UUID外键）'")
    private String titleId;
    
    @Column(name = "title_name", columnDefinition = "VARCHAR(100) COMMENT '职称名称（冗余）'")
    private String titleName;
    
    @Column(name = "avatar_url", columnDefinition = "VARCHAR(255) COMMENT '头像地址'")
    private String avatarUrl;
    
    @Column(name = "face_image_url", columnDefinition = "VARCHAR(255) COMMENT '人脸照片地址'")
    private String faceImageUrl;
    
    @Column(name = "card_number", columnDefinition = "VARCHAR(50) COMMENT '一卡通编号'")
    private String cardNumber;
    
    @Column(name = "attendance_number", columnDefinition = "VARCHAR(50) COMMENT '无感考勤编号'")
    private String attendanceNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(10) COMMENT '状态（NORMAL / DISABLED）'")
    private Status status = Status.NORMAL;
    
    @Column(name = "customer_id", columnDefinition = "VARCHAR(32) COMMENT '客户域ID，多租户支持'")
    private String customerId;
    
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createTime;
    
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    private LocalDateTime updateTime;
    
    @Column(name = "deleted", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否逻辑删除（0否，1是）'")
    private Boolean deleted = false;
    
    /**
     * 用户状态枚举
     */
    public enum Status {
        NORMAL, DISABLED
    }
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}