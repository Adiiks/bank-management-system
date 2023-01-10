package com.adrian.bank.management.system.utility;

import com.adrian.bank.management.system.entity.ERole;
import com.adrian.bank.management.system.entity.Role;
import com.adrian.bank.management.system.entity.User;
import com.adrian.bank.management.system.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationUtility {

    public static Authentication buildAuthenticatedUser() {
        User user = User.builder()
                .username("customer")
                .password("password")
                .role(new Role(ERole.ROLE_CUSTOMER))
                .build();

        UserDetails userDetails = UserDetailsImpl.build(user);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
