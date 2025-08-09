package com.intr.debt.httpclient;

import com.intr.debt.dto.PayoutDto;
import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayoutClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private FailedPayoutRepository failedPayoutRepository;

    @InjectMocks
    private PayoutClient client;

    private PayoutDto dto;


    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(client, "payoutUri", "http://fakeuri");

        dto = PayoutDto.builder()
                .companyIdentityNumber("123-456")
                .paymentDate(LocalDate.of(2025, 8, 7))
                .paymentAmount(new BigDecimal("1000.50"))
                .build();
    }

    @Test
    public void testSuccessCall() {

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        client.processPayout(dto);
        verify(failedPayoutRepository, never()).save(any());
    }

    @Test
    public void testFailedPayoutNonRetryShouldSave() {
        doThrow(new RuntimeException("Service down")).when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));

        client.processPayout(dto, false);
        verify(failedPayoutRepository, times(1)).save(any(FailedPayout.class));
    }

    @Test
    public void testFailedPayoutInRetryShouldNotSave() {
        doThrow(new RuntimeException("Service down")).when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));

        client.processPayout(dto, true);
        verify(failedPayoutRepository, never()).save(any());
    }
}
