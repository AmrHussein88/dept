package com.intr.debt.controller;


import com.intr.debt.service.RetryPayoutService;
import com.intr.debt.service.WakandaProcessor;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payouts")
@AllArgsConstructor
public class ManualTriggerController {

    private final WakandaProcessor wakandaProcessor;
    private final RetryPayoutService retryPayoutService;

    @PostMapping("/process-payout")
    public ResponseEntity triggerWakandaPayout() {
        wakandaProcessor.processPayout();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/retry-payout")
    public ResponseEntity triggerRetryFailedPayout() {
        retryPayoutService.checkAndRetryPayouts();

        return ResponseEntity.ok().build();
    }
}

