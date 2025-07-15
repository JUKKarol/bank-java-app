package com.github.jukkarol.dto.depositDto.event;

public record DepositRequestEvent(
        Integer amount,

        String accountNumber
) { }
