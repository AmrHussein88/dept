package com.intr.debt.httpclient;

import com.intr.debt.dto.PayoutDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PayoutClient {
    @Value("${intrum.payout.api}")
    private String payoutUri;
    private RestTemplate restTemplate;

    public PayoutClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void processPayout(PayoutDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PayoutDto> request = new HttpEntity<>(dto, headers);
        restTemplate.postForEntity(payoutUri, request, String.class);
    }
}
