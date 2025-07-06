package com.github.jukkarol.dto.accountDto.response;

public record CreateAccountResponse(
        String accountNumber,

        Integer  balance
) { }