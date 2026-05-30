package com.github.jukkarol.controller;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.dto.creditDto.response.GetAccountCreditsResponse;
import com.github.jukkarol.dto.creditHistoryDto.response.GetCreditHistoriesResponse;
import com.github.jukkarol.service.CreditHistoryService;
import com.github.jukkarol.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("credits/history")
//@PreAuthorize("hasRole('Employee')")
public class CreditHistoryController {
    private final CreditService creditService;

    private final CreditHistoryService creditHistoryService;

    @GetMapping("{creditId}")
    @Operation(
            summary = "Get credit history",
            description = "Get credits history for credit"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return credit history",
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
                    description = "Credit not found",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
    })
    public ResponseEntity<GetCreditHistoriesResponse> getCreditHistory(@PathVariable @Valid @Positive Long creditId) throws BadRequestException {
        GetCreditHistoriesResponse response = creditHistoryService.getCreditsHistoriesByCreditId(creditId);

        return ResponseEntity.ok(response);
    }
}
