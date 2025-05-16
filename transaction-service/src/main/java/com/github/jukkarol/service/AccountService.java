package com.github.jukkarol.service;

import com.github.jukkarol.dto.accountDto.request.CreateAccountRequest;
import com.github.jukkarol.dto.accountDto.response.CreateAccountResponse;
import com.github.jukkarol.mapper.AccountMapper;
import com.github.jukkarol.model.Account;
import com.github.jukkarol.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        account.setUser_id(request.getUser_id());
        account.setBalance(1000);
        String accountNumber = String.format("%010d", generator.nextLong(1_000_000_0000L));
        account.setAccountNumber(accountNumber);

        Account createdAccount = accountRepository.save(account);

        return accountMapper.accountToCreateAccountResponse(createdAccount);
    }
}
