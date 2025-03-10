package org.example.controller;

import org.example.dto.TransferResult;
import org.example.model.User;
import org.example.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/{controllerName}")
public class TransactionController {
    private TransactionService transactionService;

    @PostMapping("/maketransfer")
    public ResponseEntity<ArrayList<User>> makeTransfer(String fromAccountNumber, String toAccountNumber, int amount) {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1L, "user1", "1", 1000, "1"));
        users.add(new User(2L, "user2", "2", 1000, "2"));
        users.add(new User(3L, "user3", "3", 1000, "3"));
        users.add(new User(4L, "user4", "4", 1000, "4"));

        TransferResult transferResult = transactionService.makeTransferBetweenUsers(users, fromAccountNumber, toAccountNumber, amount);

        if(!transferResult.isSuccess())
        {
            return new ResponseEntity<>(transferResult.getUsers(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(transferResult.getUsers(), HttpStatus.OK);
    }
}

