package com.example.demoSQL.dto.account;

import com.example.demoSQL.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountUpdateDTO {
    private AccountStatus status;
    private BigDecimal limit;
}
