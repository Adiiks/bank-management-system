package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.entity.User;
import com.adrian.bank.management.system.security.AuthenticationFacade;
import com.adrian.bank.management.system.service.UserService;
import com.adrian.bank.management.system.utility.AuthenticationUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    MockMvc mockMvc;

    @Mock
    UserService userService;

    @Mock
    AuthenticationFacade authFacade;

    UserController userController;

    ObjectMapper objectMapper = new ObjectMapper();

    Authentication authenticatedUser = AuthenticationUtility.buildAuthenticatedUser();

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, authFacade);

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        lenient().when(authFacade.getAuthentication()).thenReturn(authenticatedUser);
    }

    @DisplayName("Get information about user")
    @Test
    void getUserDetails() throws Exception {
        UserProfile userDetails = buildUserDto();

        when(userService.getUserDetails(anyString())).thenReturn(userDetails);


        mockMvc.perform(get("/api/users/user-details"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userDetails)));
    }

    @DisplayName("Get information about user - User not found")
    @Test
    void getUserDetailsUserNotFound() throws Exception {
        when(userService.getUserDetails(anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


        mockMvc.perform(get("/api/users/user-details"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Update information about user")
    @Test
    void updateUserDetails() throws Exception {

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUserDto())))
                .andExpect(status().isOk());
    }

    @DisplayName("Update information about user - User not found")
    @Test
    void updateUserDetailsUserNotFound() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService)
                        .updateUserDetails(any(), anyString());

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUserDto())))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Update information about user - Username already exist")
    @Test
    void updateUserDetailsUsernameAlreadyExist() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
                .when(userService)
                .updateUserDetails(any(), anyString());

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUserDto())))
                .andExpect(status().isConflict());
    }

    @DisplayName("Update information about user - Validation Failed - Username is blank")
    @Test
    void updateUserDetailsUsernameIsBlank() throws Exception {
        UserProfile userInformationRequest = new UserProfile("", "Adrian", "543435678",
                "adrian@wp.pl");

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInformationRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update information about user - Validation Failed - Name is blank")
    @Test
    void updateUserDetailsNameIsBlank() throws Exception {
        UserProfile userInformationRequest = new UserProfile("customer", "", "543435678",
                "adrian@wp.pl");

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInformationRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update information about user - Validation Failed - Phone is invalid")
    @Test
    void updateUserDetailsPhoneIsInvalid() throws Exception {
        UserProfile userInformationRequest = new UserProfile("customer", "Adrian", "54343",
                "adrian@wp.pl");

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInformationRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update information about user - Validation Failed - Email is invalid")
    @Test
    void updateUserDetailsEmailIsInvalid() throws Exception {
        UserProfile userInformationRequest = new UserProfile("customer", "Adrian", "543435678",
                "adrianwpl");

        mockMvc.perform(put("/api/users/user-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInformationRequest)))
                .andExpect(status().isBadRequest());
    }

    private UserProfile buildUserDto() {
        return new UserProfile("customer", "Krzysztof Chluba", "567622456",
                "krzysztof@wp.pl");
    }

    @DisplayName("Update user password")
    @Test
    void updateUserPassword() throws Exception {
        mockMvc.perform(patch("/api/users/password")
                .content("password"))
                .andExpect(status().isOk());
    }
}