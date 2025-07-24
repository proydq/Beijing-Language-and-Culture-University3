package com.proshine.system.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "sys_role_permission")
public class SysRolePermission {
    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "role_id", length = 32)
    private String roleId;

    @Column(name = "permission_id", length = 32)
    private String permissionId;
}
