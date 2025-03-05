package org.example;

import org.example.model.User;

import java.util.ArrayList;

import static org.example.service.MenuService.invokeMenu;
import static org.example.service.MenuService.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}