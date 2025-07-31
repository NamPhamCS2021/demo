package com.example.demoSQL.contorller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.account.*;
import com.example.demoSQL.enums.AccountStatus;
import com.example.demoSQL.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @PostMapping
    public ApiResponse<Object> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO){
        return accountService.createAccount(accountCreateDTO);
    }

    @PutMapping("status/{id}")
    public ApiResponse<Object> updateAccountStatus(@PathVariable Long id, @Valid @RequestBody AccountUpdateStatusDTO accountUpdateStatusDTO){
        return accountService.updateAccountStatus(id, accountUpdateStatusDTO);
    }

    @PutMapping("accountlimit/{id}")
    public ApiResponse<Object> updateAccountLimit(@PathVariable Long id, @Valid @RequestBody AccountUpdateLimitDTO accountUpdateLimitDTO){
        return accountService.updateAccountLimit(id, accountUpdateLimitDTO);

    }

    @GetMapping("/{id}")
    public ApiResponse<Object> getAccountById(@PathVariable Long id){
        return accountService.getAccountById(id);
    }
    @GetMapping
    public ApiResponse<Object> getAllAccount(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.getAllAccounts(pageable);
    }

    @GetMapping("customer/{customerId}")
    public ApiResponse<Object> getAccountsByCustomerId(@PathVariable Long customerId,
                                                                       @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        return accountService.getAccountByCustomerId(customerId, pageable);
    }

    @GetMapping("customer/status/{customerId}")
    public ApiResponse<Object> getAccountsByCustomerIdAndStatus(@PathVariable Long customerId,@RequestParam AccountStatus status,
                                                                                @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable){
        return accountService.getAccountsByCustomerIdAndStatus(customerId, status, pageable);
    }
    @GetMapping("status/{status}")
    public ApiResponse<Object> getAccountsByStatus(
            @PathVariable AccountStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)Pageable pageable) {
        return accountService.getAccountsByStatus(status, pageable);
    }

    @PostMapping("/search")
    public ApiResponse<Object> searchAccounts(@Valid @RequestBody AccountSearchDTO accountSearchDTO, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return accountService.searchAccounts(accountSearchDTO, pageable);
    }

    @PostMapping("/search/{id}")
    public ApiResponse<Object> searchSelfAccount(@PathVariable Long id, @Valid @RequestBody AccountUserSearchDTO dto, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.searchSelfAccounts(id, dto, pageable);
    }



}
