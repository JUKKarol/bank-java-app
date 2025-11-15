package com.github.jukkarol.scheduler;

import com.github.jukkarol.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreditScheduler {
    private final CreditService creditService;

    @Scheduled(cron = "0 0 1 * *")
    public void decreaseInstallmentsMonthly() {
        creditService.processCreditsInstallments();
    }
}
