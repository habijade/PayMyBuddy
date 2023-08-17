package com.example.demo.service.impl;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.model.ResultTransactions;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ConnectionService connectionService;

    @Override
    public ResultTransactions transferAmount(String recipientEmail, String description, Double transferAmount) {
        ResultTransactions resultTransactions = new ResultTransactions();
        User userSender = userService.getLoggedUser();
        Long userId = userSender.getId();

        List<UserDto> connections = connectionService.getFriends(userId);

        boolean existingInConnections = false;
        for (UserDto u : connections) {
            if (u.getEmail().equals(recipientEmail)) {
                existingInConnections = true;
                break;
            }
        }

        if (existingInConnections) {
            User recipient = userService.findUserByEmail(recipientEmail);
            if (recipientEmail.equals(userSender.getEmail())) {
                resultTransactions.setMessage("Cannot transfer money to yourself");
                resultTransactions.setResult(false);
                return resultTransactions;
            }


            Double senderBalance = userSender.getBalance();
            if (senderBalance < transferAmount) {
                resultTransactions.setMessage("Insufficient funds in the sender's account");
                resultTransactions.setResult(false);
                return resultTransactions;
            }

            Double fee = (transferAmount * 0.5 / 100);
            transferAmount = transferAmount + fee;

            userSender.setBalance(senderBalance - transferAmount);

            if (recipient.getBalance() == null) {
                recipient.setBalance(transferAmount);
            } else {
                recipient.setBalance(recipient.getBalance() + (transferAmount - fee));
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
        } else {
            resultTransactions.setResult(false);
            resultTransactions.setMessage("This user is not in your friends list");
        }

        return resultTransactions;
    }

    @Override
    public List<TransactionDto> getTransactionHistoryForUser(Long userId) {
        List<Transaction> sentTransactions = transactionRepository.findTransactionsBySenderId(userId);
        List<Transaction> receivedTransactions = transactionRepository.findTransactionsByReceiverId(userId);

        List<TransactionDto> transactionDtoList = new ArrayList<>();

        for (Transaction transaction : sentTransactions) {
            Long reveiverId = transaction.getReceiverId();
            User reveiver = userService.findUserById(reveiverId);
            String receiverEmail = reveiver.getEmail();

            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setAmount(transaction.getAmount());
            transactionDto.setDescription(transaction.getDescription());
            transactionDto.setDate(transaction.getDate());
            transactionDto.setReceiverId(transaction.getReceiverId());
            transactionDto.setSenderId(transaction.getSenderId());
            transactionDto.setRecipientEmail(receiverEmail);
            transactionDtoList.add(transactionDto);
        }

        for (Transaction transaction : receivedTransactions) {
            Long senderId = transaction.getSenderId();
            User sender = userService.findUserById(senderId);
            String senderEmail = sender.getEmail();

            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setAmount(transaction.getAmount());
            transactionDto.setDescription(transaction.getDescription());
            transactionDto.setReceiverId(transaction.getReceiverId());
            transactionDto.setSenderId(transaction.getSenderId());
            transactionDto.setDate(transaction.getDate());
            transactionDto.setRecipientEmail(senderEmail);
            transactionDtoList.add(transactionDto);
        }

        return transactionDtoList;
    }

}
