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
        boolean isLogged = userService.loginUser(accountNumber, password);

        if (!isLogged) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/saldo/{accountNumber}")
    public ResponseEntity<Integer> getSaldo(@PathVariable String accountNumber) {
        int userSaldo = userService.getUserSaldo(accountNumber);

        return new ResponseEntity<>(userSaldo, HttpStatus.OK);
    }

    @PostMapping("/create")
    public User create(String name, String password, int saldo, String accountNumber) {
        return userService.addUser(name, password, saldo, accountNumber);
    }
}

