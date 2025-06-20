package com.github.jukkarol.service;

import com.github.jukkarol.dto.withdrawalDto.event.WithdrawalRequestedEvent;
import com.github.jukkarol.dto.withdrawalDto.request.MakeWithdrawalRequest;
import com.github.jukkarol.dto.withdrawalDto.response.MakeWithdrawalResponse;
import com.github.jukkarol.mapper.WithdrawalMapper;
import com.github.jukkarol.model.Withdrawal;
import com.github.jukkarol.repository.WithdrawalRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;
    private final WithdrawalMapper withdrawalMapper;
    private final KafkaTemplate<String, WithdrawalRequestedEvent> kafkaTemplate;

    public MakeWithdrawalResponse makeWithdrawal(MakeWithdrawalRequest request)
    {
        Withdrawal withdrawal = withdrawalMapper.makeWithdrawalRequestToWithdrawal(request);
        requestWithdrawal(withdrawal.getAccountNumber(), withdrawal.getAmount());
        withdrawalRepository.save(withdrawal);

        return withdrawalMapper.withdrawalToWithdrawalResponse(withdrawal);
    }

    public void requestWithdrawal(String accountNumber, Integer amount) {
        WithdrawalRequestedEvent event = new WithdrawalRequestedEvent();
        event.setAccountNumber(accountNumber);
        event.setAmount(amount);
        kafkaTemplate.send("withdrawal-requests", event);
    }
}
