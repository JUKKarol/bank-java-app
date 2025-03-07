package org.example.controller;

import org.example.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class UserController {
    @PostMapping("/login")
    public String login(String accountNumber, String password) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "1", 1000, "1"));
        users.add(new User("user2", "2", 1000, "2"));
        users.add(new User("user3", "3", 1000, "3"));
        users.add(new User("user4", "4", 1000, "4"));

        var currentUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        if (currentUser == null) {
            return "incorrect login or password";
        }

        boolean isPasswordCorrect = currentUser.getPassword().equals(password);
        if (isPasswordCorrect) {
            return "Logged in";
        }

        return "incorrect login or password";
    }

    @GetMapping("/saldo/{accountNumber}")
    public ResponseEntity<Integer> getSaldo(@PathVariable String accountNumber) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "1", 1000, "1"));
        users.add(new User("user2", "2", 1000, "2"));
        users.add(new User("user3", "3", 1000, "3"));
        users.add(new User("user4", "4", 1000, "4"));

        var currentUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(currentUser.getSaldo(), HttpStatus.OK);
    }
}

