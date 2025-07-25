package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import com.proshine.system.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询条件DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchUserCondition extends SearchBaseCondition {
    
    private String username;
    private String realName;
    private String phone;
    private String jobNumber;
    private String departmentId;
    private String departmentName;
    private String positionId;
    private String positionName;
    private String titleId;
    private String titleName;
    private SysUser.Status status = SysUser.Status.NORMAL;
    private String gender;
}