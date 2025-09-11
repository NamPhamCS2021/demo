package com.example.demoSQL.controller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.transaction.TransactionCreateANDTO;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionSearchDTO;
import com.example.demoSQL.dto.transaction.TransactionUserSearchDTO;
import com.example.demoSQL.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/transactions")
@Validated
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#transactionCreateDTO.accountId)")
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Object>> deposit(@Validated(TransactionCreateDTO.OnWithdraw.class) @Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(transactionCreateDTO));
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#transactionCreateDTO.accountId)")
    @PostMapping("/withdraw")
    public ApiResponse<Object> withdraw(@Validated(TransactionCreateDTO.OnWithdraw.class) @Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return transactionService.withdraw(transactionCreateDTO);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#transactionCreateDTO.accountId)")
    @PostMapping("/transfer")
    public ApiResponse<Object> transfer(@Validated(TransactionCreateDTO.OnTransfer.class) @Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return transactionService.transfer(transactionCreateDTO);
    }
    @PreAuthorize("@authSecurity.isOwnerOfAccountByAccountNumber(#transactionCreateDTO.accountNumber)")
    @PostMapping("/depositByAccountNumber")
    public ResponseEntity<ApiResponse<Object>> depositByAccountNumber(@Validated(TransactionCreateANDTO.OnWithdraw.class) @Valid @RequestBody TransactionCreateANDTO transactionCreateDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.depositByAccountNumber(transactionCreateDTO));
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccountByAccountNumber(#transactionCreateDTO.accountNumber)")
    @PostMapping("/withdrawByAccountNumber")
    public ApiResponse<Object> withdrawByAccountNumber(@Validated(TransactionCreateANDTO.OnWithdraw.class) @Valid @RequestBody TransactionCreateANDTO transactionCreateDTO){
        return transactionService.withdrawByAccountNumber(transactionCreateDTO);
    }
//    @PreAuthorize("@authSecurity.isOwnerOfAccountByAccountNumber(#transactionCreateDTO.accountNumber)")
    @PostMapping("/transferByAccountNumber")
    public ApiResponse<Object> transferByAccountNumber(@Validated(TransactionCreateANDTO.OnTransfer.class) @Valid @RequestBody TransactionCreateANDTO transactionCreateDTO){
        return transactionService.transferByAccountNumber(transactionCreateDTO);
    }

    @PreAuthorize("@authSecurity.isOwnerOfTransaction(#id)")
    @GetMapping("/{id}")
    public ApiResponse<Object> getTransaction(@PathVariable Long id){
        return transactionService.getTransaction(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/searchh")
    public ApiResponse<Object> searchTransactions( @Valid @RequestBody TransactionSearchDTO dto, @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return transactionService.searchTransactions(dto, pageable);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#accountId)")
    @PutMapping("/account/{accountId}/search")
    public ApiResponse<Object> selfTransactionSearch(@PathVariable Long accountId, @Valid @RequestBody TransactionUserSearchDTO dto, @PageableDefault(size = 20,sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return transactionService.selfTransactionSearch(accountId,dto,pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("locationcount")
    public ApiResponse<Object> countTransactionsByLocation(){
        return transactionService.countTransactionsByLocation();
    }
    @PreAuthorize("@authSecurity.isOwnerOfAccountByAccountNumber(#accountNumber)")
    @PutMapping("account/accountnumber/{accountNumber}/search")
    public ApiResponse<Object>selfTransactionSearchByAccountNumber(@PathVariable("accountNumber") String accountNumber, @Valid@RequestBody TransactionUserSearchDTO dto,
                                                                   @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return transactionService.selfTransactionSearchByAccountNumber(accountNumber, dto, pageable);
    }
}
