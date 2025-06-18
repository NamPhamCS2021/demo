package com.example.demoSQL.service;

import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateDTO;
import com.example.demoSQL.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountResponseDTO createAccount(AccountCreateDTO accountCreateDTO);
    AccountResponseDTO updateAccountStatus(Long id, AccountUpdateDTO accountUpdate);
    AccountResponseDTO updateAccountLimit(Long id, AccountUpdateDTO accountUpdate);
    AccountResponseDTO getAccountById(Long id);
    Page<AccountResponseDTO> getAccountsByCustomer(Long customerId, Pageable pageable);
    Page<AccountResponseDTO> getAllAccounts(Pageable pageable);
    AccountResponseDTO getAccountByAccountNumber(String accountNumber);
    Page<AccountResponseDTO> getAccountsByStatus(AccountStatus status, Pageable pageable);
    Page<AccountResponseDTO> getAccountsByCustomerAndStatus(Long customerId, AccountStatus status, Pageable pageable);
}
