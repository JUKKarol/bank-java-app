package com.github.jukkarol.kafka.listener;

import com.github.jukkarol.dto.creditDto.event.CreditRequestEvent;
import com.github.jukkarol.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreditRequestListener {
    private final TransactionService transactionService;

    @KafkaListener(topics = "credit-requests", groupId = "transaction-group")
    public void listen(CreditRequestEvent event) {
        transactionService.processCreditsInstallments(event);
    }
}
