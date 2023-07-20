package com.example.demo.service;

import com.example.demo.dto.AccountDto;
import com.example.demo.entity.Account;
import com.example.demo.model.ResultDebitAccount;
import com.example.demo.model.ResultWithdrawAccount;

import java.math.BigDecimal;

public interface AccountService {
    public Account getBankAccountInformation(Long userId);

    public boolean saveBankAccountInformation(Account bankAccountEntity);

    public boolean updateBankAccountInformation(Long userId, AccountDto accountDto);

    public boolean deleteBankAccount(Long userId);


    public boolean checkIfUserAccountExists(Long userId);

    public boolean checkIfIbanExists(int iban);
    public ResultDebitAccount debitAccount(Double amount);
    public ResultWithdrawAccount withdrawAccount(Double amount);

}
