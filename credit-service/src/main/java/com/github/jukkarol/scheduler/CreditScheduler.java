package com.github.jukkarol.scheduler;

import com.github.jukkarol.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreditScheduler {
    private final CreditService creditService;

    @Scheduled(cron = "${scheduler.credit.decreaseInstallmentsMonthly.cron}")
    public void decreaseInstallmentsMonthly() {
        creditService.processCreditsInstallments();
    }
}
