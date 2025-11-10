package com.github.jukkarol.dto.creditDto.event.response;

import java.math.BigDecimal;

public record CreditResponseEvent(
        boolean success,

        String message
) { }
