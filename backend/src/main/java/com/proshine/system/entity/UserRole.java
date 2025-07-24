package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user_role")
public class UserRole {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(length = 32)
    private String id;

    @Column(length = 32)
    private String userId;

    @Column(length = 32)
    private String roleId;
}
