package com.example.demoSQL.dto.account;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateDTO {
    private Long customerId;
    private BigDecimal accountLimit;
}
