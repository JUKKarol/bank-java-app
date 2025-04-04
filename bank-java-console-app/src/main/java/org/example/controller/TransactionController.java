package org.example.controller;

import org.example.dto.userDto.MakeTransferResponse;
import org.example.repository.UserRepository;
import org.example.service.TransactionService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{controllerName}")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/makeTransfer")
    public ResponseEntity<MakeTransferResponse> makeTransfer(String fromAccountNumber, String toAccountNumber, int amount) {
        boolean transferResult = transactionService.makeTransferBetweenUsers(fromAccountNumber, toAccountNumber, amount);

        if (!transferResult) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        MakeTransferResponse response = new MakeTransferResponse(userService.getUserBalance(fromAccountNumber));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<Integer> getBalance(@PathVariable String accountNumber) {
        Integer  userBalance = userService.getUserBalance(accountNumber);

        return new ResponseEntity<>(userBalance, HttpStatus.OK);
    }
}

