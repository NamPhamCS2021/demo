package com.example.demoSQL.controller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.*;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.security.entity.User;
import com.example.demoSQL.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PreAuthorize("@authSecurity.isSelfCustomer(#accountCreateDTO.customerId)")
    @PostMapping
    public ApiResponse<Object> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO){
        return accountService.createAccount(accountCreateDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    @PutMapping("status/{id}")
    public ApiResponse<Object> updateAccountStatus(@PathVariable Long id, @Valid @RequestBody AccountUpdateStatusDTO accountUpdateStatusDTO){
        return accountService.updateAccountStatus(id, accountUpdateStatusDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    @PutMapping("accountlimit/{id}")
    public ApiResponse<Object> updateAccountLimit(@PathVariable Long id, @Valid @RequestBody AccountUpdateLimitDTO accountUpdateLimitDTO){
        return accountService.updateAccountLimit(id, accountUpdateLimitDTO);
    }


    @PreAuthorize("@authSecurity.isOwnerOfAccount(#id)")
    @GetMapping("/{id}")
    public ApiResponse<Object> getAccountById(@PathVariable Long id){
        return accountService.getAccountById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ApiResponse<Object> getAllAccount(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.getAllAccounts(pageable);
    }

    @PreAuthorize("@authSecurity.isSelfCustomer(#customerId)")
    @GetMapping("customer/{customerId}")
    public ApiResponse<Object> getAccountsByCustomerId(@PathVariable Long customerId,
                                                                       @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        return accountService.getAccountByCustomerId(customerId, pageable);
    }

    @PreAuthorize("@authSecurity.isSelfCustomer(#customerId)")
    @GetMapping("customer/status/{customerId}")
    public ApiResponse<Object> getAccountsByCustomerIdAndStatus(@PathVariable Long customerId,@RequestParam AccountStatus status,
                                                                                @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        return accountService.getAccountsByCustomerIdAndStatus(customerId, status, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("status/{status}")
    public ApiResponse<Object> getAccountsByStatus(
            @PathVariable AccountStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        return accountService.getAccountsByStatus(status, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/search")
    public ApiResponse<Object> searchAccounts(@Valid @RequestBody AccountSearchDTO accountSearchDTO, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return accountService.searchAccounts(accountSearchDTO, pageable);
    }

    @PreAuthorize("@authSecurity.isSelfCustomer(#id)")
    @PostMapping("/search/{id}")
    public ApiResponse<Object> searchSelfAccount(@PathVariable Long id, @Valid @RequestBody AccountUserSearchDTO dto, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.searchSelfAccounts(id, dto, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccountByAccountNumber(#accountNumber)")
    @GetMapping("/byAccountNumber/{accountNumber}")
    public ApiResponse<Object> getAccountByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/byAccountNumber/receiver/{accountNumber}")
    public ApiResponse<Object> getReceiverByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getReceiver(accountNumber);
    }

    @GetMapping("/me")
    public ApiResponse<Object> getMyAccounts(Authentication auth, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        String mail = auth.getName();
        return accountService.getAccountsByEmail(mail, pageable);
    }


}
