package com.proshine.system.dto;

import com.proshine.system.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {
    private User user;
    private List<String> permissionCodes;
}
