package com.github.jukkarol.dto.depositDto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequestedEvent {
    private Integer amount;

    private String accountNumber;
}
