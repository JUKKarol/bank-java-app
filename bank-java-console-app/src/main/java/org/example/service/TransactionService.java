package org.example.service;

import org.example.dto.TransferResult;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TransactionService {
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean makeTransferBetweenUsers(String fromAccountNumber, String toAccountNumber, int amount) {
        Optional<User> fromUserOptional = userRepository.findByAccountNumber(fromAccountNumber);
        Optional<User> toUserOptional = userRepository.findByAccountNumber(toAccountNumber);

        if (fromUserOptional.isEmpty() || toUserOptional.isEmpty()) {
            return false;
        }

        User fromUser = fromUserOptional.get();
        User toUser = toUserOptional.get();

        if (fromUser.getSaldo() < amount) {
            return false;
        }

        fromUser.setSaldo(fromUser.getSaldo() - amount);
        toUser.setSaldo(toUser.getSaldo() + amount);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        return true;
    }
}
