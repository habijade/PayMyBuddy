package com.example.demo.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@IdClass(ConnectionId.class)
@Table(name = "connections")
public class Connection {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Id
    @Column(name = "connected_user_id")
    private Long connectedUserId;

        public Connection() {
    }
}
