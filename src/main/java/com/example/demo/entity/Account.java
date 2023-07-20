package com.example.demo.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Temporal(TemporalType.DATE)
    private Date date;
    private String description;
    private Double balance;
    private int iban;
    private String name;

    public Account() {
    }

    public Account(User user, Date date, String description, Double balance, int iban, String name) {
        this.user = user;
        this.date = date;
        this.description = description;
        this.balance = balance;
        this.iban = iban;
        this.name = name;
    }

}
