package com.intr.debt.service;

import com.intr.debt.dto.PayoutDto;
import com.intr.debt.httpclient.PayoutClient;
import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RetryPayoutService {
    private final PayoutClient payoutClient;
    private final FailedPayoutRepository failedPayoutRepository;


    public RetryPayoutService(PayoutClient payoutClient, FailedPayoutRepository failedPayoutRepository) {
        this.payoutClient = payoutClient;
        this.failedPayoutRepository = failedPayoutRepository;
    }

    @Transactional
    public void retryFailedPayout(FailedPayout failedPayout) {
        log.info("Retrying payout for ID: {}", failedPayout.getId());
        try {
            boolean isProcessed = payoutClient.processPayout(PayoutDto.builder()
                    .paymentAmount(failedPayout.getPaymentAmount())
                    .companyIdentityNumber(failedPayout.getIdNumber())
                    .paymentDate(failedPayout.getPaymentDate())
                    .build(), true);
            if (isProcessed) {
                failedPayoutRepository.updatePayoutStatus("Processed", failedPayout.getId());
            }
        } catch (Exception e) {
            log.error("Retry failed for payout [{}]: {}", failedPayout.getIdNumber(), e.getMessage(), e);
        }
    }
}
