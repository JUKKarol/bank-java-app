package com.github.jukkarol.dto.creditDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditDisplayDto (
        Long id,

        BigDecimal amountTotal,

        BigDecimal amountMonthly,

        int installmentTotal,

        int installmentLeft,

        String accountNumber,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
){ }
