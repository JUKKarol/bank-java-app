package com.github.jukkarol.controller;

import com.github.jukkarol.configs.JwtAuthenticationToken;
import com.github.jukkarol.dto.transactionDto.request.GetAccountTransactionsRequest;
import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.dto.transactionDto.response.GetAccountTransactionsResponse;
import com.github.jukkarol.dto.transactionDto.response.MakeTransactionResponse;
import com.github.jukkarol.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @Operation(
            summary = "Make transfer",
            description = "Make transfer from account to account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transfer done successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MakeTransactionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Not enough founds",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
    })
    public ResponseEntity<MakeTransactionResponse> makeTransfer(@RequestBody @Valid MakeTransactionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUserId(userId);
        }

        MakeTransactionResponse response = transactionService.makeTransfer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //ToDo: Add pagination
    @GetMapping("{accountNumber}")
    @Operation(
            summary = "Get account transactions",
            description = "Get account transactions for specified account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return all account transactions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MakeTransactionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    })
    public ResponseEntity<GetAccountTransactionsResponse> getAccountTransactions(@PathVariable @Valid @Size(min=10, max=10) @NotEmpty String accountNumber) {
        GetAccountTransactionsRequest request = new GetAccountTransactionsRequest();
        request.setAccountNumber(accountNumber);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUserId(userId);
        }

        GetAccountTransactionsResponse response = transactionService.getAccountTransactions(request);

        return ResponseEntity.ok(response);
    }
}