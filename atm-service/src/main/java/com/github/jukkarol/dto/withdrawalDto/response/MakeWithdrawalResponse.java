package com.github.jukkarol.dto.withdrawalDto.response;

import java.math.BigDecimal;

public record MakeWithdrawalResponse(
        BigDecimal amount,

        String accountNumber
) { }