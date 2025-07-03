package com.example.demoSQL.service;


import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;
import com.example.demoSQL.entity.AccountStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;

public interface AccountStatusHistoryService {

    Page<AccountStatusHistoryResponseDTO> findByAccountId(Long accountId, Pageable pageable);
    Page<AccountStatusHistoryResponseDTO> findBetweenByAccount(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

}
