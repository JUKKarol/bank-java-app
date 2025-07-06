package com.github.jukkarol.dto.transactionDto;

import java.time.LocalDateTime;

public record TransactionDisplayDto (
    Integer amount,

    Integer balanceAfterTransaction,

    String fromAccountNumber,

    String toAccountNumber,

    LocalDateTime createdAt
){}
