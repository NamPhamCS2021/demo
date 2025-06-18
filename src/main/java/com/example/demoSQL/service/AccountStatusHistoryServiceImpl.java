package com.example.demoSQL.service;


import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;
import com.example.demoSQL.entity.AccountStatusHistory;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AccountStatusHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AccountStatusHistoryServiceImpl implements AccountStatusHistoryService {

    @Autowired
    private AccountStatusHistoryRepository accountStatusHistoryRepository;
    @Autowired
    private AccountRepository accountRepository;

    public AccountStatusHistoryServiceImpl(AccountStatusHistoryRepository accountStatusHistoryRepository) {
        this.accountStatusHistoryRepository = accountStatusHistoryRepository;
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountStatusHistoryByAccount", key = "#id")
    public Page<AccountStatusHistoryResponseDTO> findByAccountId(Long id, Pageable pageable){
        if(!accountRepository.existsById(id)){
            throw new EntityNotFoundException("Account with id "+id+" not found");
        }
        Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findByAccountId(id, pageable);
        return accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountStatusHistoryByAccountNumber", key = "#accountNumber")
    public Page<AccountStatusHistoryResponseDTO> findByAccountNumber(String accountNumber, Pageable pageable){
        if(!accountRepository.existsByAccountNumber(accountNumber)){
            throw new EntityNotFoundException("Account with account number "+accountNumber+" not found");
        }
        Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findByAccountNumber(accountNumber, pageable);
        return accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AccountStatusHistoryResponseDTO> findBetweenByAccount(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable){
        if(!accountRepository.existsById(id)) {
            throw new EntityNotFoundException("Account with id " + id + " not found");
        }
        Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findBetweenByAccountId(id, start, end, pageable);
        return accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountStatusHistoryResponseDTO> findBetweenByAccountNumber(String accountNumber, LocalDateTime start, LocalDateTime end, Pageable pageable){
        if(!accountRepository.existsByAccountNumber(accountNumber)) {
            throw new EntityNotFoundException("Account with account number " + accountNumber + " not found");
        }
        Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findBetweenByAccountNumber(accountNumber, start, end, pageable);
        return accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO);
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
