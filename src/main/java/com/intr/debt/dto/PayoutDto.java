package com.intr.debt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PayoutDto {
    private String companyIdentityNumber;// tax number
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
}
