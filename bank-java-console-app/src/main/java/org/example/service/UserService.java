package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.userDto.CreateUserRequest;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Integer getUserBalance(String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(User::getBalance).orElse(0);

    }

    public boolean loginUser(String accountNumber, String password) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(value -> value.getPassword().equals(password)).orElse(false);
    }

    public User addUser(CreateUserRequest userDto) {
        User user = new User(null, userDto.getName(), userDto.getEmail(), userDto.getPassword(), userDto.getBalance(), userDto.getAccountNumber(), null, null);
        return userRepository.save(user);
    }
}
