package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class ConnectionId implements Serializable {
    private Long userId;
    private Long connectedUserId;
}
