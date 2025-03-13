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

    public int getUserSaldo(String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(User::getSaldo).orElse(0);

    }

    public boolean loginUser(String accountNumber, String password) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        if(user.isEmpty())
        {
            return false;
        }

        return user.get().getPassword().equals(password);
    }

    public User addUser(String name, String password, int saldo, String accountNumber) {
        User user = new User(null, name, password, saldo, accountNumber);
        return userRepository.save(user);
    }
}
