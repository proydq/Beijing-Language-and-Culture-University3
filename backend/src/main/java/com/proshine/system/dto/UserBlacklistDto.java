package com.proshine.system.dto;

import lombok.Data;

/**
 * 用户黑名单DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class UserBlacklistDto {

    /**
     * 黑名单ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 工号
     */
    private String employeeId;

    /**
     * 部门
     */
    private String department;

    /**
     * 加入黑名单原因
     */
    private String reason;

    /**
     * 黑名单类型
     */
    private String blacklistType;

    /**
     * 违规次数
     */
    private Integer violationCount;

    /**
     * 创建时间
     */
    private String createTime;
}