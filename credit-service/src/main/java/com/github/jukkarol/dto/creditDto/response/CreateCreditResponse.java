package com.github.jukkarol.dto.creditDto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCreditResponse (
        BigDecimal amountTotal,

        BigDecimal amountMonthly,

        int installmentTotal,

        int installmentLeft,

        String accountNumber,

        LocalDateTime createdAt
){ }
