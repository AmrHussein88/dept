package com.intr.debt.job;

import com.intr.debt.service.WakandaProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class WakandaPayoutJob {
    private final WakandaProcessor wakandaProcessor;

    @Scheduled(cron = "0 0 1 * * ?")
    public void processWakandaFiles() {
        wakandaProcessor.processPayout();

    }
}
