package com.example.demoSQL.service;

import com.example.demoSQL.dto.transaction.TransactionCreateDTO;
import com.example.demoSQL.dto.transaction.TransactionResponseDTO;
import com.example.demoSQL.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionResponseDTO deposit(TransactionCreateDTO transactionCreateDTO);

    TransactionResponseDTO withdraw(TransactionCreateDTO transactionCreateDTO);

    TransactionResponseDTO transfer(TransactionCreateDTO transactionCreateDTO);

    TransactionResponseDTO getTransaction(Long id);

    Page<TransactionResponseDTO> getTransactionsByAccountId(Long accountId, Pageable pageable);

    Page<TransactionResponseDTO> getTransactionsByType(TransactionType type, Pageable pageable);
    Page<TransactionResponseDTO> getTransactionsByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable);

}
