package com.example.demo.service;

import com.example.demo.dto.AccountDto;
import com.example.demo.entity.Account;

public interface AccountService {
    public Account getBankAccountInformation(Long userId);

    public boolean saveBankAccountInformation(Account bankAccountEntity);

    public boolean updateBankAccountInformation(Long userId, AccountDto accountDto);

    //public boolean deleteBankAccount(Long userId);


    public boolean checkIfUserAccountExists(Long userId);

}
