package com.github.jukkarol.dto.creditDto.event;

import java.math.BigDecimal;

public record SingleCreditRequest(
        BigDecimal amount,

        String accountNumber
) { }
