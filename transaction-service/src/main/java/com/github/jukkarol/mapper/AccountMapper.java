package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.accountDto.AccountDetailsDisplayDto;
import com.github.jukkarol.dto.accountDto.AccountDisplayDto;
import com.github.jukkarol.dto.accountDto.response.CreateAccountResponse;
import com.github.jukkarol.model.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    CreateAccountResponse accountToCreateAccountResponse(Account account);

    List<AccountDisplayDto> accountsToAccountDisplayDtos(List<Account> accounts);

    AccountDisplayDto accountToAccountDisplayDto(Account account);

    AccountDetailsDisplayDto accountToAccountDetailsDisplayDto(Account account);
}
