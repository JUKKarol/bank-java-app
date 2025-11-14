package com.github.jukkarol.dto.creditDto.event.request;

import java.math.BigDecimal;

public record SingleCreditRequest(
        BigDecimal amount,

        String accountNumber
) { }