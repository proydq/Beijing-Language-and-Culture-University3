package com.proshine.system.dto;

import lombok.Data;

/**
 * 人员信息VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class PersonnelVo {

    /**
     * 人员ID
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 用户名
     */
    private String username;
}