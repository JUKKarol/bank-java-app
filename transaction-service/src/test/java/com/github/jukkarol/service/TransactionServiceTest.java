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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Account fromAccount;
    private Account toAccount;
    private MakeTransactionRequest request;

    @BeforeEach
    void setUp() {
        fromAccount = new Account();
        fromAccount.setAccountNumber("1234567890");
        fromAccount.setUserId(1L);
        fromAccount.setBalance(1000);

        toAccount = new Account();
        toAccount.setAccountNumber("0987654321");
        toAccount.setUserId(2L);
        toAccount.setBalance(500);

        request = new MakeTransactionRequest();
        request.setFromAccountNumber("1234567890");
        request.setToAccountNumber("0987654321");
        request.setUserId(1L);
        request.setAmount(300);
    }

    @Test
    void shouldMakeTransferSuccessfully() {
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(toAccount));
        when(transactionMapper.makeTransactionRequestToTransfer(request)).thenReturn(new Transaction());

        MakeTransactionResponse response = transactionService.makeTransfer(request);

        assertEquals(700, fromAccount.getBalance());
        assertEquals(800, toAccount.getBalance());
        assertEquals(300, response.getAmount());
        assertEquals(700, response.getBalanceAfterTransaction());

        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
        verify(transactionRepository).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionIfFromAccountNotFound() {
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.makeTransfer(request));
    }

    @Test
    void shouldThrowPermissionDeniedIfUserIdDoesNotMatch() {
        fromAccount.setUserId(999L);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(fromAccount));

        assertThrows(PermissionDeniedException.class, () -> transactionService.makeTransfer(request));
    }

    @Test
    void shouldThrowNotFoundExceptionIfToAccountNotFound() {
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.makeTransfer(request));
    }

    @Test
    void shouldThrowInsufficientFundsIfNotEnoughBalance() {
        fromAccount.setBalance(100);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class, () -> transactionService.makeTransfer(request));
    }
}
