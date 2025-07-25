package com.proshine.system.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User view object for responses.
 */
@Data
public class UserVO {
    private String id;
    private String realName;
    private String phone;
    private String jobNumber;
    private String gender;
    private String departmentName;
    private String positionName;
    private String titleName;
    private String avatarUrl;
    private String faceImageUrl;
    private String status;
    private LocalDateTime createTime;
}
