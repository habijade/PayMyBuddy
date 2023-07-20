package com.example.demo.service.impl;

import com.example.demo.dto.TransactionDto;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.model.ResultTransactions;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ResultTransactions transferAmount(String recipientEmail, String description, Double transferAmount) {
        ResultTransactions resultTransactions = new ResultTransactions();
        User userSender = userService.getLoggedUser();
        Long userId = userSender.getId();

        if (recipientEmail.equals(userSender.getEmail())) {
            resultTransactions.setMessage("Cannot transfer money to yourself");
            resultTransactions.setResult(false);
            return resultTransactions;
        }

        User recipient = userService.findUserByEmail(recipientEmail);
        if (recipient == null) {
            resultTransactions.setMessage("Invalid recipient");
            resultTransactions.setResult(false);
            return resultTransactions;
        }


        Double senderBalance = userSender.getBalance();
        if (senderBalance < transferAmount) {
            resultTransactions.setMessage("Insufficient funds in the sender's account");
            resultTransactions.setResult(false);
            return resultTransactions;
        }

        //calcul du prélèvement
        Double fee = (transferAmount * 0.5 / 100);
        //déduction du prélèvement du montant transféré
        transferAmount = transferAmount - fee;

        userSender.setBalance(senderBalance - transferAmount);

        // Check if the recipient's balance is null
        if (recipient.getBalance() == null) {
            recipient.setBalance(transferAmount);
        } else {
            recipient.setBalance(recipient.getBalance() + transferAmount);
        }

        userService.saveUser(userSender);
        userService.saveUser(recipient);

        Transaction transaction = new Transaction();
        transaction.setAmount(transferAmount);
        transaction.setDescription(description);
        transaction.setDate(LocalDate.now());
        transaction.setFee(fee);

        transaction.setSenderId(userSender.getId());
        transaction.setReceiverId(recipient.getId());

        transactionRepository.save(transaction);

        resultTransactions.setMessage("Transfer successful");
        resultTransactions.setResult(true);
        return resultTransactions;
    }

    @Override
    public List<TransactionDto> getTransactionHistoryForUser(Long userId) {
        List<Transaction> sentTransactions = transactionRepository.findTransactionsBySenderId(userId);
        List<Transaction> receivedTransactions = transactionRepository.findTransactionsByReceiverId(userId);

        List<TransactionDto> transactionDtoList = new ArrayList<>();

        for (Transaction transaction : sentTransactions) {
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setAmount(transaction.getAmount());
            transactionDto.setDescription(transaction.getDescription());
            // Set other properties as needed

            transactionDtoList.add(transactionDto);
        }

        for (Transaction transaction : receivedTransactions) {
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setAmount(transaction.getAmount());
            transactionDto.setDescription(transaction.getDescription());
            // Set other properties as needed

            transactionDtoList.add(transactionDto);
        }

        return transactionDtoList;
    }

}
