package com.github.jukkarol.service;

import com.github.jukkarol.dto.transactionDto.request.MakeTransferRequest;
import com.github.jukkarol.dto.transactionDto.response.MakeTransferResponse;
import com.github.jukkarol.exception.InsufficientFundsException;
import com.github.jukkarol.exception.NotFoundException;
import com.github.jukkarol.model.Account;
import com.github.jukkarol.repository.AccountRepository;
import com.github.jukkarol.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public MakeTransferResponse makeTransfer(MakeTransferRequest request)
    {
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber());
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber());

        if (fromAccount == null) {
            throw new NotFoundException(Account.class.getSimpleName(), request.getFromAccountNumber());
        }

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

        return new MakeTransferResponse(fromAccount.getBalance(), request.getAmount());
    }
}
