package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.AccountInformation;
import com.adrian.bank.management.system.dto.TransferRequest;
import com.adrian.bank.management.system.security.AuthenticationFacade;
import com.adrian.bank.management.system.service.AccountService;
import com.adrian.bank.management.system.utility.AuthenticationUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    MockMvc mockMvc;

    @Mock
    AccountService accountService;

    @Mock
    AuthenticationFacade authFacade;

    AccountController accountController;

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        accountController = new AccountController(accountService, authFacade);

        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new ValidationExceptionHandler())
                .build();
    }

    @DisplayName("Get information about user's account")
    @Test
    void getAccountInformation() throws Exception {
        AccountInformation accountInformation = buildAccountInformation();

        when(authFacade.getAuthentication()).thenReturn(AuthenticationUtility.buildAuthenticatedUser());
        when(accountService.getAccount(anyString(), anyInt())).thenReturn(accountInformation);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(accountInformation)));
    }

    @DisplayName("Get information about user's account - Account not found")
    @Test
    void getAccountInformationFailedAccountNotFound() throws Exception {
        when(authFacade.getAuthentication()).thenReturn(AuthenticationUtility.buildAuthenticatedUser());
        when(accountService.getAccount(anyString(), anyInt()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound());
    }

    private AccountInformation buildAccountInformation() {
        return new AccountInformation(1, LocalDate.now(), BigDecimal.valueOf(4500));
    }

    @DisplayName("Make transfer from one account to another")
    @Test
    void makeTransfer() throws Exception {
        TransferRequest transfer = new TransferRequest(1, BigDecimal.valueOf(200));

        when(authFacade.getAuthentication()).thenReturn(AuthenticationUtility.buildAuthenticatedUser());

        mockMvc.perform(patch("/api/accounts/1/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).makeTransfer(anyString(), anyInt(), any());
    }

    @DisplayName("Make transfer from one account to another - Validation failed")
    @Test
    void makeTransferValidationFailed() throws Exception {
        TransferRequest transfer = new TransferRequest(null, BigDecimal.valueOf(0));

        mockMvc.perform(patch("/api/accounts/1/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));

        verify(accountService, times(0)).makeTransfer(anyString(), anyInt(), any());
    }

    @DisplayName("Withdraw money - Validation Failed - Amount not positive")
    @Test
    void withdrawValidationFailed() throws Exception {
        mockMvc.perform(patch("/api/accounts/1/withdraw")
                .param("amount", "-200"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Withdraw money")
    @Test
    void withdraw() throws Exception {
        when(authFacade.getAuthentication()).thenReturn(AuthenticationUtility.buildAuthenticatedUser());

        mockMvc.perform(patch("/api/accounts/1/withdraw")
                        .param("amount", "200"))
                .andExpect(status().isOk());

        verify(accountService, times(1)).withdraw(anyInt(), anyString(), any());
    }

    @DisplayName("Deposit money - Validation Failed - Amount not positive")
    @Test
    void depositValidationFailed() throws Exception {
        mockMvc.perform(patch("/api/accounts/1/deposit")
                .param("amount", "-200"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Deposit money")
    @Test
    void deposit() throws Exception {
        when(authFacade.getAuthentication())
                .thenReturn(AuthenticationUtility.buildAuthenticatedUser());

        mockMvc.perform(patch("/api/accounts/1/deposit")
                        .param("amount", "200"))
                .andExpect(status().isOk());

        verify(accountService, times(1))
                .deposit(anyInt(), anyString(), any());
    }
}