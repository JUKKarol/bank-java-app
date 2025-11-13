package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.event.request.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.event.response.CreditResponseEvent;
import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.mapper.CreditMapper;
import com.github.jukkarol.model.Credit;
import com.github.jukkarol.model.CreditHistory;
import com.github.jukkarol.repository.CreditHistoryRepository;
import com.github.jukkarol.repository.CreditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class CreditService {
    private final CreditRepository creditRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final CreditMapper creditMapper;
    private final ReplyingKafkaTemplate<String, CreditRequestEvent, CreditResponseEvent> replyingKafkaTemplate;

    @Transactional
    public CreateCreditResponse createCredit(CreateCreditRequest request)
    {
        //check is account number exists

        Credit credit = creditMapper.createCreditRequestToCredit(request);
        creditRepository.save(credit);

        return creditMapper.creditToCreateCreditResponse(credit);
    }

    @Transactional
    public Boolean triggerCreditInstallment(CreditRequestEvent request)
    {
        CreditHistory credit = creditMapper.createCreditRequestToCredit(request);


        //create event

        //if success


        //if no throw err

        //add timer to execute credit installment

        return creditMapper.creditToCreateCreditResponse(credit);
    }
}
