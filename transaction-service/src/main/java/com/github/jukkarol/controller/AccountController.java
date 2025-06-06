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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor
@RestController
@RequestMapping("/api/account")
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
            request.setUser_id(userId);
        }

        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.CREATED);
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
            request.setUser_id(userId);
        }

        return new ResponseEntity<>(accountService.getAccountsByUserId(request), HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}")
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
    })
    public ResponseEntity<AccountDetailsDisplayDto> getAccountDetails(@PathVariable String accountNumber) {
        GetAccountDetailsRequest request = new GetAccountDetailsRequest();
        request.setAccountNumber(accountNumber);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();
            request.setUser_id(userId);
        }

        return new ResponseEntity<>(accountService.getAccountByAccountNumber(request), HttpStatus.OK);
    }
}
