package com.proshine.system.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "sys_user_role")
public class SysUserRole {
    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "user_id", length = 32)
    private String userId;

    @Column(name = "role_id", length = 32)
    private String roleId;
}
