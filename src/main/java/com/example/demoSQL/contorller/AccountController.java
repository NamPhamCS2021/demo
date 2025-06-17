package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateDTO;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Validated
public class AccountController {

    @Autowired
    private AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO){
        AccountResponseDTO createdDTO = accountService.createAccount(accountCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountUpdateDTO accountUpdateDTO){
        AccountResponseDTO updatedDTO = accountService.updateAccount(id, accountUpdateDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    @GetMapping
    public ResponseEntity<Page<AccountResponseDTO>> getAllAccount(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AccountResponseDTO> accounts = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("status/{status}")
    public ResponseEntity<Page<AccountResponseDTO>> getAccountsByStatus(
            @PathVariable AccountStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        Page<AccountResponseDTO> accounts = accountService.getAccountsByStatus(status, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("customer")
    public ResponseEntity<Page<AccountResponseDTO>> getAccountsByCustomer(
            @RequestParam Long customerId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        Page<AccountResponseDTO> accounts = accountService.getAccountsByCustomer(customerId, pageable);
        return ResponseEntity.ok(accounts);
    }
    @GetMapping("account-number/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByAccountNumber(
            @PathVariable String accountNumber) {
        AccountResponseDTO account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
    @GetMapping("customer/status/{status}")
    public ResponseEntity<Page<AccountResponseDTO>> getAccountsByCustomerAndStatus(
            @RequestParam Long customerId,
            @PathVariable AccountStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        Page<AccountResponseDTO> accounts = accountService.getAccountsByCustomerAndStatus(customerId, status, pageable);
        return ResponseEntity.ok(accounts);
    }

}
