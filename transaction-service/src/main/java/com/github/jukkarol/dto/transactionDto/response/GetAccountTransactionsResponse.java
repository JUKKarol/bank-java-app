package com.github.jukkarol.dto.transactionDto.response;

import com.github.jukkarol.dto.transactionDto.TransactionDisplayDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountTransactionsResponse {
    private List<TransactionDisplayDto> transactions;
}
