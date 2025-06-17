package com.example.demoSQL.dto.transaction;

import com.example.demoSQL.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private  Long customerId;
    private  Long receiverId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String location;
}
