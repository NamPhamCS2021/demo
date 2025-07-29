package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.*;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.AccountStatusHistory;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.enums.CustomerType;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.AccountStatusHistoryRepository;
import com.example.demoSQL.repository.CustomerRepository;
import com.example.demoSQL.specification.AccountSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountStatusHistoryRepository accountStatusHistoryRepository;



    @Override
    @CachePut(value = "accounts", key = "#accountCreateDTO.customerId")
    public ApiResponse<Object> createAccount(AccountCreateDTO accountCreateDTO) {
        try{
            Customer customer = customerRepository.findById(accountCreateDTO.getCustomerId()).orElseThrow(() -> new EntityNotFoundException("Customer with id " + accountCreateDTO.getCustomerId() + " not found"));
            Account account = new Account();
            account.setCustomer(customer);
            if(customer.getType() == CustomerType.TEMPORARY || customer.getType() == CustomerType.PERSONAL){
                account.setAccountLimit(BigDecimal.valueOf(500000));
            }
            else {
                account.setAccountLimit(BigDecimal.valueOf(10000000));
            }
            accountRepository.save(account);

            return new ApiResponse<>(toAccountResponseDTO(account), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch(Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @CachePut(value = "accounts", key = "#id")
    public ApiResponse<Object> updateAccountStatus(Long id, AccountUpdateStatusDTO accountUpdate) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
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

            return new ApiResponse<>(toAccountResponseDTO(existingAccount), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch(Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }

    @Override
    @CachePut(value = "accounts", key = "#id")
    public ApiResponse<Object> updateAccountLimit(Long id, AccountUpdateLimitDTO accountUpdate) {
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Account existingAccount = optionalAccount.get();
            if (accountUpdate.getAccountLimit() != null && accountUpdate.getAccountLimit() != existingAccount.getAccountLimit()) {
                existingAccount.setAccountLimit(accountUpdate.getAccountLimit());
            }
            accountRepository.save(existingAccount);
            return new ApiResponse<>(toAccountResponseDTO(existingAccount), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());

        } catch (EntityNotFoundException e){
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        } catch(Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts", key = "#id")
    public ApiResponse<Object> getAccountById(Long id){
        try{
            Optional<Account> optionalAccount = accountRepository.findById(id);

            if(optionalAccount.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Account account = optionalAccount.get();
            return new ApiResponse<>(toAccountResponseDTO(account), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountsByCustomer", key = "#id")
    public ApiResponse<Object> getAccountByCustomerId(Long id, Pageable pageable) {
        try{
            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if(optionalCustomer.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Customer customer = optionalCustomer.get();
            Page<Account> accountPage = accountRepository.findByCustomerId(id, pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional
    @Cacheable(value = "accountsByCustomer", key = "#id")
    public ApiResponse<Object> getAccountsByCustomerIdAndStatus(Long id, AccountStatus status, Pageable pageable) {
        try{
            Optional<Customer> optionalCustomer = customerRepository.findById(id);
            if(optionalCustomer.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Customer customer = optionalCustomer.get();
            Page<Account> accountPage = accountRepository.findByCustomerIdAndStatus(id, status, pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts")
    public ApiResponse<Object> getAllAccounts(Pageable pageable) {
        try{
            Page<Account> accountPage = accountRepository.findAll(pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }

    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accountsbyStatus", key = "#status")
    public ApiResponse<Object> getAccountsByStatus(AccountStatus status, Pageable pageable) {
        try{
            Page<Account> accountPage = accountRepository.findByStatus(status, pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts")
    public ApiResponse<Object> searchAccounts(AccountSearchDTO dto, Pageable pageable) {
        try{
            Specification<Account> spec = AccountSpecification.hasCustomer(dto.getCustomerId())
                    .and(AccountSpecification.hasStatus(dto.getStatus()))
                    .and(AccountSpecification.hasMaxLimit(dto.getMaxLimit()))
                    .and(AccountSpecification.hasMinLimit(dto.getMinLimit()))
                    .and(AccountSpecification.hasMaxBalance(dto.getMaxBalance()))
                    .and(AccountSpecification.hasMinBalance(dto.getMinBalance()))
                    .and(AccountSpecification.createdBefore(dto.getFrom()))
                    .and(AccountSpecification.createdAfter(dto.getTo()));
            return new ApiResponse<>(accountRepository.findAll(spec, pageable), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts")
    public ApiResponse<Object> searchSelfAccounts(Long id, AccountUserSearchDTO dto, Pageable pageable) {
        try {
            log.info("Search DTO - status: {}, minBalance: {}, maxBalance: {}, minLimit: {}, maxLimit: {}, from: {}, to: {}",
                    dto.getStatus(), dto.getMinBalance(), dto.getMaxBalance(),
                    dto.getMinLimit(), dto.getMaxLimit(), dto.getFrom(), dto.getTo());

            Specification<Account> spec = (root, query, builder) -> builder.conjunction(); // base

            spec = spec.and(AccountSpecification.hasCustomer(id));
            spec = spec.and(AccountSpecification.hasStatus(dto.getStatus()));
            spec = spec.and(AccountSpecification.hasMaxLimit(dto.getMaxLimit()));
            spec = spec.and(AccountSpecification.hasMinLimit(dto.getMinLimit()));
            spec = spec.and(AccountSpecification.hasMaxBalance(dto.getMaxBalance()));
            spec = spec.and(AccountSpecification.hasMinBalance(dto.getMinBalance()));
            spec = spec.and(AccountSpecification.createdBefore(dto.getTo()));
            spec = spec.and(AccountSpecification.createdAfter(dto.getFrom()));

            log.debug("Searching with spec: {}", spec);  // safe logging

            return new ApiResponse<>(
                    accountRepository.findAll(spec, pageable),
                    ReturnMessage.SUCCESS.getCode(),
                    ReturnMessage.SUCCESS.getMessage()
            );
        } catch (Exception e) {
            log.error("Error during self account search", e);
            return new ApiResponse<>(
                    e.getMessage(),
                    ReturnMessage.FAIL.getCode(),
                    ReturnMessage.FAIL.getMessage()
            );
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
