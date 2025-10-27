package com.github.jukkarol.dto.transactionDto.response;

import java.math.BigDecimal;

public record MakeTransactionResponse(
        BigDecimal balanceAfterTransaction,
        BigDecimal  amount
) { }