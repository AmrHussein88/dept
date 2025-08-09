package com.intr.debt.service;

import com.intr.debt.dto.PayoutDto;
import com.intr.debt.httpclient.PayoutClient;
import com.intr.debt.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class WakandaProcessor implements PayoutProcessor {
    @Value("${wakanda.file.path}")
    private String filePath;

    private final Parser wakandaParser;
    private final PayoutClient payoutClient;

    @Autowired
    public WakandaProcessor(Parser wakandaParser, PayoutClient payoutClient) {
        this.wakandaParser = wakandaParser;
        this.payoutClient = payoutClient;
    }

    @Override
    public void processPayout() {
        List<File> files = getWakandaFiles();
        files.forEach(file -> {
            List<PayoutDto> payoutDtos = wakandaParser.parseFile(file);
            payoutDtos.forEach(payoutDto -> {
                payoutClient.processPayout(payoutDto);
            });
        });
    }

    List<File> getWakandaFiles() {
        String yesterday = LocalDate.now()
                .minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File filesDirectory = new File(filePath);
        return Arrays.asList(filesDirectory.listFiles((dir, name) -> name.startsWith("WK_payouts_" + yesterday)));
    }
}
