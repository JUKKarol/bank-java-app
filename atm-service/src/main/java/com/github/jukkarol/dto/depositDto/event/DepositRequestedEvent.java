package com.github.jukkarol.dto.depositDto.event;

public record DepositRequestedEvent(
        Integer amount,

        String accountNumber
) { }
