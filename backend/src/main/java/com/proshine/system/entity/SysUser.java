package com.proshine.system.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_user")
public class SysUser {
    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(length = 10)
    private String gender;

    @Column(length = 20)
    private String phone;

    @Column(name = "job_number", length = 30)
    private String jobNumber;

    @Column(name = "department_id", length = 32)
    private String departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "position_id", length = 32)
    private String positionId;

    @Column(name = "position_name", length = 100)
    private String positionName;

    @Column(name = "title_id", length = 32)
    private String titleId;

    @Column(name = "title_name", length = 100)
    private String titleName;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "face_image_url", length = 255)
    private String faceImageUrl;

    @Column(name = "card_number", length = 50)
    private String cardNumber;

    @Column(name = "attendance_number", length = 50)
    private String attendanceNumber;

    @Column(length = 10)
    private String status;

    @Column(name = "customer_id", length = 32)
    private String customerId;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    private Boolean deleted = false;
}
