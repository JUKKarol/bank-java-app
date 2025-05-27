package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.accountDto.response.CreateAccountResponse;
import com.github.jukkarol.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    CreateAccountResponse accountToCreateAccountResponse(Account account);
}
