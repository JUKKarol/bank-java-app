package org.example;

import org.example.model.User;

import java.util.ArrayList;

import static org.example.service.MenuService.login;

public class Main {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "password1", 1000, "1"));
        users.add(new User("user2", "password2", 1000, "2"));

        boolean isLoginCorrect = login(users);
        System.out.println(isLoginCorrect);
    }
}