package com.github.jukkarol.dto.withdrawalDto.event.request;

import java.math.BigDecimal;

public record WithdrawalRequestEvent(
        String transactionId,
        String accountNumber,
        BigDecimal amount
) {}
