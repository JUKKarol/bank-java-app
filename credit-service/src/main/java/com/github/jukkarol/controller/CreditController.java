package com.github.jukkarol.controller;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.service.CreditService;
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
@RequestMapping("credit")
@PreAuthorize("hasRole('Employee')")
public class CreditController {
    private final CreditService creditService;

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
    })
    public ResponseEntity<CreateCreditResponse> makeDeposit(@RequestBody @Valid CreateCreditRequest request) {
        CreateCreditResponse response = creditService.createCredit(request);

        return ResponseEntity.ok(response);
    }
}
