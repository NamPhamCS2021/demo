package com.example.demoSQL.service;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryResponseDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistorySearchDTO;
import com.example.demoSQL.dto.accountstatushistory.AccountStatusHistoryUserSearchDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.AccountStatusHistory;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AccountStatusHistoryRepository;
import com.example.demoSQL.specification.AccountStatusHistorySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AccountStatusHistoryServiceImpl implements AccountStatusHistoryService {

    @Autowired
    private AccountStatusHistoryRepository accountStatusHistoryRepository;
    @Autowired
    private AccountRepository accountRepository;




    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> findByAccountId(Long id, Pageable pageable){
        try{
            Optional<Account> accountOptional = accountRepository.findById(id);
            if(accountOptional.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findByAccountId(id, pageable);
            return new ApiResponse<>(accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> findBetweenByAccount(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable){
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<AccountStatusHistory> accountStatusHistoryPage = accountStatusHistoryRepository.findBetweenByAccountId(id, start, end, pageable);
            return new ApiResponse<>(accountStatusHistoryPage.map(this::toAccountStatusHistoryDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e){
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> search(AccountStatusHistorySearchDTO dto, Pageable pageable) {
        try{
            Specification<AccountStatusHistory> spec = (root, query, builder) -> builder.conjunction(); // base

            spec = spec.and(AccountStatusHistorySpecification.hasAccount(dto.getAccountId()));
            spec = spec.and(AccountStatusHistorySpecification.hasStatus(dto.getStatus()));
            spec = spec.and(AccountStatusHistorySpecification.createdBefore(dto.getEnd()));
            spec = spec.and(AccountStatusHistorySpecification.createdAfter(dto.getStart()));
            return new ApiResponse<>(accountStatusHistoryRepository.findAll(spec, pageable), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> selfSearch(Long id, AccountStatusHistoryUserSearchDTO dto, Pageable pageable) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }

            Specification<AccountStatusHistory> spec = (root, query, builder) -> builder.conjunction(); // base

            spec = spec.and(AccountStatusHistorySpecification.hasAccount(id));
            spec = spec.and(AccountStatusHistorySpecification.hasStatus(dto.getStatus()));
            spec = spec.and(AccountStatusHistorySpecification.createdBefore(dto.getEnd()));
            spec = spec.and(AccountStatusHistorySpecification.createdAfter(dto.getStart()));
            return new ApiResponse<>(accountStatusHistoryRepository.findAll(spec, pageable), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
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
