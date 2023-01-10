package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.security.JwtUtils;
import com.adrian.bank.management.system.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    AuthController authController;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserService userService;

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        authController = new AuthController(authenticationManager, jwtUtils, userService);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new ValidationExceptionHandler())
                .build();
    }

    @DisplayName("Register new customer - Validation Failed")
    @Test
    void registerCustomerValidationFailed() throws Exception {
        UserProfile userRequest = new UserProfile("", "", "2222", "aaaaaa");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register-customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(4)));

        verify(userService, times(0)).createUser(any());
    }

    @DisplayName("Register new customer")
    @Test
    void registerCustomer() throws Exception {
        UserProfile userRequest = buildValidUserRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register-customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any());
    }

    private UserProfile buildValidUserRequest() {
        return new UserProfile("username", "Jan Kowalski",
                "546123567", "jan.kowalski@wp.pl");
    }
}