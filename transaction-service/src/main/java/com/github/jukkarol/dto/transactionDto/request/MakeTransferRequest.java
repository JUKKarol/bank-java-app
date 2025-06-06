package com.github.jukkarol.dto.transactionDto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeTransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;

    private Integer amount;
}
