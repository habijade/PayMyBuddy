package com.example.demo.service.impl;

import com.example.demo.dto.AccountDto;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;


    @Override
    public Account getBankAccountInformation(Long userId) {
        if (checkIfUserAccountExists(userId)) {
            return accountRepository.findByUserId(userId);
        }
        return null;
    }

    //créer un compte bancaire pour un utilisateur
    @Override
    public boolean saveBankAccountInformation(Account accountEntity) {
        if (accountEntity != null) {

            accountRepository.save(accountEntity);
            return true;
        } else {
            throw new IllegalArgumentException("Information given to create account is incorrect");
        }
    }

    @Override
    public boolean updateBankAccountInformation(Long userId, AccountDto accountDto) {
        if (checkIfUserAccountExists(userId)) {
            Account account = accountRepository.findByUserId(userId);
            account.setName(accountDto.getName());
            account.setIban(accountDto.getIban());

            // Mise à jour du solde du compte
            //accountRepository.updateAccountBalance(account.getId(), accountDto.getFee());

            accountRepository.save(account);
            return true;
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }
    }

    /*@Override
    public boolean deleteBankAccount(Long userId) {
        if (checkIfUserAccountExists(userId)) {
            accountRepository.deleteByUserId(userId);
            return true;
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }
    }

     */

    //verifier si le compte bancaire d'un utilisateur existe
    @Override
    public boolean checkIfUserAccountExists(Long userId) {
        return (accountRepository.findByUserId(userId) != null);
    }
}
