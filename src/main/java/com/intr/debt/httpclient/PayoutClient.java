package com.intr.debt.httpclient;

import com.intr.debt.dto.PayoutDto;
import com.intr.debt.model.FailedPayout;
import com.intr.debt.repository.FailedPayoutRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutClient {
    private final RestTemplate restTemplate;
    private final FailedPayoutRepository failedPayoutRepository;
    @Value("${intrum.payout.api}")
    private String payoutUri;

    public void processPayout(PayoutDto dto) {
        processPayout(dto, false);
    }

    public boolean processPayout(PayoutDto dto, boolean isRetry) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PayoutDto> request = new HttpEntity<>(dto, headers);
        try {
            restTemplate.postForEntity(payoutUri, request, String.class);
            return true;
        } catch (Exception e) {
            log.error("An error occured during sending data to external system ", e);

            if (!isRetry) {
                mapAndSaveFailedPayout(dto);
            }
            return false;
        }
    }

    private void mapAndSaveFailedPayout(PayoutDto dto) {
        FailedPayout failedPayout = new FailedPayout();
        failedPayout.setIdNumber(dto.getCompanyIdentityNumber());
        failedPayout.setPaymentAmount(dto.getPaymentAmount());
        failedPayout.setPaymentDate(dto.getPaymentDate());
        failedPayoutRepository.save(failedPayout);
    }
}
