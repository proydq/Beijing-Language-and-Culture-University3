package com.proshine.system.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量删除管理员请求
 */
@Data
public class BatchDeleteAdminRequest {
    
    /**
     * 用户ID列表
     */
    @NotEmpty(message = "用户ID列表不能为空")
    private List<String> userIds;
}