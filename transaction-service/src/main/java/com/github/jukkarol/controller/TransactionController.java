package com.github.jukkarol.controller;

import com.github.jukkarol.configs.JwtAuthenticationToken;
import com.github.jukkarol.dto.transactionDto.request.GetAccountTransactionsRequest;
import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.dto.transactionDto.response.GetAccountTransactionsResponse;
import com.github.jukkarol.dto.transactionDto.response.MakeTransactionResponse;
import com.github.jukkarol.exception.PermissionDeniedException;
import com.github.jukkarol.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

            MakeTransactionRequest processedRequest = new MakeTransactionRequest(
                    userId, request.fromAccountNumber(), request.toAccountNumber(), request.amount());

            MakeTransactionResponse response = transactionService.makeTransfer(processedRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        throw new PermissionDeniedException();
    }

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
    public ResponseEntity<GetAccountTransactionsResponse> getAccountTransactions(
            @Parameter(description = "Account number (10 digits)", example = "1234567890")
            @PathVariable @Valid @Size(min=10, max=10) @NotEmpty String accountNumber,

            @Parameter(description = "Page number (0 = first page)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Field to sort by (e.g. createdAt, amount)", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sorting direction: asc or desc", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        int pageSize = 20;

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();

            GetAccountTransactionsRequest request = new GetAccountTransactionsRequest(userId, accountNumber, pageable);
            GetAccountTransactionsResponse response = transactionService.getAccountTransactions(request);

            return ResponseEntity.ok(response);
        }

        throw new PermissionDeniedException();
    }
}