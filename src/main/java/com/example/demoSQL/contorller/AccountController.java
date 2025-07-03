package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateLimitDTO;
import com.example.demoSQL.dto.account.AccountUpdateStatusDTO;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;



    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO){
        ApiResponse<Object> createdDTO = accountService.createAccount(accountCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @PutMapping("status/{id}")
    public ResponseEntity<ApiResponse<Object>> updateAccountStatus(@PathVariable Long id, @Valid @RequestBody AccountUpdateStatusDTO accountUpdateStatusDTO){
        ApiResponse<Object> updatedDTO = accountService.updateAccountStatus(id, accountUpdateStatusDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    @PutMapping("accountlimit/{id}")
    public ResponseEntity<ApiResponse<Object>> updateAccountLimit(@PathVariable Long id, @Valid @RequestBody AccountUpdateLimitDTO accountUpdateLimitDTO){
        ApiResponse<Object> updatedDTO = accountService.updateAccountLimit(id, accountUpdateLimitDTO);
        return ResponseEntity.ok(updatedDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getAccountById(@PathVariable Long id){
        ApiResponse<Object> accountResponseDTO = accountService.getAccountById(id);
        return ResponseEntity.ok(accountResponseDTO);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllAccount(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        ApiResponse<Object> accounts = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("status/{status}")
    public ResponseEntity<ApiResponse<Object>> getAccountsByStatus(
            @PathVariable AccountStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        ApiResponse<Object> accounts = accountService.getAccountsByStatus(status, pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("account-number/{accountNumber}")
    public ResponseEntity<ApiResponse<Object>> getAccountByAccountNumber(
            @PathVariable String accountNumber) {
        ApiResponse<Object> account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }


}
