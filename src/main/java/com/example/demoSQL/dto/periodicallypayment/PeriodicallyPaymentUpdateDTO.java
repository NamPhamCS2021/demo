package com.example.demoSQL.dto.periodicallypayment;

import com.example.demoSQL.enums.SubscriptionStatus;
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

    private BigDecimal amount;
    private SubscriptionStatus status;
}
