package com.github.jukkarol.service;

import com.github.jukkarol.dto.depositDto.event.DepositRequestEvent;
import com.github.jukkarol.dto.transactionDto.TransactionDisplayDto;
import com.github.jukkarol.dto.transactionDto.request.GetAccountTransactionsRequest;
import com.github.jukkarol.dto.transactionDto.request.MakeTransactionRequest;
import com.github.jukkarol.dto.transactionDto.response.GetAccountTransactionsResponse;
import com.github.jukkarol.dto.transactionDto.response.MakeTransactionResponse;
import com.github.jukkarol.dto.withdrawalDto.event.request.WithdrawalRequestEvent;
import com.github.jukkarol.dto.withdrawalDto.event.response.WithdrawalResponseEvent;
import com.github.jukkarol.exception.InsufficientFundsException;
import com.github.jukkarol.exception.NotFoundException;
import com.github.jukkarol.exception.PermissionDeniedException;
import com.github.jukkarol.mapper.TransactionMapper;
import com.github.jukkarol.model.Account;
import com.github.jukkarol.model.Transaction;
import com.github.jukkarol.repository.AccountRepository;
import com.github.jukkarol.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

        Page<Transaction> page = transactionRepository
                .findAllByFromAccountNumberOrToAccountNumber(
                        request.accountNumber(),
                        request.accountNumber(),
                        request.pageable());

        List<TransactionDisplayDto> transactionsDto = transactionMapper
                .transactionsToTransactionDisplayDtos(page.getContent(), request.accountNumber());

        return new GetAccountTransactionsResponse(
                transactionsDto,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    public void makeDeposit(DepositRequestEvent event)
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

    @KafkaListener(topics = "withdrawal-requests", groupId = "transaction-service-group")
    @SendTo("withdrawal-responses")
    @Transactional
    public WithdrawalResponseEvent processWithdrawal(WithdrawalRequestEvent event) {
        log.info("Processing withdrawal request: {}", event.transactionId());

        try {
            Account account = accountRepository.findByAccountNumber(event.accountNumber())
                    .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

            log.info("Account found: {}, current balance: {}", event.accountNumber(), account.getBalance());

            if (account.getBalance() < event.amount()) {
                log.warn("Insufficient funds for transaction: {}. Required: {}, Available: {}",
                        event.transactionId(), event.amount(), account.getBalance());

                return new WithdrawalResponseEvent(
                        event.transactionId(),
                        false,
                        "Insufficient funds. Available: " + account.getBalance() + ", Required: " + event.amount(),
                        account.getBalance(),
                        null
                );
            }

            int newBalance = account.getBalance() - event.amount();
            account.setBalance(newBalance);
            Account savedAccount = accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setTransactionId(event.transactionId());
            transaction.setAmount(-Math.abs(event.amount()));
            transaction.setToAccountBalanceAfterTransaction(savedAccount.getBalance());
            transaction.setFromAccountNumber("ATM");
            transaction.setToAccountNumber(event.accountNumber());
            transaction.setCreatedAt(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);

            log.info("Withdrawal completed successfully for transaction: {}. New balance: {}",
                    event.transactionId(), newBalance);

            return new WithdrawalResponseEvent(
                    event.transactionId(),
                    true,
                    "Withdrawal completed successfully",
                    newBalance,
                    savedTransaction.getId()
            );

        } catch (NotFoundException e) {
            log.error("Account not found for transaction: {}", event.transactionId());
            return new WithdrawalResponseEvent(
                    event.transactionId(),
                    false,
                    "Account not found: " + event.accountNumber(),
                    0,
                    null
            );

        } catch (Exception e) {
            log.error("Unexpected error processing withdrawal for transaction: {}", event.transactionId(), e);
            return new WithdrawalResponseEvent(
                    event.transactionId(),
                    false,
                    "System error: " + e.getMessage(),
                    0,
                    null
            );
        }
    }
}
