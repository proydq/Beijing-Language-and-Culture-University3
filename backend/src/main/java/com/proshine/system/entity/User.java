package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(length = 32)
    private String id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(length = 64)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, DISABLED
    }
}
