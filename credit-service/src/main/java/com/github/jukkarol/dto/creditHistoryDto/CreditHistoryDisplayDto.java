package com.github.jukkarol.dto.creditHistoryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditHistoryDisplayDto(
        Long id,

        BigDecimal amountLeft,

        BigDecimal amount,

        int installmentLeft,

        LocalDateTime createdAt
){ }
