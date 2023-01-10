package com.adrian.bank.management.system.service;

import com.adrian.bank.management.system.dto.TransactionDTO;
import com.adrian.bank.management.system.entity.Transaction;
import com.adrian.bank.management.system.entity.TransactionType;
import com.adrian.bank.management.system.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    TransactionService transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository);
    }

    @DisplayName("Get all transactions for given user's account")
    @Test
    void getTransactionsForUserAccount() {
        Transaction transactionFromDb = buildTransaction();

        when(transactionRepository.findByAccount_IdAndAccount_User_Username(anyInt(), anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(transactionFromDb), Pageable.unpaged(), 1));

        Page<TransactionDTO> transactionDTOPage =
                transactionService.getTransactionsForUserAccount("username", 1, Pageable.unpaged());

        assertNotNull(transactionDTOPage);
        assertEquals(1, transactionDTOPage.getContent().size());
        assertEquals(1, transactionDTOPage.getTotalElements());
    }

    private Transaction buildTransaction() {
        return Transaction.builder()
                .id(1)
                .amount(BigDecimal.valueOf(200))
                .dateTime(LocalDateTime.now())
                .type(TransactionType.TRANSFER)
                .build();
    }

    @DisplayName("Save transaction to db")
    @Test
    void saveTransaction() {
        transactionService.saveTransaction(new Transaction());

        verify(transactionRepository, times(1)).save(any());
    }
}