package com.example.demoSQL.service;

import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateDTO;
import com.example.demoSQL.entity.Account;
import com.example.demoSQL.entity.Customer;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.repository.AccountRepository;
import com.example.demoSQL.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public AccountResponseDTO createAccount(AccountCreateDTO accountCreateDTO) {
        Customer customer = customerRepository.findById(accountCreateDTO.getCustomerId()).orElseThrow(() -> new EntityNotFoundException("Customer with id " + accountCreateDTO.getCustomerId() + " not found"));
        Account account = new Account();
        account.setCustomer(customer);
        accountRepository.save(account);

        return toAccountResponseDTO(account);
    }

    public AccountResponseDTO updateAccount(Long id, AccountUpdateDTO accountUpdate) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with id " + id + " not found"));
        if (accountUpdate.getStatus() != null || accountUpdate.getStatus() != existingAccount.getStatus()) {
            existingAccount.setStatus(accountUpdate.getStatus());
        }
        accountRepository.save(existingAccount);

        return toAccountResponseDTO(existingAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long id){
        Account account = accountRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Account with id "+id+" not found"));
        return toAccountResponseDTO(account);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponseDTO> getAccountsByCustomer(Long customerId, Pageable pageable) {
        if(!customerRepository.existsById(customerId)){
            throw new EntityNotFoundException("Customer with id "+customerId+" not found");
        }
        Page<Account> accountPage = accountRepository.findByCustomerId(customerId, pageable);
        return accountPage.map(this::toAccountResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponseDTO> getAllAccounts(Pageable pageable) {
        Page<Account> accountPage = accountRepository.findAll(pageable);
        return accountPage.map(this::toAccountResponseDTO);
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()->new EntityNotFoundException("Account with account number "+accountNumber+" not found"));
        return toAccountResponseDTO(account);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponseDTO> getAccountsByStatus(AccountStatus status, Pageable pageable) {
        Page<Account> accountPage = accountRepository.findByStatus(status, pageable);
        return accountPage.map(this::toAccountResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponseDTO> getAccountsByCustomerAndStatus(Long customerId, AccountStatus status, Pageable pageable) {
        if(!customerRepository.existsById(customerId)){
            throw new EntityNotFoundException("Customer with id "+customerId+" not found");
        }
        Page<Account> accountPage = accountRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        return accountPage.map(this::toAccountResponseDTO);
    }

    //helpa
    public AccountResponseDTO toAccountResponseDTO(Account account){
        return AccountResponseDTO.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .openingDate(account.getOpeningDate())
                .customerName(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName()).build();
    }
}
