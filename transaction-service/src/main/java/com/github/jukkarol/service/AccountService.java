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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public CreateAccountResponse createAccount(CreateAccountRequest request)
    {
        Random generator = new Random();

        Account account = new Account();
        account.setUserId(request.getUser_id());
        account.setBalance(1000);
        String accountNumber = String.format("%010d", generator.nextLong(1_000_000_0000L));
        account.setAccountNumber(accountNumber);

        Account createdAccount = accountRepository.save(account);

        return accountMapper.accountToCreateAccountResponse(createdAccount);
    }

    public GetMyAccountsResponse getAccountsByUserId(GetMyAccountsRequest request) {
        GetMyAccountsResponse response = new GetMyAccountsResponse();

        List<Account> accounts = accountRepository.findAllByUserId(request.getUser_id());

        List<AccountDisplayDto> accountDtos = accountMapper.accountsToAccountDisplayDtos(accounts);

        response.setAccounts(accountDtos);

        return response;
    }

    public AccountDetailsDisplayDto getAccountByAccountNumber(GetAccountDetailsRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber());

        if (!account.getUserId().equals(request.getUser_id()))
        {
            throw new PermissionDeniedException();
        }

        return accountMapper.accountToAccountDetailsDisplayDto(account);
    }
}
