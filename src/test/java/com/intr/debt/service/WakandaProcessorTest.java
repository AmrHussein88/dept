package com.intr.debt.service;

import com.intr.debt.dto.PayoutDto;
import com.intr.debt.httpclient.PayoutClient;
import com.intr.debt.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WakandaProcessorTest {

    @Mock
    private Parser parser;
    @Mock
    private PayoutClient payoutClient;

    @InjectMocks
    private WakandaProcessor processor;

    private File mockFile;
    private PayoutDto dto;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(processor, "filePath", "src/test/resources/wakanda");
        mockFile = new File("src/test/resources/wakanda/WK_payouts_" + LocalDate.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
        dto = PayoutDto.builder()
                .companyIdentityNumber("123-ABC")
                .paymentDate(LocalDate.of(2025, 8, 7))
                .paymentAmount(new BigDecimal("100.00"))
                .build();
    }

    @Test
    public void testProcessPayout_SingleFileSingleDto() {
        when(parser.parseFile(any(File.class))).thenReturn(List.of(dto));
        WakandaProcessor spy = spy(processor);
        doReturn(List.of(mockFile)).when(spy).getWakandaFiles();

        spy.processPayout();

        verify(payoutClient, times(1)).processPayout(dto);
    }

    @Test
    public void testProcessPayout_NoFiles() {
        WakandaProcessor spy = spy(processor);
        doReturn(Collections.emptyList()).when(spy).getWakandaFiles();

        spy.processPayout();

        verify(payoutClient, never()).processPayout(any());
    }

    @Test
    public void testProcessPayout_EmptyDtosInFile() {
        when(parser.parseFile(any(File.class))).thenReturn(Collections.emptyList());
        WakandaProcessor spy = spy(processor);
        doReturn(List.of(mockFile)).when(spy).getWakandaFiles();

        spy.processPayout();

        verify(payoutClient, never()).processPayout(any());
    }
}