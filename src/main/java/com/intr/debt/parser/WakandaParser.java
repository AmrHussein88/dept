package com.intr.debt.parser;

import com.intr.debt.dto.PayoutDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WakandaParser implements Parser {
    private final String ID_NUMBER = "Company tax number";
    private final String PAYMENT_DATE = "Payment Date";
    private final String AMOUNT = "Amount";

    @Override
    public List<PayoutDto> parseFile(File file) {
        List<PayoutDto> payoutDtos = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), ISO_ENCODING)) {
            CSVFormat format = CSVFormat.Builder.create().
                    setDelimiter(';')
                    .setQuote('"')
                    .setIgnoreSurroundingSpaces(true)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .setRecordSeparator("\r\n").get();

            Iterable<CSVRecord> records = format.parse(reader);

            records.forEach(record -> {
                String paymentDate = record.get(PAYMENT_DATE);
                String amountStr = record.get(AMOUNT).replace(",", ".");

                BigDecimal amount = new BigDecimal(amountStr);
                PayoutDto payoutDto = new PayoutDto();
                payoutDto.setPaymentAmount(amount);
                payoutDto.setPaymentDate(LocalDate.parse(paymentDate));
                payoutDto.setCompanyIdentificationNumber(record.get(ID_NUMBER));
                payoutDtos.add(payoutDto);
            });

        } catch (FileNotFoundException e) {
            log.error("File not found ", e.getMessage());
        } catch (IOException e) {
            log.error("Error occured ", e.getMessage());
        }
        return payoutDtos;
    }
}
