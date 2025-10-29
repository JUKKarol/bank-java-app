package com.github.jukkarol.dto.depositDto.event;

import java.math.BigDecimal;

public record DepositRequestEvent(
        BigDecimal amount,

        String accountNumber
) { }
