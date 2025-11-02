package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.repository.CreditRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Server
public class CardService {
    private final CreditRepository creditRepository;

    public CreateCreditResponse createCredit(CreateCreditRequest request)
    {

    }
}
