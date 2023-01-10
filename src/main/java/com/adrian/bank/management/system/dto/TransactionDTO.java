package com.adrian.bank.management.system.dto;

import com.adrian.bank.management.system.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(Integer id,
                             LocalDateTime dateTime,
                             BigDecimal amount,
                             TransactionType type) {
}
