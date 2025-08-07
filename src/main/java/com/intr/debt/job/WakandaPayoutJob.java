package com.intr.debt.job;

import com.intr.debt.processor.WakandaProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WakandaPayoutJob {
    private final WakandaProcessor wakandaProcessor;

    public WakandaPayoutJob(WakandaProcessor wakandaProcessor) {
        this.wakandaProcessor = wakandaProcessor;
    }

    @Scheduled(cron = "0 */4 * ? * *")
    public void processWakandaFiles() {
        wakandaProcessor.processPayout();

    }
}
