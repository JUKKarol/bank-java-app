package com.github.jukkarol.controller;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.dto.creditDto.response.GetAccountCreditsResponse;
import com.github.jukkarol.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("credits")
@PreAuthorize("hasRole('Employee')")
public class CreditController {
    private final CreditService creditService;

    @PostMapping
    @Operation(
            summary = "Create credit",
            description = "Create credit for account number"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Credit created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCreditResponse.class)
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
    })
    public ResponseEntity<CreateCreditResponse> createCredit(@RequestBody @Valid CreateCreditRequest request) throws BadRequestException {
        CreateCreditResponse response = creditService.createCredit(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{accountNumber}")
    @Operation(
            summary = "Get credits",
            description = "Get credits for account number"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return credits",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCreditResponse.class)
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
    public ResponseEntity<GetAccountCreditsResponse> getCredits(@PathVariable @Valid @Size(min=10, max=10) @NotEmpty String accountNumber) throws BadRequestException {
        GetAccountCreditsResponse response = creditService.getAccountCredits(accountNumber);

        return ResponseEntity.ok(response);
    }
}
