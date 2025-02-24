package org.example.service;

import org.example.dto.TransferResult;
import org.example.model.User;

import java.util.ArrayList;

public class TransactionService {
    public static int checkBalance(ArrayList<User> users, String accountNumber) {
        var currentUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        return currentUser.getSaldo();
    }

    public static TransferResult makeTransfer(ArrayList<User> users, String fromAccountNumber, String toAccountNumber, int amount) {
        var fromtUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(fromAccountNumber))
                .findFirst()
                .orElse(null);

        var toUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(toAccountNumber))
                .findFirst()
                .orElse(null);

        if (fromtUser.getSaldo() < amount)
        {
            return new TransferResult(users, false);
        }

        fromtUser.setSaldo(fromtUser.getSaldo() - amount);
        toUser.setSaldo(toUser.getSaldo() + amount);

        return new TransferResult(users, true);
    }
}
