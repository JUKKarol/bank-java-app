package com.github.jukkarol.kafka;

import com.github.jukkarol.dto.depositDto.event.DepositRequestedEvent;
import com.github.jukkarol.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepositRequestListener {
    private final TransactionService transactionService;

    @KafkaListener(topics = "deposit-requests", groupId = "transaction-group")
    public void listen(DepositRequestedEvent event) {
        transactionService.makeDeposit(event);
    }
}
