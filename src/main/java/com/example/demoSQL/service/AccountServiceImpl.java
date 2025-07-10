package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateLimitDTO;
import com.example.demoSQL.dto.account.AccountUpdateStatusDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.AccountStatusHistory;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.CustomerType;
import com.example.demoSQL.enums.EResponseCode;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AccountStatusHistoryRepository;
import com.example.demoSQL.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    private final AccountStatusHistoryRepository accountStatusHistoryRepository;



    @Override
    @CachePut(value = "accounts", key = "#accountCreateDTO.customerId")
    public ApiResponse<Object> createAccount(AccountCreateDTO accountCreateDTO) {
        try{
            Optional<Customer> optionalCustomer = customerRepository.findById(accountCreateDTO.getCustomerId());
            if(optionalCustomer.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Customer customer = optionalCustomer.get();
            Account account = new Account();
            account.setCustomer(customer);
            if(customer.getType().equals(CustomerType.TEMPORARY) || customer.getType().equals(CustomerType.PERSONAL)){
                account.setAccountLimit(BigDecimal.valueOf(500000));
            }
            else {
                account.setAccountLimit(BigDecimal.valueOf(10000000));
            }
            accountRepository.save(account);

            return new ApiResponse<>(toAccountResponseDTO(account), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch(Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }

    @Override
    @CachePut(value = "accounts", key = "#id")
    public ApiResponse<Object> updateAccountStatus(Long id, AccountUpdateStatusDTO accountUpdate) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Account existingAccount = optionalAccount.get();
            if (accountUpdate.getStatus() != null && accountUpdate.getStatus() != existingAccount.getStatus()) {
                existingAccount.setStatus(accountUpdate.getStatus());
            }

            AccountStatusHistory history = new AccountStatusHistory();
            history.setAccount(existingAccount);
            history.setStatus(existingAccount.getStatus());
            accountStatusHistoryRepository.save(history);
            accountRepository.save(existingAccount);

            return new ApiResponse<>(toAccountResponseDTO(existingAccount), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch(Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @CachePut(value = "accounts", key = "#id")
    public ApiResponse<Object> updateAccountLimit(Long id, AccountUpdateLimitDTO accountUpdate) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }

            Account existingAccount = optionalAccount.get();
            if (accountUpdate.getAccountLimit() != null && accountUpdate.getAccountLimit() != existingAccount.getAccountLimit()) {
                existingAccount.setAccountLimit(accountUpdate.getAccountLimit());
            }
            accountRepository.save(existingAccount);
            return new ApiResponse<>(toAccountResponseDTO(existingAccount), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());

        } catch(Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts", key = "#id")
    public ApiResponse<Object> getAccountById(Long id){
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Account existingAccount = optionalAccount.get();
            return new ApiResponse<>(toAccountResponseDTO(existingAccount), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts")
    public ApiResponse<Object> getAllAccounts(Pageable pageable) {
        try{
            Page<Account> accountPage = accountRepository.findAll(pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountsbyNumber", key = "#accountNumber")
    public ApiResponse<Object> getAccountByAccountNumber(String accountNumber) {
        try{
            Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(EResponseCode.NOT_FOUND.getCode(), EResponseCode.NOT_FOUND.getMessage());
            }
            Account account = optionalAccount.get();
            return new ApiResponse<>(toAccountResponseDTO(account), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountsbyStatus", key = "#status")
    public ApiResponse<Object> getAccountsByStatus(AccountStatus status, Pageable pageable) {
        try{
            Page<Account> accountPage = accountRepository.findByStatus(status, pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), EResponseCode.SUCCESS.getCode(), EResponseCode.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), EResponseCode.FAIL.getCode(), EResponseCode.FAIL.getMessage());
        }
    }

    //helper
    public AccountResponseDTO toAccountResponseDTO(Account account){
        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .accountLimit(account.getAccountLimit())
                .openingDate(account.getOpeningDate())
                .customerName(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName()).build();
    }
}
