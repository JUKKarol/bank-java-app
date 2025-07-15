package com.github.jukkarol.dto.withdrawalDto.event.response;

public record WithdrawalResponseEvent(
        String transactionId,
        boolean success,
        String message,
        Integer remainingBalance,
        Long transactionDbId
) {}