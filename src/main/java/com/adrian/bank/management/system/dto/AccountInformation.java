package com.adrian.bank.management.system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountInformation(Integer id,
                                 LocalDate openingDate,
                                 BigDecimal balance) {
}
