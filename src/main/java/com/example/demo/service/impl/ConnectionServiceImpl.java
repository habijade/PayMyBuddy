package com.example.demo.service.impl;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Connection;
import com.example.demo.entity.User;
import com.example.demo.exeption.ConnectionServiceException;
import com.example.demo.repository.ConnectionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public boolean addFriend(Long userId, String buddyEmail) throws IllegalArgumentException {
        if (userService.isAnExistingEmail(buddyEmail)) {
            Long buddyId = userService.findUserByEmail(buddyEmail).getId();

            if (!userId.equals(buddyId)) {

                if (!areFriend(userId, buddyId)) {
                    Connection connection = new Connection(userId, buddyId);
                    connectionRepository.save(connection);
                    return true;
                }

                throw new ConnectionServiceException("You are already connected with this user");
            }

            throw new ConnectionServiceException("You can't add yourself");
        }

        throw new ConnectionServiceException("There is no user linked to this email address");
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        connectionRepository.deleteByUserIdAndConnectedUserId(userId, friendId);
    }

    @Override
    public List<UserDto> getFriends(Long userId) {
        List<Connection> connections = connectionRepository.findAllConnectionByUserId(userId);
        List<Long> friendIds = connections.stream()
                .map(Connection::getConnectedUserId)
                .collect(Collectors.toList());
        List<User> friends = userRepository.findAllById(friendIds);
        return friends.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    @Override
    public boolean areFriend(Long userId, Long buddyId) {
        List<Connection> connections = connectionRepository.findAllConnectionByUserId(userId);

        for (Connection connection : connections) {
            if (connection.getConnectedUserId().equals(buddyId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        connectionRepository.deleteByUserIdAndConnectedUserId(userId, friendId);
    }
}
