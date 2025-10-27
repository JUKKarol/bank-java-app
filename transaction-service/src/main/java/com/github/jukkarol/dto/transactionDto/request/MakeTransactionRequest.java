package com.github.jukkarol.dto.transactionDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MakeTransactionRequest(
        @JsonIgnore
        Long userId,

        @Size(min = 10, max = 10)
        @NotEmpty
        String fromAccountNumber,
        @NotEmpty
        @Size(min = 10, max = 10)
        String toAccountNumber,

        @NotNull
        @Positive
        BigDecimal amount
) { }
