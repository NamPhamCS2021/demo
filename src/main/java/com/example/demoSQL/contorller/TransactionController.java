package com.example.demoSQL.contorller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/transactions")
@Validated
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#transactionCreateDTO.accountId)")
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Object>> deposit(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        ;
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(transactionCreateDTO));
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#transactionCreateDTO.accountId)")
    @PostMapping("/withdraw")
    public ApiResponse<Object> withdraw(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return transactionService.withdraw(transactionCreateDTO);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#transactionCreateDTO.accountId)")
    @PostMapping("/transfer")
    public ApiResponse<Object> transfer(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return transactionService.transfer(transactionCreateDTO);
    }

    @PreAuthorize("@authSecurity.isOwnerOfTransaction(#id)")
    @GetMapping("/{id}")
    public ApiResponse<Object> getTransaction(@PathVariable Long id){
        return transactionService.getTransaction(id);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#accountId)")
    @GetMapping("/account")
    public ApiResponse<Object> getTransactionsByAccountId(@RequestParam Long accountId,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return transactionService.getTransactionsByAccountId(accountId, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Object>> getTransactionsByType(@PathVariable TransactionType type,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        ApiResponse<Object> transactions = transactionService.getTransactionsByType(type, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("@authSecurity.isOwnerOfAccount(#accountId)")
    @GetMapping("account/type/{type}")
    public ApiResponse<Object> getTransactionsByAccountIdAndType(@RequestParam Long accountId,
                                                                             @PathVariable TransactionType type,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return transactionService.getTransactionsByAccountIdAndType(accountId, type, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("locationcount")
    public ApiResponse<Object> countTransactionsByLocation(){
        return transactionService.countTransactionsByLocation();
    }
}
