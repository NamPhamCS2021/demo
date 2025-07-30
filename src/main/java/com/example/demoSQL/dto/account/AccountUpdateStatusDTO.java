package com.example.demoSQL.dto.account;

import com.example.demoSQL.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountUpdateStatusDTO {
    private AccountStatus status;
}
