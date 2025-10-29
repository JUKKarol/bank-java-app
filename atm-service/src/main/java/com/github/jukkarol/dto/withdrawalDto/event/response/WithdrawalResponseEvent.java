package com.github.jukkarol.dto.withdrawalDto.event.response;

import java.math.BigDecimal;

public record WithdrawalResponseEvent(
        String transactionId,
        boolean success,
        String message,
        BigDecimal remainingBalance,
        Long transactionDbId
) {}