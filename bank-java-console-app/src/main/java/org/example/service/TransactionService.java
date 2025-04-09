package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TransactionService {
    private final UserRepository userRepository;

    public boolean makeTransferBetweenUsers(String fromAccountNumber, String toAccountNumber, int amount) {
        Optional<User> fromUserOptional = userRepository.findByAccountNumber(fromAccountNumber);
        Optional<User> toUserOptional = userRepository.findByAccountNumber(toAccountNumber);

        if (fromUserOptional.isEmpty() || toUserOptional.isEmpty()) {
            return false;
        }

        User fromUser = fromUserOptional.get();
        User toUser = toUserOptional.get();

        if (fromUser.getBalance() < amount) {
            return false;
        }

        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        return true;
    }

    public Integer getUserBalance(String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(User::getBalance).orElse(0);

    }
}
