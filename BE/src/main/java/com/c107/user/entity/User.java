package com.c107.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = true)
    private String name;

    @Column(name = "birth_date", nullable = true)
    private String birthDate;

    private String financeUserKey;

    private String role;

    @Column(nullable = false)
    private Integer deposit = 0;
}
