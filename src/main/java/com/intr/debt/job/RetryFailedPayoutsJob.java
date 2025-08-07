package com.intr.debt.job;

import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import com.intr.debt.service.RetryPayoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RetryFailedPayoutsJob {
    private final FailedPayoutRepository failedPayoutRepository;
    private final RetryPayoutService retryPayoutService;

    public RetryFailedPayoutsJob(FailedPayoutRepository failedPayoutRepository, RetryPayoutService retryPayoutService) {
        this.failedPayoutRepository = failedPayoutRepository;
        this.retryPayoutService = retryPayoutService;
    }

    @Scheduled(cron = "0 * * ? * *")
    public void retryFailedPayouts() {
        List<FailedPayout> failedPayouts = failedPayoutRepository.findAllByStatus("Failed");
        failedPayouts.forEach(failedPayout -> {
            retryPayoutService.retryFailedPayout(failedPayout);
        });

    }

}
