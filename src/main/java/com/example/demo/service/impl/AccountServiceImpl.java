package com.example.demo.service.impl;

import com.example.demo.dto.AccountDto;
import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.model.ResultDebitAccount;
import com.example.demo.model.ResultWithdrawAccount;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserService userService;


    @Override
    public Account getBankAccountInformation(Long userId) {
        if (checkIfUserAccountExists(userId)) {
            return accountRepository.findByUserId(userId);
        }
        return null;
    }

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
            accountRepository.save(account);
            return true;
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }
    }

    @Transactional
    @Override
    public boolean deleteBankAccount(Long userId) {
        if (checkIfUserAccountExists(userId)) {
            accountRepository.deleteByUserIdQuery(userId);
            return true;
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }
    }

    @Override
    public boolean checkIfUserAccountExists(Long userId) {
        boolean exist = accountRepository.existsByUserId(userId);
        return accountRepository.existsByUserId(userId);
    }

    @Override
    public boolean checkIfIbanExists(int iban) {
        Account existingAccount = accountRepository.findByIban(iban);
        return existingAccount != null;
    }

    @Override
    public ResultDebitAccount debitAccount(Double amount) {
        ResultDebitAccount resultDebitAccount = new ResultDebitAccount();
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        if (user != null) {
            Double balance = user.getBalance();
            Account account = getBankAccountInformation(userId);
            if (account != null) {
                Double currentBalance = account.getBalance();
                if (currentBalance.compareTo(amount) < 0) {
                    resultDebitAccount.setMessage("Insufficient funds in the virtual account");
                    resultDebitAccount.setResult(false);
                    return resultDebitAccount;
                }
                Double updatedBalanceAccount = currentBalance - amount;
                account.setBalance(updatedBalanceAccount);
                accountRepository.save(account);

                if (balance == null) {
                    balance = 0.0;
                }
                Double updatedBalance = balance + amount;
                user.setBalance(updatedBalance);
                userService.saveUser(user);
                resultDebitAccount.setMessage("Deposit successful");
                resultDebitAccount.setResult(true);
                return resultDebitAccount;
            }

        } else {
            resultDebitAccount.setMessage("Utilisteur inconnue");
            resultDebitAccount.setResult(false);
        }
        return resultDebitAccount;
    }

    @Override
    public ResultWithdrawAccount withdrawAccount(Double amount) {
        ResultWithdrawAccount resultWithdrawAccount = new ResultWithdrawAccount();
        User user = userService.getLoggedUser();
        if (user != null) {
            Double balance = user.getBalance();
            if (balance != null && balance >= amount) {
                Double updatedBalance = balance - amount;
                user.setBalance(updatedBalance);
                resultWithdrawAccount.setMessage("Withdrawal successful");
                resultWithdrawAccount.setResult(true);
            } else {
                resultWithdrawAccount.setMessage("Insufficient funds");
                resultWithdrawAccount.setResult(false);
            }
        }
        return resultWithdrawAccount;
    }
}
