package com.example.demoSQL.contorller;


import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.enums.TransactionType;
import com.example.demoSQL.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        TransactionResponseDTO transactionResponseDTO = transactionService.deposit(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        TransactionResponseDTO transactionResponseDTO =  transactionService.withdraw(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        TransactionResponseDTO transactionResponseDTO = transactionService.transfer(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable Long id){
        TransactionResponseDTO transactionResponseDTO = transactionService.getTransaction(id);
        return ResponseEntity.ok(transactionResponseDTO);
    }

    @GetMapping("/account")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByAccountId(@RequestParam Long accountId,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        Page<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccountId(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByType(@PathVariable TransactionType type,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        Page<TransactionResponseDTO> transactions = transactionService.getTransactionsByType(type, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("account/type/{type}")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByAccountIdAndType(@RequestParam Long accountId,
                                                                             @PathVariable TransactionType type,
                                                                             @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable){
        Page<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccountIdAndType(accountId, type, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("locationcount")
    public List<LocationCount> countTransactionsByLocation(){
        return transactionService.countTransactionsByLocation();
    }
}
