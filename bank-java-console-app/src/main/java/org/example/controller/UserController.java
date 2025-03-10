package org.example.controller;

import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import org.example.service.UserService;

@RestController
@RequestMapping("/api/{controllerName}")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity login(String accountNumber, String password) {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new User(1L, "user1", "1", 1000, "1"));
        users.add(new User(2L, "user2", "2", 1000, "2"));
        users.add(new User(3L, "user3", "3", 1000, "3"));
        users.add(new User(4L, "user4", "4", 1000, "4"));

        boolean isLogged = userService.loginUser(users, accountNumber, password);

        if (!isLogged) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/saldo/{accountNumber}")
    public ResponseEntity<Integer> getSaldo(@PathVariable String accountNumber) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1L, "user1", "1", 1000, "1"));
        users.add(new User(2L, "user2", "2", 1000, "2"));
        users.add(new User(3L, "user3", "3", 1000, "3"));
        users.add(new User(4L, "user4", "4", 1000, "4"));

        int userSaldo = userService.getUserSaldo(users, accountNumber);

        return new ResponseEntity<>(userSaldo, HttpStatus.OK);
    }

    @PostMapping("/create")
    public User create(String name, String password, int saldo, String accountNumber) {
        return userService.addUser(name, password, saldo, accountNumber);
    }
}

