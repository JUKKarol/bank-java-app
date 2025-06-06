package com.github.jukkarol.controller;

import com.github.jukkarol.dto.transactionDto.request.MakeTransferRequest;
import com.github.jukkarol.dto.transactionDto.response.MakeTransferResponse;
import com.github.jukkarol.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/makeTransfer")
    public ResponseEntity<MakeTransferResponse> makeTransfer(@RequestBody MakeTransferRequest request) {
        //check is user acc owner by token

        MakeTransferResponse response = transactionService.makeTransfer(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/balance/{accountNumber}")
//    public ResponseEntity<Integer> getBalance(@PathVariable String accountNumber) {
//        Integer  userBalance = transactionService.getUserBalance(accountNumber);
//
//        return new ResponseEntity<>(userBalance, HttpStatus.OK);
//    }
}