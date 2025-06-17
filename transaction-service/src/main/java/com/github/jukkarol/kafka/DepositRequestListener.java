package com.github.jukkarol.kafka;

import com.github.jukkarol.dto.depositDto.event.DepositRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DepositRequestListener {

    @KafkaListener(topics = "deposit-requests", groupId = "transaction-group")
    public void listen(DepositRequestedEvent event) {
        System.out.println("Received deposit: " + event.getAmount() + " for account: " + event.getAccountNumber());
        // TODO: Add funds to the account
    }
}
