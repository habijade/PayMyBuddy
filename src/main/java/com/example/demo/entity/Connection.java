package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "connections")
public class Connection {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "connected_user_id")
    private Long connectedUserId;
}
