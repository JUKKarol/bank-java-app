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
    public ResponseEntity makeTransfer(String fromAccountNumber, String toAccountNumber, int amount) {
        boolean transferResult = transactionService.makeTransferBetweenUsers(fromAccountNumber, toAccountNumber, amount);

        if(!transferResult)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

