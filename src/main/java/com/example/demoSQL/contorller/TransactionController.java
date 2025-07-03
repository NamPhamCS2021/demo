package com.example.demoSQL.contorller;


import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Object>> deposit(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        ;
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(transactionCreateDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Object>> withdraw(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        ApiResponse<Object> transactionResponseDTO =  transactionService.withdraw(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Object>> transfer(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        ApiResponse<Object> transactionResponseDTO = transactionService.transfer(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getTransaction(@PathVariable Long id){
        ApiResponse<Object> transactionResponseDTO = transactionService.getTransaction(id);
        return ResponseEntity.ok(transactionResponseDTO);
    }

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<Object>> getTransactionsByAccountId(@RequestParam Long accountId,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        ApiResponse<Object> transactions = transactionService.getTransactionsByAccountId(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Object>> getTransactionsByType(@PathVariable TransactionType type,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        ApiResponse<Object> transactions = transactionService.getTransactionsByType(type, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("account/type/{type}")
    public ResponseEntity<ApiResponse<Object>> getTransactionsByAccountIdAndType(@RequestParam Long accountId,
                                                                             @PathVariable TransactionType type,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        ApiResponse<Object> transactions = transactionService.getTransactionsByAccountIdAndType(accountId, type, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("locationcount")
    public ApiResponse<Object> countTransactionsByLocation(){
        return transactionService.countTransactionsByLocation();
    }
}
