package com.github.jukkarol.dto.creditDto.event.request;

import java.math.BigDecimal;

public record CreditRequestEvent(
        BigDecimal amount,

        String accountNumber
) { }
