package com.example.demo.service;

public interface ConnectionService {
    public void addFriend(Long userId, Long friendId);

    public void removeFriend(Long userId, Long friendId);
}
