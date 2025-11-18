package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.event.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.event.SingleCreditRequest;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public MakeTransactionResponse makeTransfer(MakeTransactionRequest request) {
        Account fromAccount = accountRepository.findByAccountNumber(request.fromAccountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), request.fromAccountNumber()));

        if (!request.userId().equals(fromAccount.getUserId())) {
            throw new PermissionDeniedException();
        }

        Account toAccount = accountRepository.findByAccountNumber(request.toAccountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), request.toAccountNumber()));

        if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Not enough funds.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
        toAccount.setBalance(toAccount.getBalance().add(request.amount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = transactionMapper.makeTransactionRequestToTransfer(request);
        transaction.setFromAccountBalanceAfterTransaction(fromAccount.getBalance());
        transaction.setToAccountBalanceAfterTransaction(toAccount.getBalance());
        transactionRepository.save(transaction);

        return new MakeTransactionResponse(fromAccount.getBalance(), request.amount());
    }

    public GetAccountTransactionsResponse getAccountTransactions(GetAccountTransactionsRequest request) {
        Account account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), request.accountNumber()));

        if (!request.userId().equals(account.getUserId())) {
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

    public void makeDeposit(DepositRequestEvent event) {
        Account account = accountRepository.findByAccountNumber(event.accountNumber())
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

        account.setBalance(account.getBalance().add(event.amount()));

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(event.amount());
        transaction.setToAccountBalanceAfterTransaction(account.getBalance().add(event.amount()));
        transaction.setFromAccountNumber("ATM");
        transaction.setToAccountNumber(event.accountNumber());

        transactionRepository.save(transaction);
    }

    public void processCreditsInstallments(CreditRequestEvent event) {
        List<String> accountsNumbers = event.creditRequests().stream().map(SingleCreditRequest::accountNumber).toList();

        List<Account> accounts = accountRepository.findAllByAccountNumberIn(accountsNumbers)
                .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), "processCreditsInstallments"));

        for (Account account : accounts) {
            SingleCreditRequest request = event.creditRequests()
                    .stream()
                    .filter(a -> a.accountNumber().equals(account.getAccountNumber()))
                    .findAny()
                    .orElseThrow(() -> new NotFoundException(
                            SingleCreditRequest.class.getSimpleName(), account.getAccountNumber()));

            account.setBalance(account.getBalance().subtract(request.amount()));

            accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAmount(request.amount());
            transaction.setToAccountBalanceAfterTransaction(account.getBalance().add(request.amount()));
            transaction.setFromAccountNumber("CREDIT");
            transaction.setToAccountNumber(request.accountNumber());

            transactionRepository.save(transaction);
        }
    }

    @KafkaListener(topics = "withdrawal-requests", groupId = "transaction-service-group")
    @SendTo("withdrawal-responses")
    @Transactional
    public WithdrawalResponseEvent processWithdrawal(WithdrawalRequestEvent event) {
        log.info("Processing withdrawal request: {}", event.transactionId());

        try {
            Optional<Transaction> existingTransaction = transactionRepository.findByTransactionId(event.transactionId());
            if (existingTransaction.isPresent()) {
                log.info("Transaction already processed: {}", event.transactionId());

                Account account = accountRepository.findByAccountNumber(event.accountNumber())
                        .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

                return new WithdrawalResponseEvent(
                        event.transactionId(),
                        true,
                        "Transaction already processed",
                        account.getBalance(),
                        existingTransaction.get().getId()
                );
            }

            Account account = accountRepository.findByAccountNumber(event.accountNumber())
                    .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

            log.info("Account found: {}, current balance: {}", event.accountNumber(), account.getBalance());

            if (account.getBalance().compareTo(event.amount()) < 0) {
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

            BigDecimal newBalance = account.getBalance().subtract(event.amount());
            account.setBalance(newBalance);
            Account savedAccount = accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setTransactionId(event.transactionId());
            transaction.setAmount(event.amount().abs().negate());
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

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk6plyfbm3wy6ds7hongoml5xbk")) {
                log.warn("Duplicate transaction detected: {}", event.transactionId());

                try {
                    Optional<Transaction> existingTransaction = transactionRepository.findByTransactionId(event.transactionId());
                    Account account = accountRepository.findByAccountNumber(event.accountNumber())
                            .orElseThrow(() -> new NotFoundException(Account.class.getSimpleName(), event.accountNumber()));

                    return new WithdrawalResponseEvent(
                            event.transactionId(),
                            true,
                            "Transaction already processed",
                            account.getBalance(),
                            existingTransaction.map(Transaction::getId).orElse(null)
                    );
                } catch (Exception innerException) {
                    log.error("Error handling duplicate transaction: {}", event.transactionId(), innerException);
                    return new WithdrawalResponseEvent(
                            event.transactionId(),
                            false,
                            "Duplicate transaction handling error: " + innerException.getMessage(),
                            BigDecimal.valueOf(0),
                            null
                    );
                }
            } else {
                throw e;
            }
        } catch (NotFoundException e) {
            log.error("Account not found for transaction: {}", event.transactionId());
            return new WithdrawalResponseEvent(
                    event.transactionId(),
                    false,
                    "Account not found: " + event.accountNumber(),
                    BigDecimal.valueOf(0),
                    null
            );
        } catch (Exception e) {
            log.error("Unexpected error processing withdrawal for transaction: {}", event.transactionId(), e);
            return new WithdrawalResponseEvent(
                    event.transactionId(),
                    false,
                    "System error: " + e.getMessage(),
                    BigDecimal.valueOf(0),
                    null
            );
        }
    }
}