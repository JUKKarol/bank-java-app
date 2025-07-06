package com.github.jukkarol.dto.withdrawalDto.event;

public record WithdrawalRequestedEvent(
        Integer amount,

        String  accountNumber
) { }