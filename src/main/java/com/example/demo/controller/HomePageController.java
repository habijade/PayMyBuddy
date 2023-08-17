package com.example.demo.controller;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.model.ResultTransactions;
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
        populateUserModel(user, userDto, model);
        processIndexData(model, user);
        return "index";
    }

    @PostMapping("/")
    public String transferAmount(@ModelAttribute("transactionDto") TransactionDto transferDto,
                                 @ModelAttribute("userDto") UserDto userDto, Model model) {
        User user = userService.getLoggedUser();
        populateUserModel(user, userDto, model);

        List<UserDto> connections = connectionService.getFriends(user.getId());
        model.addAttribute("connections", !connections.isEmpty());

        List<TransactionDto> transactionDtoList = transactionService.getTransactionHistoryForUser(user.getId());
        processTransactionHistory(transactionDtoList);

        model.addAttribute("transactions", transactionDtoList);

        User existingUser = userService.findUserByEmail(transferDto.getRecipientEmail());


        if (existingUser == null) {
            model.addAttribute("errorMessage", "User not found");
        } else {
            ResultTransactions resultTransactions = transactionService.transferAmount(transferDto.getRecipientEmail(), transferDto.getDescription(), transferDto.getAmount());
            if (resultTransactions.isResult() == false) {
                model.addAttribute("errorMessage", resultTransactions.getMessage());
            } else {
                model.addAttribute("successMessage", resultTransactions.getMessage());
            }
        }

        processIndexData(model, user);
        return "index";
    }

    private void populateUserModel(User user, UserDto userDto, Model model) {
        model.addAttribute("balance", user.getBalance() != null ? user.getBalance() : 0.0);
        model.addAttribute("name", user.getName());
        model.addAttribute("userDto", userDto);
    }

    private void processTransactionHistory(List<TransactionDto> transactionDtoList) {
        for (TransactionDto transactionDto : transactionDtoList) {
            String type = getTransactionType(transactionDto);
            transactionDto.setType(type);
            transactionDto.setRecipientEmail(transactionDto.getRecipientEmail());
        }
    }

    private String getTransactionType(TransactionDto transactionDto) {
        Long receiverId = transactionDto.getReceiverId();
        User receiver = userService.findUserById(receiverId);
        String receiverEmail = receiver.getEmail();
        if (transactionDto.getRecipientEmail().equals(receiverEmail)) {
            return "SENT";
        } else {
            return "RECEIVED";
        }
    }

    private void processIndexData(Model model, User user) {
        List<UserDto> connections = connectionService.getFriends(user.getId());
        model.addAttribute("connections", !connections.isEmpty());

        List<TransactionDto> transactionDtoList = transactionService.getTransactionHistoryForUser(user.getId());
        processTransactionHistory(transactionDtoList);

        model.addAttribute("transactionDto", new TransactionDto());
        model.addAttribute("transactions", transactionDtoList);
    }
}