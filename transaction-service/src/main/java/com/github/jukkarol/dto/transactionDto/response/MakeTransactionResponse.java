package com.github.jukkarol.dto.transactionDto.response;

public record MakeTransactionResponse(
         Integer balanceAfterTransaction,
         Integer amount
) { }