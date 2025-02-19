package org.example.service;

import org.example.model.User;

import java.util.ArrayList;
import java.util.Scanner;

public class MenuService {
    public static void invokeMenu(ArrayList<User> users) {
        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to the banking system!");
            System.out.println("1. Check account balance");
            System.out.println("2. Make a transfer");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Checking account balance...");
                    break;
                case 2:
                    System.out.println("Making a transfer...");
                    break;
                case 3:
                    System.out.println("Thank you for using our system!");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    invokeMenu(users);
            }

            scanner.close();
        }
    }

    public static boolean login(ArrayList<User> users)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Login: ");
        String accountNumber = scanner.nextLine();

        var currentUser = users.stream()
                .filter(user -> user.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        if(currentUser == null)
        {
            return false;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        return currentUser.getPassword().equals(password);
    }
}
