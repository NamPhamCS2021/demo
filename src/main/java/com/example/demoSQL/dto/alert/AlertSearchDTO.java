package com.example.demoSQL.dto.alert;

import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import com.example.demoSQL.enums.TransactionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AlertSearchDTO {
    private UUID transactionPublicId;
    private AlertType type;
    private AlertStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;
}
