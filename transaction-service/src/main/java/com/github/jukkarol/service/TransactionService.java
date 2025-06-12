package com.github.jukkarol.service;

import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.dto.transactionDto.response.MakeTransactionResponse;
import com.github.jukkarol.exception.InsufficientFundsException;
import com.github.jukkarol.exception.NotFoundException;
import com.github.jukkarol.exception.PermissionDeniedException;
import com.github.jukkarol.mapper.TransactionMapper;
import com.github.jukkarol.model.Account;
import com.github.jukkarol.model.Transaction;
import com.github.jukkarol.repository.AccountRepository;
import com.github.jukkarol.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public MakeTransactionResponse makeTransfer(MakeTransactionRequest request)
    {
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber());

        if (fromAccount == null) {
            throw new NotFoundException(Account.class.getSimpleName(), request.getFromAccountNumber());
        }

        if (!request.getUserId().equals(fromAccount.getUserId()))
        {
            throw new PermissionDeniedException();
        }

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber());

        if (toAccount == null) {
            throw new NotFoundException(Account.class.getSimpleName(), request.getToAccountNumber());
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Not enough funds.");
        }

        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = transactionMapper.makeTransactionRequestToTransfer(request);
        transaction.setFromAccountBalanceAfterTransaction(fromAccount.getBalance());
        transaction.setToAccountBalanceAfterTransaction(toAccount.getBalance());
        transactionRepository.save(transaction);

        return new MakeTransactionResponse(fromAccount.getBalance(), request.getAmount());
    }
}
