package com.github.jukkarol.dto.withdrawalDto.event.request;

public record WithdrawalRequestEvent(
        String transactionId,
        String accountNumber,
        Integer amount
) {}
