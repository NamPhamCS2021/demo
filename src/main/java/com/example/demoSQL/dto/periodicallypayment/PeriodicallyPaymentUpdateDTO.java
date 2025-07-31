package com.example.demoSQL.dto.periodicallypayment;

import com.example.demoSQL.enums.SubscriptionStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeriodicallyPaymentUpdateDTO {

    @PositiveOrZero(message = "Amount cannot be negative")
    private BigDecimal amount;
    private SubscriptionStatus status;
}
