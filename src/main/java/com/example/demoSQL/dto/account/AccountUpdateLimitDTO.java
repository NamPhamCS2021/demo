package com.example.demoSQL.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountUpdateLimitDTO {
    private BigDecimal accountLimit;
}
