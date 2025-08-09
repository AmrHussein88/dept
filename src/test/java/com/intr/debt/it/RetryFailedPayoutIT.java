package com.intr.debt.it;

import com.intr.debt.job.RetryFailedPayoutsJob;
import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seed a failed payout, retry job sends it successfully -> status becomes "Processed".
 */
public class RetryFailedPayoutIT extends BaseIT {

    @Autowired
    private FailedPayoutRepository failedRepo;

    @Autowired
    private RetryFailedPayoutsJob retryJob;

    @BeforeEach
    void setup() {
        failedRepo.deleteAll();
        wm().resetAll();
        wm().stubFor(post(urlEqualTo("/payout")).willReturn(aResponse().withStatus(200)));
    }

    @Test
    void retry_moves_failed_to_processed_on_success() {
        FailedPayout fp = new FailedPayout();
        fp.setIdNumber("999-9999999");
        fp.setPaymentDate(LocalDate.of(2024, 1, 1));
        fp.setPaymentAmount(new BigDecimal("123.45"));
        fp.setStatus("Failed");
        fp = failedRepo.save(fp);

        retryJob.retryFailedPayouts();

        FailedPayout updated = failedRepo.findById(fp.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("Processed");
        wm().verify(1, postRequestedFor(urlEqualTo("/payout")));
    }
}
