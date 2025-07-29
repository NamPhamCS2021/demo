package com.example.demoSQL.service;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistorySearchDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryUserSearchDTO;
import com.example.demoSQL.entity.AccountStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;

public interface AccountStatusHistoryService {

    ApiResponse<Object> findByAccountId(Long accountId, Pageable pageable);
    ApiResponse<Object> findBetweenByAccount(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);
    ApiResponse<Object> search(AccountStatusHistorySearchDTO accountStatusHistorySearchDTO, Pageable pageable);
    ApiResponse<Object> selfSearch(Long id, AccountStatusHistoryUserSearchDTO accountStatusHistoryUserSearchDTO, Pageable pageable);

}
