package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.LoginRequest;
import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.security.JwtUtils;
import com.adrian.bank.management.system.security.UserDetailsImpl;
import com.adrian.bank.management.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        return jwtUtils.generateJwt(userDetails);
    }

    @PreAuthorize("hasRole('ROLE_TELLER')")
    @PostMapping("/register-customer")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerCustomer(@Valid @RequestBody UserProfile user) {
        userService.createUser(user);
    }
}
