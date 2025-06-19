package com.github.jukkarol.dto.withdrawalDto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeWithdrawalRequest {
    private Integer amount;

    private String accountNumber;
}
