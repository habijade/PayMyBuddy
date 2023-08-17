package com.example.demo.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//@AllArgsConstructor
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

    public Connection(Long userId, Long buddyId) {
            this.userId = userId;
            this.connectedUserId = buddyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(Long connectedUserId) {
        this.connectedUserId = connectedUserId;
    }
}
