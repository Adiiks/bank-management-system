package com.adrian.bank.management.system.controller;

import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.security.AuthenticationFacade;
import com.adrian.bank.management.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationFacade authFacade;

    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user-details")
    public UserProfile getUserDetails() {
        String username = authFacade.getAuthentication().getName();

        return userService.getUserDetails(username);
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/user-details")
    public void updateUserDetails(@Valid @RequestBody UserProfile userProfile) {
        String username = authFacade.getAuthentication().getName();

        userService.updateUserDetails(userProfile, username);
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/password")
    public void updateUserPassword(@RequestBody String password) {
        String username = authFacade.getAuthentication().getName();

        userService.updatePassword(username, password);
    }
}
