package com.example.demo.controller;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HomePageController {
    @Autowired
    private UserService userService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String home(@ModelAttribute("userDto") UserDto userDto, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        String name = user.getName();
        Double balance = user.getBalance();

        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }

        // Get the list of connections
        List<UserDto> connections = connectionService.getFriends(userId);

        TransactionDto transferDto = new TransactionDto();
        model.addAttribute("transactionDto", transferDto);
        List<TransactionDto> transactionList = transactionService.getTransactionHistoryForUser(userId);

        model.addAttribute("balance", balance);
        model.addAttribute("name", name);

        if (connections != null) {
            model.addAttribute("connections", true);
        } else {
            model.addAttribute("connections", false);
        }

        model.addAttribute("transactions", transactionList);

        return "index";
    }

    @PostMapping("/")
    public String transferAmount(@ModelAttribute("transactionDto") TransactionDto transferDto, @ModelAttribute("userDto") UserDto userDto, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        String name = user.getName();
        Double balance = user.getBalance();

        List<UserDto> connections = connectionService.getFriends(userId);
        List<TransactionDto> transactionList = transactionService.getTransactionHistoryForUser(userId);

        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found");
            model.addAttribute("connections", !connections.isEmpty());

            model.addAttribute("transactions", transactionList);

            return "index";
        } else {
            transactionService.transferAmount(transferDto.getRecipientEmail(), transferDto.getDescription(), transferDto.getAmount());

        }
        model.addAttribute("balance", balance);
        model.addAttribute("name", name);


        return "index";
    }
}
