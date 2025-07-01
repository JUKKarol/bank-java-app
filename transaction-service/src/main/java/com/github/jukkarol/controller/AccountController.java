package com.github.jukkarol.controller;

import com.github.jukkarol.configs.JwtAuthenticationToken;
import com.github.jukkarol.dto.accountDto.AccountDetailsDisplayDto;
import com.github.jukkarol.dto.accountDto.request.CreateAccountRequest;
import com.github.jukkarol.dto.accountDto.request.GetAccountDetailsRequest;
import com.github.jukkarol.dto.accountDto.request.GetMyAccountsRequest;
import com.github.jukkarol.dto.accountDto.response.CreateAccountResponse;
import com.github.jukkarol.dto.accountDto.response.GetMyAccountsResponse;
import com.github.jukkarol.service.AccountService;
import com.github.jukkarol.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor
@RestController
@RequestMapping("account")
@Tag(name = "Authentication", description = "Authentication endpoints for basic user operations")
public class AccountController {
    private final AccountService accountService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(
            summary = "Create account",
            description = "Creates a new bank account for logged user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateAccountResponse.class)
                    )
            ),
    })
    public ResponseEntity<CreateAccountResponse> createAccount() {
        CreateAccountRequest request = new CreateAccountRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUserId(userId);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping
    @Operation(
            summary = "Get all accounts",
            description = "Get all accounts for logged user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns all logged user accounts",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetMyAccountsResponse.class)
                    )
            ),
    })
    public ResponseEntity<GetMyAccountsResponse> getMyAccounts() {
        GetMyAccountsRequest request = new GetMyAccountsRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUserId(userId);
        }

        return ResponseEntity.ok(accountService.getAccountsByUserId(request));
    }

    @GetMapping("{accountNumber}")
    @Operation(
            summary = "Get account",
            description = "Get account with details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns user account with details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountDetailsDisplayDto.class)
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
            ),
    })
    public ResponseEntity<AccountDetailsDisplayDto> getAccountDetails(@PathVariable @Valid @Size(min=10, max=10) @NotEmpty String accountNumber) {
        GetAccountDetailsRequest request = new GetAccountDetailsRequest();
        request.setAccountNumber(accountNumber);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUserId(userId);
        }

        return ResponseEntity.ok(accountService.getAccountByAccountNumber(request));
    }
}
