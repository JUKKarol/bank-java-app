package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction makeTransactionRequestToTransfer(MakeTransactionRequest request);
}
