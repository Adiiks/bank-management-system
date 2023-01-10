package com.adrian.bank.management.system.converter;

import com.adrian.bank.management.system.dto.UserProfile;
import com.adrian.bank.management.system.entity.User;

public class UserConverter {

    public static UserProfile convertToDto(User user) {
        return new UserProfile(user.getUsername(), user.getName(), user.getPhone(), user.getEmail());
    }
}
