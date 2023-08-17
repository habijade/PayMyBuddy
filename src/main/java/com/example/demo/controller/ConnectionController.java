package com.example.demo.controller;

import com.example.demo.dto.ConnectionDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@Controller
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserService userService;

    @GetMapping("/connection")
    public String getConnectionPage(Model model) {
        ConnectionDto connectionDto = new ConnectionDto();
        model.addAttribute("connectionDto", connectionDto);

        User user = userService.getLoggedUser();
        populateUserInfo(user, model);

        List<UserDto> friends = connectionService.getFriends(user.getId());
        model.addAttribute("friends", friends);

        return "connection";
    }

    @PostMapping("/connection")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, Model model) {
        User user = userService.getLoggedUser();
        populateUserInfo(user, model);

        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return refreshConnectionPage(user, model);
        }

        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }

        boolean isAlreadyFriend = connectionService.areFriend(user.getId(), existingUser.getId());
        if (isAlreadyFriend) {
            model.addAttribute("errorMessage", "You are already connected with this user");
            return refreshConnectionPage(user, model);
        }

        try {
            connectionService.addFriend(user.getId(), existingUser.getEmail());
        } catch (IllegalArgumentException e) {
            return "redirect:/error";
        }

        return "redirect:/connection";
    }

    @PostMapping("/connection/delete")
    @Transactional
    public String deleteFriend(@RequestParam("friendId") Long friendId, Model model) {
        User user = userService.getLoggedUser();
        populateUserInfo(user, model);

        try {
            connectionService.deleteFriend(user.getId(), friendId);
        } catch (IllegalArgumentException e) {
            return "redirect:/error";
        }

        return refreshConnectionPage(user, model);
    }

    private void populateUserInfo(User user, Model model) {
        Long userId = user.getId();
        String name = user.getName();
        Double balance = (user.getBalance() != null) ? user.getBalance() : 0.0;

        model.addAttribute("balance", balance);
        model.addAttribute("name", name);
    }

    private String refreshConnectionPage(User user, Model model) {
        List<UserDto> friends = connectionService.getFriends(user.getId());
        model.addAttribute("friends", friends);
        return "connection";
    }
}