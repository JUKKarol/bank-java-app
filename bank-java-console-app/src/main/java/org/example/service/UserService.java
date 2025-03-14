package org.example.service;

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

    public int getUserBalance(String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(User::getBalance).orElse(0);

    }

    public boolean loginUser(String accountNumber, String password) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(value -> value.getPassword().equals(password)).orElse(false);

    }

    public User addUser(String name, String password, int balance, String accountNumber) {
        User user = new User(null, name, password, balance, accountNumber);
        return userRepository.save(user);
    }
}
