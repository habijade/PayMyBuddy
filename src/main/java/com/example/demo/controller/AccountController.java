package com.example.demo.controller;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.WithdrawDto;
import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.model.ResultDebitAccount;
import com.example.demo.model.ResultWithdrawAccount;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;


    @GetMapping("/bank")
    public String showAddAccountForm(Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();

        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }

        if (accountService.checkIfUserAccountExists(userId)) {
            Account account = accountService.getBankAccountInformation(userId);
            model.addAttribute("account", account);
        }

        AccountDto accountDto = new AccountDto();
        WithdrawDto withdrawDto = new WithdrawDto();
        model.addAttribute("accountDto", accountDto);
        model.addAttribute("withdrawDto", withdrawDto);

        updateUserInfo(model);
        return "bank";
    }

    @PostMapping("/bank")
    public String addAccount(@Valid @ModelAttribute("accountDto") AccountDto accountDto,
                             BindingResult result,
                             Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();

        if (result.hasErrors()) {
            model.addAttribute("accountDto", accountDto);
            return "bank";
        }

        if (accountService.checkIfUserAccountExists(userId)) {
            result.rejectValue("name", null, "An account already exists for this user");
            model.addAttribute("accountDto", accountDto);
            return "bank";
        }

        if (accountService.checkIfIbanExists(accountDto.getIban())) {
            result.rejectValue("iban", null, "IBAN already exists. Please enter a different IBAN.");
            model.addAttribute("accountDto", accountDto);
            return "bank";
        }
        Account account = new Account();
        account.setName(accountDto.getName());
        account.setIban(accountDto.getIban());
        account.setBalance(accountDto.getBalance());
        account.setDescription(accountDto.getDescription());
        account.setUser(user);

        accountService.saveBankAccountInformation(account);
        model.addAttribute("account", account);
        updateUserInfo(model);
        return "redirect:/bank?success";
    }

    @PostMapping("/bank/withdraw")
    public String withdrawAmount(@RequestParam("withdrawAmount") Double withdrawAmount, @ModelAttribute("accountDto") AccountDto accountDto,
                                 Model model) {
        ResultWithdrawAccount resultWithdrawAccount = accountService.withdrawAccount(withdrawAmount);
        User user = userService.getLoggedUser();
        Account account = accountService.getBankAccountInformation(user.getId());
        model.addAttribute("account", account);
        updateUserInfo(model);

        if (!resultWithdrawAccount.isResult()) {
            model.addAttribute("errorMessage", "Insufficient funds");
        } else {
            model.addAttribute("successMessage", "Withdrawal successful");
        }

        return "bank";
    }


    @PostMapping("/bank/deposit")
    public String depositAmount(@RequestParam("depositAmount") Double depositAmount, @ModelAttribute("accountDto") AccountDto accountDto,
                                Model model) {
        ResultDebitAccount resultDebitAccount = accountService.debitAccount(depositAmount);
        User user = userService.getLoggedUser();
        Account account = accountService.getBankAccountInformation(user.getId());
        model.addAttribute("account", account);
        updateUserInfo(model);

        if (!resultDebitAccount.isResult()) {
            model.addAttribute("errorMessage", "Insufficient funds in the virtual account");
        } else {
            model.addAttribute("successMessage", "Deposit successful");// Redirect to the bank page to avoid resubmitting the form
        }

        return "bank";
    }

    @PostMapping("/bank/delete")
    public String deleteAccount(@RequestParam("accountId") Long accountId, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();

        if (accountService.checkIfUserAccountExists(userId)) {
            accountService.deleteBankAccount(userId);
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }

        return "redirect:/bank";
    }

    @GetMapping("/bank/edit")
    public String showEditAccountForm(Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        if (accountService.checkIfUserAccountExists(userId)) {
            Account account = accountService.getBankAccountInformation(userId);
            AccountDto accountDto = new AccountDto();
            accountDto.setIban(account.getIban());
            model.addAttribute("accountDto", accountDto);
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }

        return "bank";
    }

    @PostMapping("/bank/update-iban")
    public String updateIban(@RequestParam("iban") String newIban, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        Account account = accountService.getBankAccountInformation(userId);
        model.addAttribute("account", account);

        if (!accountService.checkIfIbanExists(newIban)) {
            account.setIban(newIban);

            accountService.saveBankAccountInformation(account);

            model.addAttribute("successMessage", "IBAN updated successfully");
        } else {
            throw new IllegalArgumentException("There is no existing account for this user");
        }

        return "redirect:/bank";
    }

    private void updateUserInfo(Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("balance", user.getBalance());
        model.addAttribute("name", user.getName());
        model.addAttribute("user", user);
    }
}
