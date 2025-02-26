package org.example;

import org.example.model.User;

import java.util.ArrayList;

import static org.example.service.MenuService.invokeMenu;
import static org.example.service.MenuService.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "1", 1000, "1"));
        users.add(new User("user2", "2", 1000, "2"));

        while (true) {
            String loggedUserAccountNumber = login(users);
            System.out.println(loggedUserAccountNumber);

            if (loggedUserAccountNumber != null) {
                boolean exit = invokeMenu(users, loggedUserAccountNumber);
                if (exit) {
                    break;
                }
            }
        }
    }
}