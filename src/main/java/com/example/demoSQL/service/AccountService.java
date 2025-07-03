package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateLimitDTO;
import com.example.demoSQL.dto.account.AccountUpdateStatusDTO;
import com.example.demoSQL.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    ApiResponse<Object> createAccount(AccountCreateDTO accountCreateDTO);
    ApiResponse<Object> updateAccountStatus(Long id, AccountUpdateStatusDTO accountUpdate);
    ApiResponse<Object> updateAccountLimit(Long id, AccountUpdateLimitDTO accountUpdate);
    ApiResponse<Object> getAccountById(Long id);
    ApiResponse<Object> getAllAccounts(Pageable pageable);
    ApiResponse<Object> getAccountByAccountNumber(String accountNumber);
    ApiResponse<Object> getAccountsByStatus(AccountStatus status, Pageable pageable);

}
