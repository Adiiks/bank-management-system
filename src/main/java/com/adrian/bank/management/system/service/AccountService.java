package com.adrian.bank.management.system.service;

import com.adrian.bank.management.system.converter.AccountConverter;
import com.adrian.bank.management.system.dto.AccountInformation;
import com.adrian.bank.management.system.dto.TransferRequest;
import com.adrian.bank.management.system.entity.Account;
import com.adrian.bank.management.system.entity.Transaction;
import com.adrian.bank.management.system.entity.TransactionType;
import com.adrian.bank.management.system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final TransactionService transactionService;

    public AccountInformation getAccount(String username, Integer accountId) {
        return accountRepository.findByIdAndUserUsername(accountId, username)
                .map(AccountConverter::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found for user: " + username + " with accountId: " + accountId));
    }

    @Transactional
    public void makeTransfer(String username, Integer accountId, TransferRequest transfer) {
        // Get from db sender account - check if user has account
        Account senderAccount = accountRepository.findByIdAndUserUsername(accountId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found for user: " + username + " with accountId: " + accountId));

        // Check if sender has enough money to make transfer
        if (senderAccount.getBalance().compareTo(transfer.value()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User doesn't have enough money to make a transfer.");
        }

        // Get from db receiverAccount - check if account exist
        Account receiverAccount = accountRepository.findById(transfer.accountIdTarget())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Receiver account with accountId: " + transfer.accountIdTarget() + " not found."));

        // Update sender and receiver accounts and save them to db
        senderAccount.subtractFromBalance(transfer.value());
        receiverAccount.addToBalance(transfer.value());

        accountRepository.saveAll(List.of(senderAccount, receiverAccount));

        // Create and save transaction for sender
        Transaction transferTransactionSender = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(transfer.value())
                .account(senderAccount)
                .dateTime(LocalDateTime.now())
                .build();

        transactionService.saveTransaction(transferTransactionSender);
    }

    @Transactional
    public void withdraw(Integer accountId, String username, BigDecimal amount) {
        // get user's account from db and check if it exists
        Account account = accountRepository.findByIdAndUserUsername(accountId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found for user: " + username + " with accountId: " + accountId));

        // check if user has enough money on his account to withdraw
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User doesn't have enough money to make a transfer.");
        }

        // update balance on user's account
        account.subtractFromBalance(amount);

        accountRepository.save(account);

        // create and save transaction for withdraw
        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(amount)
                .account(account)
                .dateTime(LocalDateTime.now())
                .build();

        transactionService.saveTransaction(transaction);
    }

    @Transactional
    public void deposit(Integer accountId, String username, BigDecimal amount) {
        // get user's account from db and check if it exists
        Account account = accountRepository.findByIdAndUserUsername(accountId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found for user: " + username + " with accountId: " + accountId));

        // update user account
        account.addToBalance(amount);

        accountRepository.save(account);

        // create and save transaction for deposit
        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .account(account)
                .dateTime(LocalDateTime.now())
                .build();

        transactionService.saveTransaction(transaction);
    }
}
