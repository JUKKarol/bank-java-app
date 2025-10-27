package com.github.jukkarol.dto.accountDto;

import java.math.BigDecimal;

public record AccountDisplayDto(
        String accountNumber,

        BigDecimal balance
) { }