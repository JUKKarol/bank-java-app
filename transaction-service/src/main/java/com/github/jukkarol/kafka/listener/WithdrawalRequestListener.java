package com.github.jukkarol.kafka.listener;

import com.github.jukkarol.dto.withdrawalDto.event.request.WithdrawalRequestEvent;
import com.github.jukkarol.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WithdrawalRequestListener {
    private final TransactionService transactionService;

    @KafkaListener(topics = "withdrawal-requests", groupId = "transaction-group")
    public void listen(WithdrawalRequestEvent event) {
        transactionService.processWithdrawal(event);
    }
}
