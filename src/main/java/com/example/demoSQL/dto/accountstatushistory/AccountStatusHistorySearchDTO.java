package com.example.demoSQL.dto.accountstatushistory;


import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.AccountStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AccountStatusHistorySearchDTO {
    private Long accountId;
    private AccountStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;
}
