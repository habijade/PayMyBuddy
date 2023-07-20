package com.example.demo.service;

import com.example.demo.dto.UserDto;

import java.util.List;

public interface ConnectionService {
    boolean addFriend(Long userId, String buddyEmail);

    public void removeFriend(Long userId, Long friendId);

    public List<UserDto> getFriends(Long userId);

    boolean areFriend(Long userId, Long buddyId);
    public void  deleteFriend(Long userId, Long friendId);
}

