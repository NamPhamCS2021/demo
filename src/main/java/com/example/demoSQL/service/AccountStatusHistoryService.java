package com.example.demoSQL.service;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistorySearchDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryUserSearchDTO;
import com.example.demoSQL.entity.AccountStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.UUID;

public interface AccountStatusHistoryService {

    ApiResponse<Object> findByAccountNumber(String accountNumber, Pageable pageable);
    ApiResponse<Object> findBetweenByAccount(String accountNumber, LocalDateTime start, LocalDateTime end, Pageable pageable);
    ApiResponse<Object> search(AccountStatusHistorySearchDTO accountStatusHistorySearchDTO, Pageable pageable);
    ApiResponse<Object> selfSearch(String accountNumber, AccountStatusHistoryUserSearchDTO accountStatusHistoryUserSearchDTO, Pageable pageable);

}
