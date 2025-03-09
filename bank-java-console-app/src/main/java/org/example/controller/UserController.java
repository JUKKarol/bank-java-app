package org.example.controller;

import org.example.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import static org.example.service.UserService.loginUser;
import static org.example.service.UserService.getUserSaldo;

@RestController
@RequestMapping("/api/{controllerName}")
public class UserController {
    @PostMapping("/login")
    public ResponseEntity login(String accountNumber, String password) {
        ArrayList<User> users = new ArrayList<User>();
        users.add(new User("user1", "1", 1000, "1"));
        users.add(new User("user2", "2", 1000, "2"));
        users.add(new User("user3", "3", 1000, "3"));
        users.add(new User("user4", "4", 1000, "4"));

        boolean isLogged = loginUser(users, accountNumber, password);

        if (!isLogged)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/saldo/{accountNumber}")
    public ResponseEntity<Integer> getSaldo(@PathVariable String accountNumber) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "1", 1000, "1"));
        users.add(new User("user2", "2", 1000, "2"));
        users.add(new User("user3", "3", 1000, "3"));
        users.add(new User("user4", "4", 1000, "4"));

        int userSaldo = getUserSaldo(users, accountNumber);

        return new ResponseEntity<>(userSaldo, HttpStatus.OK);
    }
}

