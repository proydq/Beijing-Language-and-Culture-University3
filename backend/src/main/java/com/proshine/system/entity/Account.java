package com.proshine.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_account")
public class Account {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(length = 32)
    private String id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 128)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Status status = Status.NORMAL;

    public enum Status {
        NORMAL, DISABLED
    }
}
