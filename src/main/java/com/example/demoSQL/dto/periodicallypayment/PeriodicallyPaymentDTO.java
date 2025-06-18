package com.example.demoSQL.dto.periodicallypayment;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.Period;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeriodicallyPaymentDTO {
    private Long accountId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime startedAt;
    private Period period;
}