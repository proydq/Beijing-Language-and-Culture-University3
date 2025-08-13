package com.proshine.system.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 添加管理员响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddAdminResponse {
    
    /**
     * 成功添加数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failCount;
    
    /**
     * 失败的用户ID列表
     */
    private List<String> failUserIds;
    
    /**
     * 失败原因列表
     */
    private List<String> failReasons;
}