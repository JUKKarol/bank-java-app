package com.github.jukkarol.dto.creditDto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCreditResponse (
        BigDecimal totalAmount,

        BigDecimal monthlyAmount,

        String accountNumber,

        LocalDateTime createdAt
){ }
