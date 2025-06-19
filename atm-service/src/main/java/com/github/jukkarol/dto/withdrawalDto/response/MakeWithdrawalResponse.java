package com.github.jukkarol.dto.withdrawalDto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeWithdrawalResponse {
    private Integer amount;

    private String accountNumber;
}
