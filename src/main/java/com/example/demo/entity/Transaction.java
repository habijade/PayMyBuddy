package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "sender_id")
    private Long senderId;
    @Column (name = "receiver_id")
    private Long receiverId;
    @Column
    private Double amount;
    @Column
    private String description;
    @Column
    private Double fee;
    @Column
    private LocalDate date;
}
