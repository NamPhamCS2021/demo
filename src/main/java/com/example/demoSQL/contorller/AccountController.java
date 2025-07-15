package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.AccountCreateDTO;
import com.example.demoSQL.dto.account.AccountResponseDTO;
import com.example.demoSQL.dto.account.AccountUpdateLimitDTO;
import com.example.demoSQL.dto.account.AccountUpdateStatusDTO;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/accounts")
@Validated
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @PreAuthorize("@authSecurity.isSelfCustomer(#accountCreateDTO.customerId)")
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO){
        ApiResponse<Object> createdDTO = accountService.createAccount(accountCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    @PutMapping("status/{id}")
    public ResponseEntity<ApiResponse<Object>> updateAccountStatus(@PathVariable Long id, @Valid @RequestBody AccountUpdateStatusDTO accountUpdateStatusDTO){
        ApiResponse<Object> updatedDTO = accountService.updateAccountStatus(id, accountUpdateStatusDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    @PutMapping("accountlimit/{id}")
    public ResponseEntity<ApiResponse<Object>> updateAccountLimit(@PathVariable Long id, @Valid @RequestBody AccountUpdateLimitDTO accountUpdateLimitDTO){
        ApiResponse<Object> updatedDTO = accountService.updateAccountLimit(id, accountUpdateLimitDTO);
        return ResponseEntity.ok(updatedDTO);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getAccountById(@PathVariable Long id){
        ApiResponse<Object> accountResponseDTO = accountService.getAccountById(id);
        return ResponseEntity.ok(accountResponseDTO);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllAccount(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        ApiResponse<Object> accounts = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @GetMapping("customer/{customerId}")
    public ResponseEntity<ApiResponse<Object>> getAccountsByCustomerId(@PathVariable Long customerId,
                                                                       @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        ApiResponse<Object> accounts = accountService.getAccountByCustomerId(customerId, pageable);
        return ResponseEntity.ok(accounts);
    }

    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @GetMapping("customer/status/{customerId}")
    public ResponseEntity<ApiResponse<Object>> getAccountsByCustomerIdAndStatus(@PathVariable Long customerId,@RequestParam AccountStatus status,
                                                                                @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        ApiResponse<Object> accounts = accountService.getAccountsByCustomerIdAndStatus(customerId, status, pageable);
        return ResponseEntity.ok(accounts);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("status/{status}")
    public ResponseEntity<ApiResponse<Object>> getAccountsByStatus(
            @PathVariable AccountStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        ApiResponse<Object> accounts = accountService.getAccountsByStatus(status, pageable);
        return ResponseEntity.ok(accounts);
    }



}
