package com.example.demoSQL.dto.transaction;

import com.example.demoSQL.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreateDTO {
    @NotNull
    private Long accountId;
    private Long receiverId;
    @PositiveOrZero
    private BigDecimal amount;
}
