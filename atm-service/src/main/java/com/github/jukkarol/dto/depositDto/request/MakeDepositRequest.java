package com.github.jukkarol.dto.depositDto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MakeDepositRequest(
        @NotNull
        @Positive
        BigDecimal amount,

        @Size(min=10, max=10)
        @NotEmpty
        String accountNumber
) { }

