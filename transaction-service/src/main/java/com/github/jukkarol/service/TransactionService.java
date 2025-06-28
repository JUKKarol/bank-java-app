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

    public GetAccountTransactionsResponse getAccountTransactions(GetAccountTransactionsRequest request)
    {
        Account fromAccount = accountRepository.findByAccountNumber(request.getAccountNumber());

        if (fromAccount == null) {
            throw new NotFoundException(Account.class.getSimpleName(), request.getAccountNumber());
        }

        if (!request.getUserId().equals(fromAccount.getUserId()))
        {
            throw new PermissionDeniedException();
        }

        List<Transaction> transactions = transactionRepository
                .findAllByFromAccountNumberOrToAccountNumber(request.getAccountNumber(), request.getAccountNumber());

        List<TransactionDisplayDto> transactionsDto = transactionMapper
                .transactionsToTransactionDisplayDtos(transactions, request.getAccountNumber());

        return new GetAccountTransactionsResponse(transactionsDto);
    }

    public void makeDeposit(DepositRequestedEvent event)
    {
        Account account = accountRepository.findByAccountNumber(event.getAccountNumber());

        if (account == null) {
            throw new NotFoundException(Account.class.getSimpleName(), event.getAccountNumber());
        }

        account.setBalance(account.getBalance() + event.getAmount());

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(event.getAmount());
        transaction.setToAccountBalanceAfterTransaction(account.getBalance() + event.getAmount());
        transaction.setFromAccountNumber("ATM");
        transaction.setToAccountNumber(event.getAccountNumber());

        transactionRepository.save(transaction);
    }

    public void makeWithdrawal(WithdrawalRequestedEvent event)
    {
        Account account = accountRepository.findByAccountNumber(event.getAccountNumber());

        if (account == null) {
            throw new NotFoundException(Account.class.getSimpleName(), event.getAccountNumber());
        }

        account.setBalance(account.getBalance() - event.getAmount());

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(-Math.abs(event.getAmount()));
        transaction.setToAccountBalanceAfterTransaction(account.getBalance());
        transaction.setFromAccountNumber("ATM");
        transaction.setToAccountNumber(event.getAccountNumber());

        transactionRepository.save(transaction);
    }
}
