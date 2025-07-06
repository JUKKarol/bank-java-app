package com.github.jukkarol.service;

import com.github.jukkarol.dto.depositDto.event.DepositRequestedEvent;
import com.github.jukkarol.dto.transactionDto.TransactionDisplayDto;
import com.github.jukkarol.dto.transactionDto.request.GetAccountTransactionsRequest;
import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.dto.transactionDto.response.GetAccountTransactionsResponse;
import com.github.jukkarol.dto.transactionDto.response.MakeTransactionResponse;
import com.github.jukkarol.dto.withdrawalDto.event.WithdrawalRequestedEvent;
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

import java.util.List;

@AllArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    public MakeTransactionResponse makeTransfer(MakeTransactionRequest request)
    {
        Account fromAccount = accountRepository.findByAccountNumber(request.fromAccountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), request.fromAccountNumber()));

        if (!request.userId().equals(fromAccount.getUserId()))
        {
            throw new PermissionDeniedException();
        }

        Account toAccount = accountRepository.findByAccountNumber(request.toAccountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), request.toAccountNumber()));

        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Not enough funds.");
        }

        fromAccount.setBalance(fromAccount.getBalance() - request.amount());
        toAccount.setBalance(toAccount.getBalance() + request.amount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = transactionMapper.makeTransactionRequestToTransfer(request);
        transaction.setFromAccountBalanceAfterTransaction(fromAccount.getBalance());
        transaction.setToAccountBalanceAfterTransaction(toAccount.getBalance());
        transactionRepository.save(transaction);

        return new MakeTransactionResponse(fromAccount.getBalance(), request.amount());
    }

    public GetAccountTransactionsResponse getAccountTransactions(GetAccountTransactionsRequest request)
    {
        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), request.accountNumber()));

        if (!request.userId().equals(account.getUserId()))
        {
            throw new PermissionDeniedException();
        }

        List<Transaction> transactions = transactionRepository
                .findAllByFromAccountNumberOrToAccountNumber(request.accountNumber(), request.accountNumber());

        List<TransactionDisplayDto> transactionsDto = transactionMapper
                .transactionsToTransactionDisplayDtos(transactions, request.accountNumber());

        return new GetAccountTransactionsResponse(transactionsDto);
    }

    public void makeDeposit(DepositRequestedEvent event)
    {
        Account account = accountRepository.findByAccountNumber(event.accountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

        account.setBalance(account.getBalance() + event.amount());

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(event.amount());
        transaction.setToAccountBalanceAfterTransaction(account.getBalance() + event.amount());
        transaction.setFromAccountNumber("ATM");
        transaction.setToAccountNumber(event.accountNumber());

        transactionRepository.save(transaction);
    }

    public void makeWithdrawal(WithdrawalRequestedEvent event)
    {
        Account account = accountRepository.findByAccountNumber(event.accountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

        account.setBalance(account.getBalance() - event.amount());

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(-Math.abs(event.amount()));
        transaction.setToAccountBalanceAfterTransaction(account.getBalance());
        transaction.setFromAccountNumber("ATM");
        transaction.setToAccountNumber(event.accountNumber());

        transactionRepository.save(transaction);
    }
}
