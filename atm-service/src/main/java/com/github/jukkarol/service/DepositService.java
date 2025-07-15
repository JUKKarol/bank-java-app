package com.github.jukkarol.service;

import com.github.jukkarol.dto.depositDto.event.DepositRequestEvent;
import com.github.jukkarol.dto.depositDto.request.MakeDepositRequest;
import com.github.jukkarol.dto.depositDto.response.MakeDepositResponse;
import com.github.jukkarol.mapper.DepositMapper;
import com.github.jukkarol.model.Deposit;
import com.github.jukkarol.repository.DepositRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DepositService {
    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;
    private final KafkaTemplate<String, DepositRequestEvent> kafkaTemplate;

    public MakeDepositResponse makeDeposit(MakeDepositRequest request)
    {
        Deposit deposit = depositMapper.makeDepositRequestToDeposit(request);
        requestDeposit(deposit.getAccountNumber(), deposit.getAmount());
        depositRepository.save(deposit);

        return depositMapper.depositToMakeDepositResponse(deposit);
    }

    public void requestDeposit(String accountNumber, Integer amount) {
        DepositRequestEvent event = new DepositRequestEvent(amount, accountNumber);
        kafkaTemplate.send("deposit-requests", event);
    }
}
