package com.example.demo.controller;

import com.example.demo.dto.ConnectionDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        Long userId = user.getId();
        List<UserDto> friends = connectionService.getFriends(userId);
        model.addAttribute("friends", friends);
        return "connection";
    }

    @PostMapping("/connection")
    public String addUser(@ModelAttribute("userDto") UserDto userDto, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found");
            List<UserDto> friends = connectionService.getFriends(userId);
            model.addAttribute("friends", friends);
            return "connection";
        }

        boolean isAlreadyFriend = connectionService.areFriend(userId, existingUser.getId());
        if (isAlreadyFriend) {
            model.addAttribute("errorMessage", "You are already connected with this user");
            List<UserDto> friends = connectionService.getFriends(userId);
            model.addAttribute("friends", friends);
            return "connection";
        }

        try {
            connectionService.addFriend(userId, existingUser.getEmail());
        } catch (IllegalArgumentException e) {
            return "redirect:/error";
        }

        List<UserDto> friends = connectionService.getFriends(userId);

        model.addAttribute("friends", friends);
        return "connection";
    }

    @PostMapping("/connection/delete")
    @Transactional
    public String deleteFriend(@RequestParam("friendId") Long friendId, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();

        try {
            connectionService.deleteFriend(userId, friendId);
        } catch (IllegalArgumentException e) {
            return "redirect:/error";
        }

        List<UserDto> friends = connectionService.getFriends(userId);
        model.addAttribute("friends", friends);
        return "connection";
    }

}
