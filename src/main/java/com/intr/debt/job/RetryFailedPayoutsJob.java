package com.intr.debt.job;

import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import com.intr.debt.service.RetryPayoutService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class RetryFailedPayoutsJob {
    private final FailedPayoutRepository failedPayoutRepository;
    private final RetryPayoutService retryPayoutService;

    @Scheduled(cron = "0 */15 * ? * *")
    public void retryFailedPayouts() {
        retryPayoutService.checkAndRetryPayouts ();
    }

}
