package com.github.jukkarol.dto.accountDto.response;

import com.github.jukkarol.dto.accountDto.AccountDisplayDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyAccountsResponse {
    private List<AccountDisplayDto> accounts;
}
