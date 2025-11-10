package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.event.request.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.mapper.CreditMapper;
import com.github.jukkarol.model.Credit;
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
    private final CreditMapper creditMapper;

    @Transactional
    public CreateCreditResponse createCredit(CreateCreditRequest request)
    {
        Credit credit = creditMapper.createCreditRequestToCredit(request);


        //create event

        //if success


        //if no throw err

        //add timer to execute credit installment

        return creditMapper.creditToCreateCreditResponse(credit);
    }

    @Transactional
    public Boolean triggerCreditInstallment(CreditRequestEvent request)
    {
        Credit credit = creditMapper.createCreditRequestToCredit(request);


        //create event

        //if success


        //if no throw err

        //add timer to execute credit installment

        return creditMapper.creditToCreateCreditResponse(credit);
    }
}
