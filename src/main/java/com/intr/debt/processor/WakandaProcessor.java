package com.intr.debt.processor;

import com.intr.debt.dto.PayoutDto;
import com.intr.debt.httpclient.PayoutClient;
import com.intr.debt.parser.WakandaParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WakandaProcessor implements PayoutProcessor {
    @Value("${intrum.payout.api}")
    private String intrumPayoutUri;

    @Value("${wakanda.file.path}")
    private String filePath;

    private final WakandaParser wakandaParser;
    private final PayoutClient payoutClient;

    @Autowired
    public WakandaProcessor(WakandaParser wakandaParser, PayoutClient payoutClient) {
        this.wakandaParser = wakandaParser;
        this.payoutClient = payoutClient;
    }

    @Override
    public void processPayout() {
        List<File> files = getWakandaFiles();
        List<String> namesOfFiles = files.stream().map(File::getName).collect(Collectors.toList());
        List<PayoutDto> payoutDtos = wakandaParser.parseFile(files.get(0));

        payoutDtos.forEach(payoutDto -> {
        payoutClient.processPayout(payoutDto);
        });
    }

    private List<File> getWakandaFiles() {
        String yesterday = LocalDate.now()
                .minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        File filesDirectory = new File(filePath);
        return Arrays.asList(filesDirectory.listFiles((dir, name) -> name.startsWith("WK_payouts_" + yesterday)));
    }
}
