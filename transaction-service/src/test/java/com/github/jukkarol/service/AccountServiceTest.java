package com.github.jukkarol.service;

import com.github.jukkarol.dto.accountDto.AccountDetailsDisplayDto;
import com.github.jukkarol.dto.accountDto.AccountDisplayDto;
import com.github.jukkarol.dto.accountDto.request.CreateAccountRequest;
import com.github.jukkarol.dto.accountDto.request.GetAccountDetailsRequest;
import com.github.jukkarol.dto.accountDto.request.GetMyAccountsRequest;
import com.github.jukkarol.dto.accountDto.response.CreateAccountResponse;
import com.github.jukkarol.dto.accountDto.response.GetMyAccountsResponse;
import com.github.jukkarol.exception.PermissionDeniedException;
import com.github.jukkarol.mapper.AccountMapper;
import com.github.jukkarol.model.Account;
import com.github.jukkarol.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {
        // given
        CreateAccountRequest request = new CreateAccountRequest(123L);

        Account accountToSave = new Account();
        accountToSave.setUserId(123L);
        accountToSave.setBalance(1000);

        Account savedAccount = new Account();
        savedAccount.setUserId(123L);
        savedAccount.setBalance(1000);
        savedAccount.setAccountNumber("0000000123");

        CreateAccountResponse expectedResponse = new CreateAccountResponse(
                "0000000123",
                1000
        );

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.accountToCreateAccountResponse(savedAccount)).thenReturn(expectedResponse);

        // when
        CreateAccountResponse result = accountService.createAccount(request);

        // then
        assertEquals(expectedResponse, result);
        verify(accountRepository).save(any(Account.class));
        verify(accountMapper).accountToCreateAccountResponse(savedAccount);
    }

    @Test
    void shouldReturnAccountsForUser() {
        // given
        GetMyAccountsRequest request = new GetMyAccountsRequest(123L);

        List<Account> accounts = List.of(new Account(), new Account());
        List<AccountDisplayDto> dtoList = List.of(
                new AccountDisplayDto("1111111111", 1000),
                new AccountDisplayDto("2222222222", 2000)
        );

        when(accountRepository.findAllByUserId(123L)).thenReturn(accounts);
        when(accountMapper.accountsToAccountDisplayDtos(accounts)).thenReturn(dtoList);

        // when
        GetMyAccountsResponse response = accountService.getAccountsByUserId(request);

        // then
        assertEquals(dtoList, response.accounts());
        verify(accountRepository).findAllByUserId(123L);
        verify(accountMapper).accountsToAccountDisplayDtos(accounts);
    }

    @Test
    void shouldReturnAccountDetailsIfUserIsOwner() {
        // given
        GetAccountDetailsRequest request = new GetAccountDetailsRequest(123L, "1234567890");

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setUserId(123L);
        account.setBalance(1500);

        AccountDetailsDisplayDto expectedDto = new AccountDetailsDisplayDto(
                1500,
                "1234567890",
                123L,
                account.getCreatedAt(),
                account.getUpdatedAt()
        );

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDetailsDisplayDto(account)).thenReturn(expectedDto);

        // when
        AccountDetailsDisplayDto result = accountService.getAccountByAccountNumber(request);

        // then
        assertEquals(expectedDto, result);

        assertEquals("1234567890", result.accountNumber());
        assertEquals(1500, result.balance());
        assertEquals(123L, result.userId());

        verify(accountRepository).findByAccountNumber("1234567890");
        verify(accountMapper).accountToAccountDetailsDisplayDto(account);

        assertEquals(123L, request.userId());
        assertEquals("1234567890", request.accountNumber());
    }

    @Test
    void shouldThrowPermissionDeniedIfUserIsNotOwner() {
        // given
        GetAccountDetailsRequest request = new GetAccountDetailsRequest(123L, "1234567890");

        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setUserId(999L);

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        // when & then
        assertThrows(PermissionDeniedException.class, () -> {
            accountService.getAccountByAccountNumber(request);
        });

        verify(accountRepository).findByAccountNumber("1234567890");
        verify(accountMapper, never()).accountToAccountDetailsDisplayDto(any());
    }
}
