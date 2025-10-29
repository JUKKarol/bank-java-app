package com.github.jukkarol.dto.depositDto.response;

import java.math.BigDecimal;

public record MakeDepositResponse(
        BigDecimal amount,

        String accountNumber
) { }
