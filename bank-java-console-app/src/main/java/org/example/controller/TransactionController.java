package org.example.controller;

import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{controllerName}")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/makeTransfer")
    public ResponseEntity makeTransfer(String fromAccountNumber, String toAccountNumber, int amount) {
        boolean transferResult = transactionService.makeTransferBetweenUsers(fromAccountNumber, toAccountNumber, amount);

        if(!transferResult)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

