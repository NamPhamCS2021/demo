package com.example.demoSQL.dto.alert;

import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {
    private Long accountId;
    private Long transactionId;
    private String description;
    private AlertType type;
    private AlertStatus status;
    private LocalDateTime timestamp;
}
