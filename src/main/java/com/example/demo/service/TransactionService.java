package com.example.demo.service;

import com.example.demo.dto.TransactionDto;
import com.example.demo.model.ResultTransactions;

import java.util.List;

public interface TransactionService {
    ResultTransactions transferAmount(String recipientEmail, String description, Double transferAmount);
    List<TransactionDto> getTransactionHistoryForUser(Long userId);
}
