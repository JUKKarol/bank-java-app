package com.github.jukkarol.kafka.listener;

import com.github.jukkarol.dto.depositDto.event.DepositRequestEvent;
import com.github.jukkarol.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepositRequestListener {
    private final TransactionService transactionService;

    @KafkaListener(topics = "deposit-requests", groupId = "transaction-group")
    public void listen(DepositRequestEvent event) {
        transactionService.makeDeposit(event);
    }
}
