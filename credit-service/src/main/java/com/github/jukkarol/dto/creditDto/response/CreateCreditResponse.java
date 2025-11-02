package com.github.jukkarol.dto.creditDto.response;

import java.math.BigDecimal;

public record CreateCreditResponse (
    BigDecimal amount,

    String accountNumber
){ }
