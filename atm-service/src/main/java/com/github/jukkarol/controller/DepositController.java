package com.github.jukkarol.controller;

import com.github.jukkarol.dto.depositDto.request.MakeDepositRequest;
import com.github.jukkarol.dto.depositDto.response.MakeDepositResponse;
import com.github.jukkarol.service.DepositService;
import com.github.jukkarol.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("deposits")
@PreAuthorize("hasRole('ATM')")
public class DepositController {
    private final DepositService depositService;
    private final WithdrawalService withdrawalService;

    @PostMapping
    @Operation(
            summary = "Make deposit",
            description = "Make deposit for a specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit finished successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MakeDepositResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
    })
    public ResponseEntity<MakeDepositResponse> makeDeposit(@RequestBody @Valid MakeDepositRequest request) {
        MakeDepositResponse response = depositService.makeDeposit(request);

        return ResponseEntity.ok(response);
    }
}
