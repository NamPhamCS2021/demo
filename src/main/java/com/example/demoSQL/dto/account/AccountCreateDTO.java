package com.example.demoSQL.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateDTO {
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;
}
