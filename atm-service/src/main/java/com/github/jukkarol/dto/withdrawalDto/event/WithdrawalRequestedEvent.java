package com.github.jukkarol.dto.withdrawalDto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequestedEvent {
    private Integer amount;

    private String accountNumber;
}
