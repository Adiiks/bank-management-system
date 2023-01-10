package com.adrian.bank.management.system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;

    @CreatedDate
    private LocalDate registrationDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
