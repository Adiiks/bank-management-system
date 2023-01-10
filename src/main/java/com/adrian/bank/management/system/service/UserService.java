package com.adrian.bank.management.system.service;

import com.adrian.bank.management.system.converter.UserConverter;
import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.entity.ERole;
import com.adrian.bank.management.system.entity.Role;
import com.adrian.bank.management.system.entity.User;
import com.adrian.bank.management.system.repository.RoleRepository;
import com.adrian.bank.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserProfile getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .map(UserConverter::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with username " + username));
    }

    @Transactional
    public void updateUserDetails(UserProfile userDetails, String username) {

        // check if new username is used by another user
        if (!userDetails.username().equals(username) && userRepository.existsByUsername(userDetails.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username: " + userDetails.username() + " it's not available");
        }

        // get user from db
        User user = getUserFromDb(username);

        // update user information
        user.setEmail(userDetails.email());
        user.setName(userDetails.name());
        user.setUsername(userDetails.username());
        user.setPhone(userDetails.phone());

        // save user to db
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String username, String password) {
        User user = getUserFromDb(username);

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    private User getUserFromDb(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with username: " + username + " not found."));
    }

    @Transactional
    public void createUser(UserProfile user) {
        // check if username is unique
        if (userRepository.existsByUsername(user.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username: " + user.username() + " it's not available");
        }

        // generate random password for user
        String generatedPassword = RandomStringUtils.random(10, true, true);

        // get role Customer from db
        Role role = roleRepository.findByName(ERole.ROLE_CUSTOMER);

        // create and save user
        User newUser = User.builder()
                .password(passwordEncoder.encode(generatedPassword))
                .username(user.username())
                .role(role)
                .email(user.email())
                .name(user.name())
                .phone(user.phone())
                .registrationDate(LocalDate.now())
                .build();

        userRepository.save(newUser);
    }
}
