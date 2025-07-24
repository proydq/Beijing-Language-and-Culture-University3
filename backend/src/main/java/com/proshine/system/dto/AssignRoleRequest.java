package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignRoleRequest {
    private String userId;
    private List<String> roleIds;
}
