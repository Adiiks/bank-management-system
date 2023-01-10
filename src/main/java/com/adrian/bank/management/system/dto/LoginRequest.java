package com.adrian.bank.management.system.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "Username is required") String username,
                           @NotBlank(message = "Password is required") String password) {
}
