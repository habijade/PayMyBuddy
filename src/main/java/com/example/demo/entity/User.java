package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true)
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotBlank(message = "password is mandatory")
    private String password;
    @Column(name = "firstName")
    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @Column(name = "lastName")
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    //private BigDecimal balance;

    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;

    }
}
