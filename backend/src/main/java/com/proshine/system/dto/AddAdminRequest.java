package com.proshine.system.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 添加管理员请求
 */
@Data
public class AddAdminRequest {
    
    /**
     * 用户ID列表
     */
    @NotEmpty(message = "用户ID列表不能为空")
    private List<String> userIds;
    
    /**
     * 角色ID列表
     */
    @NotEmpty(message = "角色ID列表不能为空")
    private List<String> roleIds;
    
    /**
     * 客户域ID(自动设置)
     */
    private String customerId;
}