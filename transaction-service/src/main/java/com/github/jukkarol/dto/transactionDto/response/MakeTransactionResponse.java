package com.github.jukkarol.dto.transactionDto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeTransactionResponse {
    private Integer balanceAfterTransaction;
    private Integer amount;
}
