package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRoleCondition extends SearchBaseCondition {
    private String roleName;
}
