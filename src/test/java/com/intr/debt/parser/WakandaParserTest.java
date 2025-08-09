package com.intr.debt.parser;

import com.intr.debt.dto.PayoutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WakandaParserTest {

    private Parser wakandaParser;

    @BeforeEach
    void setup() {
        wakandaParser = new WakandaParser();
    }

    @Test
    public void testValidFileParsing() throws Exception {
        File tempFile = File.createTempFile("valid", ".csv");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("\"Company name\";\"Company tax number\";\"Status\";\"Payment Date\";\"Amount\"\r\n");
            writer.write("\"Wakanda Corp\";\"123-456\";\"Success\";\"2025-08-06\";\"1000,50\"\r\n");
        }

        List<PayoutDto> results = wakandaParser.parseFile(tempFile);

        assertEquals(1, results.size());
        PayoutDto dto = results.get(0);
        assertEquals("123-456", dto.getCompanyIdentityNumber());
        assertEquals(new BigDecimal("1000.50"), dto.getPaymentAmount());
        assertEquals(LocalDate.of(2025, 8, 6), dto.getPaymentDate());
        tempFile.delete();
    }

    @Test
    public void testEmptyFileReturnsEmptyList() throws Exception {
        File emptyFile = File.createTempFile("empty", ".csv");
        List<PayoutDto> results = wakandaParser.parseFile(emptyFile);
        assertTrue(results.isEmpty());
        emptyFile.delete();
    }

}
