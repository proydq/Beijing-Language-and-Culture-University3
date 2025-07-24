package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionsRequest {
    private String roleId;
    private List<String> permissionIds;
}
