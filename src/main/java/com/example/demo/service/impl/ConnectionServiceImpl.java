package com.example.demo.service.impl;

import com.example.demo.entity.Connection;
import com.example.demo.repository.ConnectionRepository;
import com.example.demo.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    ConnectionRepository connectionRepository;
    @Override
    public void addFriend(Long userId, Long friendId) {
        List<Connection> existingConnections = connectionRepository.findAllConnectionByUserId(userId);

        for(Connection connection : existingConnections){
            if(connection.getConnectedUserId().equals(friendId)){
                throw new IllegalArgumentException("This user is already in your friend list.");
            }
        }
        Connection connection = new Connection(userId, friendId);
        connectionRepository.save(connection);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        connectionRepository.deleteByUserIdAndConnectedUserId(userId, friendId);
    }
}
