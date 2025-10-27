package com.github.jukkarol.dto.accountDto.response;

import java.math.BigDecimal;

public record CreateAccountResponse(
        String accountNumber,

        BigDecimal balance
) { }