package com.github.jukkarol.dto.transactionDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDisplayDto (
    BigDecimal amount,

    BigDecimal balanceAfterTransaction,

    String fromAccountNumber,

    String toAccountNumber,

    LocalDateTime createdAt
){}
