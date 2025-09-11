package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.transaction.*;
import com.example.demoSQL.projections.LocationCount;
import com.example.demoSQL.enums.TransactionType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    ApiResponse<Object> deposit(TransactionCreateDTO transactionCreateDTO);

    ApiResponse<Object> withdraw(TransactionCreateDTO transactionCreateDTO);

    ApiResponse<Object> transfer(TransactionCreateDTO transactionCreateDTO);

    ApiResponse<Object> getTransaction(Long id);

//    ApiResponse<Object> getTransactionsByAccountId(Long accountId, Pageable pageable);
//
//    ApiResponse<Object> getTransactionsByType(TransactionType type, Pageable pageable);
//    ApiResponse<Object> getTransactionsByAccountIdAndType(Long accountId, TransactionType type, Pageable pageable);

    ApiResponse<Object> countTransactionsByLocation();

    ApiResponse<Object> searchTransactions(TransactionSearchDTO dto, Pageable pageable);

    ApiResponse<Object> selfTransactionSearch(Long id, TransactionUserSearchDTO dto, Pageable pageable);

    ApiResponse<Object> selfTransactionSearchByAccountNumber(String accountNumber, TransactionUserSearchDTO dto, Pageable pageable);

    ApiResponse<Object> depositByAccountNumber(TransactionCreateANDTO transactionCreateDTO);

    ApiResponse<Object> withdrawByAccountNumber(TransactionCreateANDTO transactionCreateDTO);

    ApiResponse<Object> transferByAccountNumber(TransactionCreateANDTO transactionCreateDTO);
}
