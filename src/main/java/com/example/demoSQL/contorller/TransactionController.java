package com.example.demoSQL.contorller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionSearchDTO;
import com.example.demoSQL.dto.transaction.TransactionUserSearchDTO;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/transactions")
@Validated
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Object>> deposit(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(transactionCreateDTO));
    }

    @PostMapping("/withdraw")
    public ApiResponse<Object> withdraw(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return transactionService.withdraw(transactionCreateDTO);
    }

    @PostMapping("/transfer")
    public ApiResponse<Object> transfer(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        return transactionService.transfer(transactionCreateDTO);
    }

    @GetMapping("/{id}")
    public ApiResponse<Object> getTransaction(@PathVariable Long id){
        return transactionService.getTransaction(id);
    }

    @PutMapping("/searchh")
    public ApiResponse<Object> searchTransactions( @Valid @RequestBody TransactionSearchDTO dto, @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return transactionService.searchTransactions(dto, pageable);
    }

    @PutMapping("/account/{accountId}/search")
    public ApiResponse<Object> selfSearchTransactions(@PathVariable Long accountId, @Valid @RequestBody TransactionUserSearchDTO dto, @PageableDefault(size = 20,sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        return transactionService.selfTransactionSearch(accountId,dto,pageable);
    }


    @GetMapping("locationcount")
    public ApiResponse<Object> countTransactionsByLocation(){
        return transactionService.countTransactionsByLocation();
    }
}
