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

    public static TransferResult makeTransferBetweenUsers(ArrayList<User> users, String fromAccountNumber, String toAccountNumber, int amount) {
        User fromUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(fromAccountNumber))
                .findFirst()
                .orElse(null);

        User toUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(toAccountNumber))
                .findFirst()
                .orElse(null);

        if (fromUser == null || toUser == null) {
            System.out.println("Błąd: Jedno z kont nie istnieje.");
            return new TransferResult(users, false);
        }

        if (fromUser.getSaldo() < amount) {
            System.out.println("Błąd: Brak wystarczających środków na koncie " + fromAccountNumber);
            return new TransferResult(users, false);
        }

        fromUser.setSaldo(fromUser.getSaldo() - amount);
        toUser.setSaldo(toUser.getSaldo() + amount);
        System.out.println("Przelew zakończony sukcesem: " + amount + " z " + fromAccountNumber + " do " + toAccountNumber);

        return new TransferResult(users,true);
    }
}
