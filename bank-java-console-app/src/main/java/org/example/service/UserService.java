package org.example.service;

import org.example.model.User;

import java.util.ArrayList;

public class UserService {
    public static int getUserSaldo(ArrayList<User> users, String accountNumber) {
        var currentUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        return currentUser.getSaldo();
    }

    public static boolean loginUser(ArrayList<User> users, String accountNumber, String password) {
        var currentUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        if (currentUser == null) {
            return false;
        }

        boolean isPasswordCorrect = currentUser.getPassword().equals(password);
        if (isPasswordCorrect) {
            return true;
        }

        return false;
    }
}
