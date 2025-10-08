package com.example.demoSQL.dto.accountstatushistory;


import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.AccountStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountStatusHistorySearchDTO {
    private String accountNumber;
    private AccountStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;
}
