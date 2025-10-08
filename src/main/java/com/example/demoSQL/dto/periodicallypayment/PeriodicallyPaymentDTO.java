package com.example.demoSQL.dto.periodicallypayment;

import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.Period;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeriodicallyPaymentDTO {
    @NotNull(message = "Account ID cannot be null")
    private String accountNumber;
    @PositiveOrZero(message = "Amount cannot be negative")
    private BigDecimal amount;
    private String description;
    private LocalDateTime startedAt;
    private Period period;
}