package com.adrian.bank.management.system.service;

import com.adrian.bank.management.system.dto.AccountInformation;
import com.adrian.bank.management.system.dto.TransferRequest;
import com.adrian.bank.management.system.entity.Account;
import com.adrian.bank.management.system.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionService transactionService;

    AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, transactionService);
    }

    @DisplayName("Get account information")
    @Test
    void getAccount() {
        String username = "customer";
        Integer accountId = 1;

        Account accountFromDb = buildAccount();

        when(accountRepository.findByIdAndUserUsername(accountId, username))
                .thenReturn(Optional.of(accountFromDb));

        AccountInformation accountInformation = accountService.getAccount(username, accountId);

        assertNotNull(accountInformation);
        assertEquals(accountFromDb.getBalance(), accountInformation.balance());
        assertEquals(accountFromDb.getId(), accountInformation.id());
        assertEquals(accountFromDb.getOpeningDate(), accountInformation.openingDate());
    }

    @DisplayName("Get account information - Failed - Account not found")
    @Test
    void getAccountAccountNotFound() {
        String username = "customer";
        Integer accountId = 1;

        when(accountRepository.findByIdAndUserUsername(accountId, username))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> accountService.getAccount(username, accountId));
    }

    @DisplayName("Make transfer - Sender's account not found")
    @Test
    void makeTransferSenderAccountNotFound() {
        TransferRequest transfer = new TransferRequest(2, BigDecimal.valueOf(200));

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> accountService.makeTransfer("username", 1, transfer));

        verify(accountRepository, times(0)).findById(anyInt());
        verify(accountRepository, times(0)).saveAll(anyCollection());
        verify(transactionService, times(0)).saveTransaction(any());
    }

    @DisplayName("Make transfer - Sender doesn't have enough money on account")
    @Test
    void makeTransferSenderDoesntHabeEnoughMoney() {
        TransferRequest transfer = new TransferRequest(2, BigDecimal.valueOf(200));
        Account senderAccount = buildAccount();
        senderAccount.setBalance(BigDecimal.valueOf(0));

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString()))
                .thenReturn(Optional.of(senderAccount));

        assertThrows(ResponseStatusException.class,
                () -> accountService.makeTransfer("username", 1, transfer));

        verify(accountRepository, times(0)).findById(anyInt());
        verify(accountRepository, times(0)).saveAll(anyCollection());
        verify(transactionService, times(0)).saveTransaction(any());
    }

    @DisplayName("Make transfer - Receiver account doesn't exist")
    @Test
    void makeTransferReceiverAccountDoesntExist() {
        TransferRequest transfer = new TransferRequest(2, BigDecimal.valueOf(200));
        Account senderAccount = buildAccount();

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString()))
                .thenReturn(Optional.of(senderAccount));

        when(accountRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> accountService.makeTransfer("username", 1, transfer));

        verify(accountRepository, times(1)).findById(anyInt());
        verify(accountRepository, times(0)).saveAll(anyCollection());
        verify(transactionService, times(0)).saveTransaction(any());
    }

    @DisplayName("Make transfer")
    @Test
    void makeTransfer() {
        TransferRequest transfer = new TransferRequest(2, BigDecimal.valueOf(200));

        Account senderAccount = buildAccount();
        senderAccount.setBalance(BigDecimal.valueOf(200));

        Account receiverAccount = buildAccount();
        receiverAccount.setBalance(BigDecimal.valueOf(0));

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString()))
                .thenReturn(Optional.of(senderAccount));

        when(accountRepository.findById(anyInt()))
                .thenReturn(Optional.of(receiverAccount));

        accountService.makeTransfer("username", 1, transfer);

        assertEquals(BigDecimal.valueOf(0), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(200), receiverAccount.getBalance());

        verify(accountRepository, times(1)).findById(anyInt());
        verify(accountRepository, times(1)).saveAll(anyCollection());
        verify(transactionService, times(1)).saveTransaction(any());
    }

    @DisplayName("Withdraw money - there's not enough money on user account")
    @Test
    void withdrawNotEnoughMoney() {
        Account account = buildAccount();
        account.setBalance(BigDecimal.valueOf(0));

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString()))
                .thenReturn(Optional.of(account));

        assertThrows(ResponseStatusException.class, () ->
                accountService.withdraw(1, "username", BigDecimal.valueOf(200)));

        verify(accountRepository, times(1))
                .findByIdAndUserUsername(anyInt(), anyString());

        verify(accountRepository, times(0))
                .save(any());

        verify(transactionService, times(0))
                .saveTransaction(any());
    }

    @DisplayName("Withdraw money")
    @Test
    void withdraw() {
        Account account = buildAccount();
        account.setBalance(BigDecimal.valueOf(200));

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString()))
                .thenReturn(Optional.of(account));

        accountService.withdraw(1, "username", BigDecimal.valueOf(200));

        assertEquals(BigDecimal.ZERO, account.getBalance());

        verify(accountRepository, times(1))
                .findByIdAndUserUsername(anyInt(), anyString());

        verify(accountRepository, times(1))
                .save(any());

        verify(transactionService, times(1))
                .saveTransaction(any());
    }

    @DisplayName("Deposit money")
    @Test
    void deposit() {
        Account account = buildAccount();
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByIdAndUserUsername(anyInt(), anyString()))
                .thenReturn(Optional.of(account));

        accountService.deposit(1, "username", BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(200), account.getBalance());

        verify(accountRepository, times(1))
                .findByIdAndUserUsername(anyInt(), anyString());

        verify(accountRepository, times(1))
                .save(any());

        verify(transactionService, times(1))
                .saveTransaction(any());
    }

    private Account buildAccount() {
        return Account.builder()
                .id(1)
                .openingDate(LocalDate.now())
                .balance(BigDecimal.valueOf(4500))
                .build();
    }
}