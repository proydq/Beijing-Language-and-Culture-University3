package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_permission")
public class Permission {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(length = 32)
    private String id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(length = 128)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Type type = Type.MENU;

    public enum Type {
        MENU, BUTTON
    }
}
