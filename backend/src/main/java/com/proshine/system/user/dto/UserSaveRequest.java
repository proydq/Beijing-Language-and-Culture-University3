package com.proshine.system.user.dto;

import lombok.Data;

/**
 * Request body for creating or updating user.
 */
@Data
public class UserSaveRequest {
    private String realName;
    private String gender;
    private String phone;
    private String jobNumber;
    private String departmentId;
    private String positionId;
    private String titleId;
    private String avatarUrl;
    private String faceImageUrl;
    private String cardNumber;
    private String attendanceNumber;
}
