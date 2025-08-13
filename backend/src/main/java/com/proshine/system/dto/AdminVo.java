package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员信息视图对象
 */
@Data
public class AdminVo {
    
    /**
     * 用户ID
     */
    private String id;
    
    /**
     * 登录用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 职位名称
     */
    private String positionName;
    
    /**
     * 工号
     */
    private String jobNumber;
    
    /**
     * 账号状态
     */
    private String accountStatus;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 用户角色列表
     */
    private List<AdminRoleVo> roles;
    
    /**
     * 头像地址
     */
    private String avatarUrl;
    
    /**
     * 微信昵称
     */
    private String wechatNickname;
    
    /**
     * 微信绑定手机号
     */
    private String wechatPhone;
    
    /**
     * 微信绑定时间
     */
    private LocalDateTime wechatBindTime;
}