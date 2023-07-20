package com.example.demo.controller;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.AccountService;
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
public class TransactionController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();

        List<UserDto> connections = connectionService.getFriends(userId);
        model.addAttribute("connections", connections);

        TransactionDto transferDto = new TransactionDto();
        model.addAttribute("transferDto", transferDto);
        List<TransactionDto> transactionList = transactionService.getTransactionHistoryForUser(userId);
        model.addAttribute("transactionHistory", transactionList);

        return "transfer";
    }

    @PostMapping("/transfer")
    public String transferAmount(@ModelAttribute("transferDto") TransactionDto transferDto, Model model) {
        transactionService.transferAmount(transferDto.getRecipientEmail(), transferDto.getDescription(), transferDto.getAmount());
        return "transfer";
    }

}
