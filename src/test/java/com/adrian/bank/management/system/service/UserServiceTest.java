package com.adrian.bank.management.system.service;

import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.entity.ERole;
import com.adrian.bank.management.system.entity.Role;
import com.adrian.bank.management.system.entity.User;
import com.adrian.bank.management.system.repository.RoleRepository;
import com.adrian.bank.management.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @DisplayName("Get user information")
    @Test
    void getUserDetails() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(buildUser()));

        UserProfile userProfile = userService.getUserDetails("customer");

        assertNotNull(userProfile);

        assertEquals("customer", userProfile.username());
    }

    @DisplayName("Update user information")
    @Test
    void updateUserDetails() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(buildUser()));

        userService.updateUserDetails(buildUserDto(), "customer");

        verify(userRepository, times(0)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any());
    }

    @DisplayName("Update user information - Failed - Username already exists")
    @Test
    void updateUserDetailsUsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> userService.updateUserDetails(buildUserDto(), "admin"));

        verify(userRepository, times(0)).findByUsername(anyString());
        verify(userRepository, times(0)).save(any());
    }

    @DisplayName("Update user information - Failed - Username not found")
    @Test
    void updateUserDetailsUsernameNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> userService.updateUserDetails(buildUserDto(), "customer"));

        verify(userRepository, times(0)).existsByUsername(anyString());
        verify(userRepository, times(0)).save(any());
    }

    private User buildUser() {
        return User.builder()
                .username("customer")
                .password("password")
                .build();
    }

    private UserProfile buildUserDto() {
        return new UserProfile("customer", "Krzysztof Chluba", "567622456",
                "krzysztof@wp.pl");
    }

    @DisplayName("Update user password")
    @Test
    void updatePassword() {
        User user = buildUser();
        String newPassword = "customer";

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        userService.updatePassword("customer", newPassword);

        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));

        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Create user - Username already exists")
    @Test
    void createUserUsernameAlreadyExists() {
        UserProfile userToCreate = buildUserDto();

        when(userRepository.existsByUsername(anyString()))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                userService.createUser(userToCreate));

        verify(userRepository, times(1))
                .existsByUsername(anyString());

        verify(roleRepository, times(0))
                .findByName(any());

        verify(userRepository, times(0))
                .save(any());
    }

    @DisplayName("Create user")
    @Test
    void createUser() {
        UserProfile userToCreate = buildUserDto();
        Role roleFromDb = new Role(ERole.ROLE_CUSTOMER);

        when(userRepository.existsByUsername(anyString()))
                .thenReturn(false);

        when(roleRepository.findByName(any()))
                .thenReturn(roleFromDb);

        userService.createUser(userToCreate);

        verify(userRepository, times(1))
                .existsByUsername(anyString());

        verify(roleRepository, times(1))
                .findByName(any());

        verify(userRepository, times(1))
                .save(any());
    }
}