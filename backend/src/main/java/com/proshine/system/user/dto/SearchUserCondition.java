package com.proshine.system.user.dto;

import lombok.Data;

/**
 * Condition object for searching users.
 */
@Data
public class SearchUserCondition {
    private String realName;
    private String phone;
    private String jobNumber;
    private String status;
    private String organizationId;
    private Integer pageNumber = 1;
    private Integer pageSize = 10;
}
