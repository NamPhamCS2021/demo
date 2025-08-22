package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.*;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    ApiResponse<Object> createAccount(AccountCreateDTO accountCreateDTO);
    ApiResponse<Object> updateAccountStatus(Long id, AccountUpdateStatusDTO accountUpdate);
    ApiResponse<Object> updateAccountLimit(Long id, AccountUpdateLimitDTO accountUpdate);
    ApiResponse<Object> getAccountByCustomerId(Long id, Pageable pageable);
    ApiResponse<Object> getAccountsByCustomerIdAndStatus(Long id, AccountStatus status, Pageable pageable);
    ApiResponse<Object> getAccountById(Long id);
    ApiResponse<Object> getAllAccounts(Pageable pageable);
    ApiResponse<Object> getAccountsByStatus(AccountStatus status, Pageable pageable);
    ApiResponse<Object> searchAccounts(AccountSearchDTO accountSearchDTO, Pageable pageable);
    ApiResponse<Object> searchSelfAccounts(Long id, AccountUserSearchDTO accountUserSearchDTO, Pageable pageable);

    Long findAccountIdByAccountNumber(String accountNumber);

    ApiResponse<Object> getAccountByAccountNumber(String accountNumber);
}
