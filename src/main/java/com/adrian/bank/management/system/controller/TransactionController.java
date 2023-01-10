package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.TransactionDTO;
import com.adrian.bank.management.system.security.AuthenticationFacade;
import com.adrian.bank.management.system.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions/{accountId}")
@RequiredArgsConstructor
public class TransactionController {

    private final AuthenticationFacade authFacade;

    private final TransactionService transactionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TransactionDTO> getPersonalHistoryOfTransactions(@PathVariable Integer accountId, Pageable pageable) {
        String username = authFacade.getAuthentication().getName();

        return transactionService.getTransactionsForUserAccount(username, accountId, pageable);
    }
}
