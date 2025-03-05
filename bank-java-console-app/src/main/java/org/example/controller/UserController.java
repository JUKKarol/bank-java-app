package org.example.controller;

import org.example.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/hello")
    public String sayHello() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "1", 1000, "1"));
        users.add(new User("user2", "2", 1000, "2"));
        users.add(new User("user3", "3", 1000, "3"));
        users.add(new User("user4", "4", 1000, "4"));

        return "Hello from API!";
    }
}

