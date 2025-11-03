package com.github.jukkarol.dto.creditDto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateCreditRequest(
        @NotEmpty
        BigDecimal totalAmount,

        @NotEmpty
        BigDecimal monthlyAmount,

        @Size(min=10, max=10)
        @NotEmpty
        String accountNumber
) { }
