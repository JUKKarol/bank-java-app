package com.github.jukkarol.controller;

import com.github.jukkarol.configs.JwtAuthenticationToken;
import com.github.jukkarol.dto.transactionDto.request.MakeTransferRequest;
import com.github.jukkarol.dto.transactionDto.response.MakeTransferResponse;
import com.github.jukkarol.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/makeTransfer")
    public ResponseEntity<MakeTransferResponse> makeTransfer(@RequestBody MakeTransferRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUserId(userId);
        }

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