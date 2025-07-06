package com.github.jukkarol.dto.transactionDto.response;

import com.github.jukkarol.dto.transactionDto.TransactionDisplayDto;

import java.util.List;

public record GetAccountTransactionsResponse(
        List<TransactionDisplayDto> transactions
) { }
