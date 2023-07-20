package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String home(Model model) {

        User user = userService.getLoggedUser();
        Double balance = user.getBalance();

        model.addAttribute("balance", balance);

        return "index";
    }
}
