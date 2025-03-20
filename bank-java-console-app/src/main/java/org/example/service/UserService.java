package org.example.service;

import org.example.dto.userDto.CreateUserRequest;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
