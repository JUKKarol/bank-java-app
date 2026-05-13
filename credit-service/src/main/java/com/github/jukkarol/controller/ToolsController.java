package com.github.jukkarol.controller;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.request.ProcessSpecifiedCreditsInstallmentsRequest;
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
@RequestMapping("tools")
@PreAuthorize("hasRole('Admin')")
public class ToolsController {
    private final CreditService creditService;

    @PostMapping
    @Operation(
            summary = "Process installments",
            description = "Process specified installments by credit id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Credits installments processed",
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
    public ResponseEntity<Void> processSpecifiedCreditsInstallments(@RequestBody @Valid ProcessSpecifiedCreditsInstallmentsRequest request) {
        creditService.processSpecifiedCreditsInstallments(request);

        return ResponseEntity.ok().build();
    }
}
