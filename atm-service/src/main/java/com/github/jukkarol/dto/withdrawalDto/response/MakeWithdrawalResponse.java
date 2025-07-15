package com.github.jukkarol.dto.withdrawalDto.response;

public record MakeWithdrawalResponse(
        Integer amount,

        String accountNumber
) { }