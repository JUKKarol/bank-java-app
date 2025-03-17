package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.userDto.CreateUserRequest;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<Integer> getBalance(@PathVariable String accountNumber) {
        Integer  userBalance = userService.getUserBalance(accountNumber);

        return new ResponseEntity<>(userBalance, HttpStatus.OK);
    }

    @PostMapping("/create")
    public User create(@RequestBody @Valid CreateUserRequest user) {
        return userService.addUser(user);
    }
}

