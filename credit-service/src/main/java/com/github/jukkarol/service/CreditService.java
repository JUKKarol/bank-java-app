package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.event.request.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.event.request.SingleCreditRequest;
import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.request.ProcessSpecifiedCreditsInstallmentsRequest;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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

        int Installments = request.amountTotal().divide(request.amountMonthly(), 0, RoundingMode.UP).intValue();

        credit.setInstallmentTotal(Installments);
        credit.setInstallmentLeft(Installments);

        creditRepository.save(credit);
        log.info("Credit created: {}", credit);

        return creditMapper.creditToCreateCreditResponse(credit);
    }

    @Transactional
    public void processCreditsInstallments()
    {
        List<Credit> creditsToDecrement = creditRepository.findAllCreditsToDecrementInstallments()
                .orElseThrow(() -> new NotFoundException(Credit.class.getSimpleName(), "processCreditsInstallments"));

        int affectedCredits = creditRepository.decrementInstallmentsForAll();
        log.info("Credits affected by installments decrement: {}", affectedCredits);

        List<SingleCreditRequest> creditsDto = creditMapper.creditsToSingleCreditsRequests(creditsToDecrement);
        CreditRequestEvent request = new CreditRequestEvent(creditsDto);
        kafkaTemplate.send("credit-requests", request);
    }

    @Transactional
    public void processSpecifiedCreditsInstallments(ProcessSpecifiedCreditsInstallmentsRequest request)
    {
        List<Credit> creditsToDecrement = creditRepository.findSpecifiedCreditsToDecrementInstallments(request.ids())
                .orElseThrow(() -> new NotFoundException(Credit.class.getSimpleName(), "processCreditsInstallments"));

        List<CreditHistory> creditHistories = creditsToDecrement.stream()
                .map(credit -> creditHistoryMapper.creditToCreditHistory(credit, calculateAmountLeft(credit)))
                .toList();

        creditHistoryRepository.saveAll(creditHistories);

        int affectedCredits = creditRepository.decrementSpecifiedInstallments(request.ids());
        log.info("Credits affected by installments decrement: {}", affectedCredits);

        List<SingleCreditRequest> creditsDto = creditMapper.creditsToSingleCreditsRequests(creditsToDecrement);
        CreditRequestEvent event = new CreditRequestEvent(creditsDto);
        kafkaTemplate.send("credit-requests", event);
    }

    private BigDecimal calculateAmountLeft(Credit credit) {
        int installmentsPaid = credit.getInstallmentTotal() - credit.getInstallmentLeft() + 1;
        return credit.getAmountTotal()
                .subtract(credit.getAmountMonthly()
                        .multiply(BigDecimal.valueOf(installmentsPaid)));
    }
}
