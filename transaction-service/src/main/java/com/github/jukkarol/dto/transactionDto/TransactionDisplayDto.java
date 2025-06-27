package com.github.jukkarol.dto.transactionDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDisplayDto {
    private Integer amount;

    private Integer BalanceAfterTransaction;

    private String fromAccountNumber;

    private String toAccountNumber;

    private LocalDateTime createdAt;
}
