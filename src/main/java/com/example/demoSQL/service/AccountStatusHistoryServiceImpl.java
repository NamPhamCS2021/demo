package com.example.demoSQL.service;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;
import com.example.demoSQL.entity.AccountStatusHistory;
import com.example.demoSQL.enums.EResponseCode;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AccountStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountStatusHistoryServiceImpl implements AccountStatusHistoryService {

    private final AccountStatusHistoryRepository accountStatusHistoryRepository;

    private final AccountRepository accountRepository;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountStatusHistoryByAccount", key = "#id")
    public ApiResponse<Object> findByAccountId(Long id, Pageable pageable){
        if(!accountRepository.existsById(id)){
            return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
        }
        Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findByAccountId(id, pageable);
        return new ApiResponse<>(accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> findBetweenByAccount(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable){
        if(!accountRepository.existsById(id)) {
            return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
        }
        Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findBetweenByAccountId(id, start, end, pageable);
        return new ApiResponse<>(accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
    }

    //helper
    private AccountStatusHistoryResponseDTO toAccountStatusHistoryDTO(AccountStatusHistory accountStatusHistory) {
        return AccountStatusHistoryResponseDTO.builder()
                .accountNumber(accountStatusHistory.getAccount().getAccountNumber())
                .status(accountStatusHistory.getStatus())
                .timestamp(accountStatusHistory.getTimestamp())
                .build();
    }


}
