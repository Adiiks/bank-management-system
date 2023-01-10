package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.AccountInformation;
import com.adrian.bank.management.system.dto.TransferRequest;
import com.adrian.bank.management.system.security.AuthenticationFacade;
import com.adrian.bank.management.system.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AuthenticationFacade authFacade;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public AccountInformation getAccountInformation(@PathVariable Integer accountId) {
        String username = authFacade.getAuthentication().getName();

        return accountService.getAccount(username, accountId);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{accountId}/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void makeTransfer(@PathVariable Integer accountId, @Valid @RequestBody TransferRequest transfer) {
        String username = authFacade.getAuthentication().getName();

        accountService.makeTransfer(username, accountId, transfer);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{accountId}/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@PathVariable Integer accountId, @RequestParam BigDecimal amount) {
        if (!(amount.compareTo(BigDecimal.valueOf(0)) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Amount has to be positive");
        }

        String username = authFacade.getAuthentication().getName();

        accountService.withdraw(accountId, username, amount);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{accountId}/deposit")
    @ResponseStatus(HttpStatus.OK)
    public void deposit(@PathVariable Integer accountId, @RequestParam Integer amount) {
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Amount has to be positive");
        }

        String username = authFacade.getAuthentication().getName();

        accountService.deposit(accountId, username, BigDecimal.valueOf(amount));
    }
}
