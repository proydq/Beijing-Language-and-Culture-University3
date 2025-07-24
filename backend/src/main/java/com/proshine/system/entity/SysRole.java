package com.proshine.system.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "sys_role")
public class SysRole {
    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "customer_id", length = 32)
    private String customerId;
}
