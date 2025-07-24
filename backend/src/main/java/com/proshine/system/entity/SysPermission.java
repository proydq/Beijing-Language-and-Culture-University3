package com.proshine.system.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "sys_permission")
public class SysPermission {
    @Id
    @Column(length = 32)
    private String id;

    @Column(length = 50)
    private String name;

    @Column(length = 100)
    private String code;

    @Column(length = 20)
    private String type;

    @Column(name = "parent_id", length = 32)
    private String parentId;

    @Column(length = 255)
    private String url;

    @Column
    private Integer sort;

    @Column(name = "customer_id", length = 32)
    private String customerId;
}
