package com.adrian.bank.management.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(@NotNull(message = "You have to pass account id on which you want to transfer money")
                              Integer accountIdTarget,
                              @NotNull @Min(value = 1) BigDecimal value) {
}
