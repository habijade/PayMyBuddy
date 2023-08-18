package com.example.demo.controller;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.WithdrawDto;
import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.model.ResultDebitAccount;
import com.example.demo.model.ResultUpdateAccount;
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
    public String showBankPage(Model model) {
        User user = userService.getLoggedUser();
        AccountDto accountDto = new AccountDto();
        prepareAccountInfo(user, model);
        Account account = accountService.getBankAccountInformation(user.getId());
        if(account != null){
            accountDto.setIban(account.getIban());
        }
        model.addAttribute("accountDto", accountDto);
        model.addAttribute("withdrawDto", new WithdrawDto());
        updateUserInfo(model);
        return "bank";
    }

    @PostMapping("/bank")
    public String addAccount(@Valid @ModelAttribute("accountDto") AccountDto accountDto,
                             BindingResult result,
                             Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        prepareAccountInfo(user, model);
        if (result.hasErrors()) {
            return showBankPage(model);
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

        Account account = createAccount(accountDto, user);
        accountService.saveBankAccountInformation(account);

        return "redirect:/bank?success";
    }

    @PostMapping("/bank/withdraw")
    public String withdrawAmount(@RequestParam("withdrawAmount") Double withdrawAmount,
                                 @ModelAttribute("accountDto") AccountDto accountDto,
                                 Model model) {
        User user = userService.getLoggedUser();
        ResultWithdrawAccount resultWithdrawAccount = accountService.withdrawAccount(withdrawAmount);
        prepareAccountInfo(user, model);

        if (!resultWithdrawAccount.isResult()) {
            model.addAttribute("errorMessage", "Insufficient funds");
        } else {
            model.addAttribute("successMessage", "Withdrawal successful");
        }

        return "bank";
    }

    @PostMapping("/bank/deposit")
    public String depositAmount(@RequestParam("depositAmount") Double depositAmount,
                                @ModelAttribute("accountDto") AccountDto accountDto,
                                Model model) {
        User user = userService.getLoggedUser();
        ResultDebitAccount resultDebitAccount = accountService.debitAccount(depositAmount);
        prepareAccountInfo(user, model);

        if (!resultDebitAccount.isResult()) {
            model.addAttribute("errorMessage", resultDebitAccount.getMessage());
        } else {
            model.addAttribute("successMessage", resultDebitAccount.getMessage());
        }

        return "bank";
    }

    @PostMapping("/bank/update")
    public String updateAccount(@ModelAttribute("accountDto") AccountDto accountDto,
                                BindingResult result,
                                Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();
        ResultUpdateAccount resultUpdateAccount = accountService.updateIban(userId, accountDto.getIban());
        prepareAccountInfo(user, model);
        if (!resultUpdateAccount.isResult()) {
            model.addAttribute("errorMessage", resultUpdateAccount.getMessage());
        } else {
            model.addAttribute("successMessage", resultUpdateAccount.getMessage());
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


    private void prepareAccountInfo(User user, Model model) {
        Long userId = user.getId();

        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }

        if (accountService.checkIfUserAccountExists(userId)) {
            Account account = accountService.getBankAccountInformation(userId);
            model.addAttribute("account", account);
        }

        updateUserInfo(model);
    }

    private Account createAccount(AccountDto accountDto, User user) {
        Account account = new Account();
        account.setName(accountDto.getName());
        account.setIban(accountDto.getIban());
        account.setBalance(accountDto.getBalance());
        account.setDescription(accountDto.getDescription());
        account.setUser(user);
        return account;
    }

    private void updateUserInfo(Model model) {
        User user = userService.getLoggedUser();
        model.addAttribute("balance", user.getBalance());
        model.addAttribute("name", user.getName());
        model.addAttribute("user", user);
    }
}