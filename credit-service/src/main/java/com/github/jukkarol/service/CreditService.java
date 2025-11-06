package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.repository.CreditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class CreditService {
    private final CreditRepository creditRepository;

    @Transactional
    public CreateCreditResponse createCredit(CreateCreditRequest request)
    {
        //chek is account exist

        //if yes - create credit
        //if no throw err

        //add timer to execute credit installment
    }
}
