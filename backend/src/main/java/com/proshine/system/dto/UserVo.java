package com.proshine.system.dto;

import com.proshine.system.entity.SysUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户VO类
 */
@Data
public class UserVo {
    
    private String id;
    private String username;
    private String realName;
    private String gender;
    private String phone;
    private String jobNumber;
    private String departmentId;
    private String departmentName;
    private String positionId;
    private String positionName;
    private String titleId;
    private String titleName;
    private String avatarUrl;
    private String faceImageUrl;
    private String cardNumber;
    private String attendanceNumber;
    private SysUser.Status status;
    private String customerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 角色信息
    private List<String> roleIds;
    private List<String> roleNames;
    
    // 权限信息
    private List<String> permissions;
}