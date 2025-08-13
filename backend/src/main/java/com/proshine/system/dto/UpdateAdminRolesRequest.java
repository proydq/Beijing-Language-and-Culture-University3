package com.proshine.system.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 更新管理员角色请求
 */
@Data
public class UpdateAdminRolesRequest {
    
    /**
     * 新的角色ID列表
     */
    @NotEmpty(message = "角色ID列表不能为空")
    private List<String> roleIds;
}