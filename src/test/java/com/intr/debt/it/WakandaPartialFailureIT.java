package com.intr.debt.it;

import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import com.intr.debt.service.WakandaProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * First call 200, second call 500 -> one failed row saved with second tax number.
 */
public class WakandaPartialFailureIT extends BaseIT {

    @Autowired
    private WakandaProcessor processor;

    @Autowired
    private FailedPayoutRepository failedRepo;

    @BeforeEach
    void setup() {
        failedRepo.deleteAll();
        wm().resetAll();

        wm().stubFor(post(urlEqualTo("/payout"))
                .inScenario("partial")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("second"));

        wm().stubFor(post(urlEqualTo("/payout"))
                .inScenario("partial")
                .whenScenarioStateIs("second")
                .willReturn(aResponse().withStatus(500)));
    }

    @Test
    void saves_failed_payout_when_one_call_returns_500() throws Exception {
        String yyyymmdd = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path file = wakandaDir().resolve("WK_payouts_" + yyyymmdd + "_235959.csv");

        String header = "\"Company name\";\"Company tax number\";\"Status\";\"Payment Date\";\"Amount\"\r\n";
        String ok = "\"Good One\";\"111-1111111\";\"PAID\";\"2024-01-01\";\"1000,00\"\r\n";
        String bad = "\"Bad One\";\"222-2222222\";\"PAID\";\"2024-01-02\";\"2000,00\"\r\n";
        Files.writeString(file, header + ok + bad, StandardCharsets.ISO_8859_1);

        processor.processPayout();

        List<FailedPayout> all = failedRepo.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getIdNumber()).isEqualTo("222-2222222");
        // One success + one failure => two requests total
        wm().verify(2, postRequestedFor(urlEqualTo("/payout")));
    }
}
