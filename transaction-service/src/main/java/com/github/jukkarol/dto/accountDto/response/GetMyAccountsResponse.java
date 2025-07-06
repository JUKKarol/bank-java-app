package com.github.jukkarol.dto.accountDto.response;

import com.github.jukkarol.dto.accountDto.AccountDisplayDto;

import java.util.List;

public record GetMyAccountsResponse(
        List<AccountDisplayDto> accounts
) { }
