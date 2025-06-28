package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.transactionDto.TransactionDisplayDto;
import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction makeTransactionRequestToTransfer(MakeTransactionRequest request);
    TransactionDisplayDto transactionToTransactionDisplayDto(Transaction transaction);

    default TransactionDisplayDto transactionToTransactionDisplayDto(Transaction transaction, String accountNumber) {
        TransactionDisplayDto dto = new TransactionDisplayDto();
        dto.setAmount(transaction.getAmount());
        dto.setFromAccountNumber(transaction.getFromAccountNumber());
        dto.setToAccountNumber(transaction.getToAccountNumber());
        dto.setCreatedAt(transaction.getCreatedAt());

        if (accountNumber.equals(transaction.getFromAccountNumber())) {
            dto.setBalanceAfterTransaction(transaction.getFromAccountBalanceAfterTransaction());
        } else {
            dto.setBalanceAfterTransaction(transaction.getToAccountBalanceAfterTransaction());
        }

        return dto;
    }

    default List<TransactionDisplayDto> transactionsToTransactionDisplayDtos(List<Transaction> transactions, String accountNumber) {
        return transactions.stream()
                .map(transaction -> transactionToTransactionDisplayDto(transaction, accountNumber))
                .toList();
    }
}
