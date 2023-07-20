package com.example.demo.controller;

import com.example.demo.dto.AccountDto;
import com.example.demo.entity.Account;
import com.example.demo.entity.User;
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
import java.math.BigDecimal;

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

        if(user.getBalance() == null){
            user.setBalance(0.0);
        }

        if (accountService.checkIfUserAccountExists(userId)) {
            Account account = accountService.getBankAccountInformation(userId);
            model.addAttribute("account", account);
        }

        AccountDto accountDto = new AccountDto();
        model.addAttribute("accountDto", accountDto);

        model.addAttribute("user", user); // Ajouter l'utilisateur au modèle

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
        return "redirect:/bank?success";
    }

    @PostMapping("/bank/withdraw")
    public String withdrawAmount(@RequestParam("withdrawAmount") Double withdrawAmount, @ModelAttribute("accountDto") AccountDto accountDto,
                                 Model model) {
        accountService.withdrawAccount(withdrawAmount);
        User user = userService.getLoggedUser();
        Account account = accountService.getBankAccountInformation(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("account", account);
        return "bank";
    }


    @PostMapping("/bank/deposit")
    public String depositAmount(@RequestParam("depositAmount") Double depositAmount,@ModelAttribute("accountDto") AccountDto accountDto,
                                Model model) {
        accountService.debitAccount(depositAmount);
        User user = userService.getLoggedUser();
        Account account = accountService.getBankAccountInformation(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("account", account);
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
    public String updateIban(@RequestParam("iban") int newIban, Model model) {
        User user = userService.getLoggedUser();
        Long userId = user.getId();

        if (!accountService.checkIfUserAccountExists(userId)) {
            throw new IllegalArgumentException("There is no existing account for this user");
        }

        Account account = accountService.getBankAccountInformation(userId);
        account.setIban(newIban);

        accountService.saveBankAccountInformation(account);
        model.addAttribute("account", account);
        model.addAttribute("successMessage", "IBAN updated successfully");
        return "redirect:/bank";
    }

}
