package com.github.jukkarol.service;

import com.github.jukkarol.dto.userDto.request.LoginUserRequest;
import com.github.jukkarol.dto.userDto.request.RegisterUserRequest;
import com.github.jukkarol.dto.userDto.response.RegisterUserResponse;
import com.github.jukkarol.model.User;
import com.github.jukkarol.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@AllArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RegisterUserResponse signup(RegisterUserRequest input) {
        Random generator = new Random();

        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setBalance(1000);
        String accountNumber = String.format("%010d", generator.nextLong(1_000_000_0000L));
        user.setAccountNumber(accountNumber);

        User createdUser =  userRepository.save(user);

        RegisterUserResponse userResponse = new RegisterUserResponse();
        userResponse.setId(createdUser.getId());
        userResponse.setName(createdUser.getName());
        userResponse.setEmail(createdUser.getEmail());
        userResponse.setBalance(createdUser.getBalance());
        userResponse.setAccountNumber(createdUser.getAccountNumber());
        userResponse.setAccountNumber(createdUser.getAccountNumber());
        userResponse.setCreatedAt(createdUser.getCreatedAt());
        userResponse.setUpdatedAt(createdUser.getUpdatedAt());

        return userResponse;
    }

    public UserDetails authenticate(LoginUserRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}