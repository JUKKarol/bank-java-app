package com.github.jukkarol.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Long userId;
    private final String email;

    public JwtAuthenticationToken(UserDetails principal, Long userId, String email,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}