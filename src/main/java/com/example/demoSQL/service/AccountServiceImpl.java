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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;


    private final CustomerRepository customerRepository;


    private final AccountStatusHistoryRepository accountStatusHistoryRepository;



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
            if (accountUpdate.getAccountLimit() != null && !accountUpdate.getAccountLimit().equals(existingAccount.getAccountLimit())) {
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
    public ApiResponse<Object> getAccountByCustomerId(Long id, Pageable pageable) {
        try{
            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if(optionalCustomer.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Account> accountPage = accountRepository.findByCustomerId(id, pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<Object> getAccountsByCustomerIdAndStatus(Long id, AccountStatus status, Pageable pageable) {
        try{
            Optional<Customer> optionalCustomer = customerRepository.findById(id);
            if(optionalCustomer.isEmpty()){
                return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
            }
            Page<Account> accountPage = accountRepository.findByCustomerIdAndStatus(id, status, pageable);
            return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    public ApiResponse<Object> searchAccounts(AccountSearchDTO dto, Pageable pageable) {
        try{

            if(dto == null) {
                return new ApiResponse<>(ReturnMessage.NULL_VALUE.getCode(), ReturnMessage.NULL_VALUE.getMessage());
            }

            if((dto.getMinLimit() != null && dto.getMaxLimit() != null && dto.getMinLimit().compareTo(dto.getMaxLimit()) > 0)
                    ||(dto.getMinBalance() != null && dto.getMaxBalance() != null && dto.getMinBalance().compareTo(dto.getMaxBalance()) > 0))
            {
                return new ApiResponse<>(ReturnMessage.INVALID_ARGUMENTS.getCode(), ReturnMessage.INVALID_ARGUMENTS.getMessage());
            }

            Specification<Account> spec = (root, query, builder) -> builder.conjunction(); // base

            spec = spec.and(AccountSpecification.hasCustomer(dto.getCustomerId()));
            spec = spec.and(AccountSpecification.hasStatus(dto.getStatus()));
            spec = spec.and(AccountSpecification.hasMaxLimit(dto.getMaxLimit()));
            spec = spec.and(AccountSpecification.hasMinLimit(dto.getMinLimit()));
            spec = spec.and(AccountSpecification.hasMaxBalance(dto.getMaxBalance()));
            spec = spec.and(AccountSpecification.hasMinBalance(dto.getMinBalance()));
            spec = spec.and(AccountSpecification.createdBefore(dto.getFrom()));
            spec = spec.and(AccountSpecification.createdAfter(dto.getTo()));
            Page<Account> result = accountRepository.findAll(spec, pageable);
            Page<AccountResponseDTO> dtoRes = result.map(this::toAccountResponseDTO);
            return new ApiResponse<>(dtoRes, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), ReturnMessage.FAIL.getCode(), ReturnMessage.FAIL.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Object> searchSelfAccounts(Long id, AccountUserSearchDTO dto, Pageable pageable) {
        try {
            if(dto == null) {
                return new ApiResponse<>(ReturnMessage.NULL_VALUE.getCode(), ReturnMessage.NULL_VALUE.getMessage());
            }

            if((dto.getMinBalance() != null && dto.getMaxBalance() != null && dto.getMinBalance().compareTo(dto.getMaxBalance()) > 0) ||
                    (dto.getMinLimit() != null && dto.getMaxLimit() != null && dto.getMinLimit().compareTo(dto.getMaxLimit()) > 0))
            {
                return new ApiResponse<>(ReturnMessage.INVALID_ARGUMENTS.getCode(), ReturnMessage.INVALID_ARGUMENTS.getMessage());
            }
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

            Page<Account> result = accountRepository.findAll(spec, pageable);
            Page<AccountResponseDTO> dtoRes = result.map(this::toAccountResponseDTO);

            log.debug("Searching with spec: {}", spec);  // safe logging

            return new ApiResponse<>(
                    dtoRes,
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



    @Override
    public Long findAccountIdByAccountNumber(String accountNumber){
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        return optionalAccount.map(Account::getId).orElse(null);
    }

    @Override
    public ApiResponse<Object> getAccountByAccountNumber(String accountNumber) {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if(optionalAccount.isEmpty()){
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        }
        Account account = optionalAccount.get();
        return new ApiResponse<>(toAccountResponseDTO(account), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
    }

    @Override
    public ApiResponse<Object> getReceiver(String accountNumber) {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if(optionalAccount.isEmpty()){
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        }
        Account account = optionalAccount.get();
        ReceiverDTO receiverDTO = ReceiverDTO.builder().
                accountNumber(account.getAccountNumber()).
                status(account.getStatus()).
                firstName(account.getCustomer().getFirstName()).
                lastName(account.getCustomer().getLastName()).build();
        return new ApiResponse<>(receiverDTO, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
    }

    @Override
    public ApiResponse<Object> getAccountsByEmail(String email, Pageable pageable) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        if(optionalCustomer.isEmpty()){
            return new ApiResponse<>(ReturnMessage.NOT_FOUND.getCode(), ReturnMessage.NOT_FOUND.getMessage());
        }
        Customer customer = optionalCustomer.get();
        Page<Account> accountPage = accountRepository.findByCustomerId(customer.getId(), pageable);
        return new ApiResponse<>(accountPage.map(this::toAccountResponseDTO), ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
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
