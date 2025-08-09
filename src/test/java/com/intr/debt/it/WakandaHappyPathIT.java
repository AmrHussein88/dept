package com.intr.debt.it;

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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Two CSV rows, both succeed -> no failed rows, two HTTP posts.
 */
public class WakandaHappyPathIT extends BaseIT {

    @Autowired
    private WakandaProcessor processor;

    @Autowired
    private FailedPayoutRepository failedRepo;

    @BeforeEach
    void setup() {
        failedRepo.deleteAll();
        wm().resetAll();
        // Default: accept all posts
        wm().stubFor(post(urlEqualTo("/payout")).willReturn(aResponse().withStatus(200)));
    }

    @Test
    void processesCsv_and_sends_all_payouts() throws Exception {
        // Arrange CSV file for "yesterday"
        String yyyymmdd = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path file = wakandaDir().resolve("WK_payouts_" + yyyymmdd + "_235959.csv");

        String header = "\"Company name\";\"Company tax number\";\"Status\";\"Payment Date\";\"Amount\"\r\n";
        String r1 = "\"Iron suites\";\"156-5562415\";\"PENDING\";\"2023-11-17\";\"7000,10\"\r\n";
        String r2 = "\"Shield factory\";\"557-3562662\";\"PAID\";\"2022-05-01\";\"9999\"\r\n";
        Files.writeString(file, header + r1 + r2, StandardCharsets.ISO_8859_1);

        // Act
        processor.processPayout();

        // Assert
        wm().verify(2, postRequestedFor(urlEqualTo("/payout")));
        assertThat(failedRepo.count()).isZero();
    }
}
