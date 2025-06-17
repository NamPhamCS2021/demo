package com.example.demoSQL.dto.transaction;

import com.example.demoSQL.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreateDTO {
    private Long accountId;
    private Long receiverId;
    private TransactionType type;
    private BigDecimal amount;
}
