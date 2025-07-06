package com.github.jukkarol.dto.transactionDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record GetAccountTransactionsRequest(
        @JsonIgnore
        Long userId,

        @Size(min = 10, max = 10)
        @NotEmpty
        String accountNumber
) { }