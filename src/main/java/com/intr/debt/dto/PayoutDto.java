package com.intr.debt.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayoutDto {
    private String companyIdentificationNumber;// tax number
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
}
