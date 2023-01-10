package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.TransactionDTO;
import com.adrian.bank.management.system.entity.TransactionType;
import com.adrian.bank.management.system.security.AuthenticationFacade;
import com.adrian.bank.management.system.service.TransactionService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    AuthenticationFacade authFacade;

    @Mock
    TransactionService transactionService;

    MockMvc mockMvc;

    TransactionController transactionController;

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        transactionController = new TransactionController(authFacade, transactionService);

        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @DisplayName("Get user's personal history of transactions for given account")
    @Test
    void getPersonalHistoryOfTransactions() throws Exception {
        Page<TransactionDTO> transactionsFromTransactionService = new PageImpl<>(
                List.of(buildTransactionDTO()),
                Pageable.unpaged(),
                1
        );

        when(authFacade.getAuthentication()).thenReturn(AuthenticationUtility.buildAuthenticatedUser());
        when(transactionService.getTransactionsForUserAccount(anyString(), anyInt(), any()))
                .thenReturn(transactionsFromTransactionService);

        mockMvc.perform(get("/api/transactions/1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(objectMapper.writeValueAsString(transactionsFromTransactionService)));
    }

    private TransactionDTO buildTransactionDTO() {
        return new TransactionDTO(1, LocalDateTime.now(), BigDecimal.valueOf(200), TransactionType.TRANSFER);
    }
}