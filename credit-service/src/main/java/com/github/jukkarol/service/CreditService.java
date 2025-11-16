package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.event.request.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.event.request.SingleCreditRequest;
import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.exception.NotFoundException;
import com.github.jukkarol.mapper.CreditHistoryMapper;
import com.github.jukkarol.mapper.CreditMapper;
import com.github.jukkarol.model.Credit;
import com.github.jukkarol.model.CreditHistory;
import com.github.jukkarol.repository.CreditHistoryRepository;
import com.github.jukkarol.repository.CreditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CreditService {
    private final CreditRepository creditRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final CreditMapper creditMapper;
    private final CreditHistoryMapper creditHistoryMapper;
    private final KafkaTemplate<String, CreditRequestEvent> kafkaTemplate;

    @Transactional
    public CreateCreditResponse createCredit(CreateCreditRequest request)
    {
        //ToDo: check is account number exists

        Credit credit = creditMapper.createCreditRequestToCredit(request);
        creditRepository.save(credit);

        return creditMapper.creditToCreateCreditResponse(credit);
    }

    @Transactional
    public void processCreditsInstallments()
    {
        List<Credit> creditsToDecrement = creditRepository.findAllCreditsToDecrementInstallments()
                .orElseThrow(() -> new NotFoundException(Credit.class.getSimpleName(), "processCreditsInstallments"));

        List<CreditHistory> creditHistory = creditMapper.creditsToCreditsHistory(creditsToDecrement);
        creditHistoryRepository.saveAll(creditHistory);

        int affectedCredits = creditRepository.decrementInstallmentsForAll();
        log.info("Credits affected by installments decrement: {}", affectedCredits);

        List<SingleCreditRequest> creditsDto = creditMapper.creditsToSingleCreditsRequests(creditsToDecrement);
        CreditRequestEvent request = new CreditRequestEvent(creditsDto);
        kafkaTemplate.send("credit-requests", request);
    }
}
