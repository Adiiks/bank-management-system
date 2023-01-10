package com.adrian.bank.management.system.dto;

import com.adrian.bank.management.system.validation.ContactNumberConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfile(@NotBlank(message = "Username is required") String username,
                          @NotBlank(message = "Name is required") String name,
                          @ContactNumberConstraint String phone,
                          @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email) {
}
