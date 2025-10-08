package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.transaction.*;
import org.springframework.data.domain.Pageable;

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

    ApiResponse<Object> search(TransactionSearchDTO dto, Pageable pageable);

    ApiResponse<Object> selfSearch(Long id, TransactionUserSearchDTO dto, Pageable pageable);

    ApiResponse<Object> selfSearchByAccountNumber(String accountNumber, TransactionUserSearchDTO dto, Pageable pageable);

    ApiResponse<Object> depositByAccountNumber(TransactionCreateANDTO transactionCreateDTO);

    ApiResponse<Object> withdrawByAccountNumber(TransactionCreateANDTO transactionCreateDTO);

    ApiResponse<Object> transferByAccountNumber(TransactionCreateANDTO transactionCreateDTO);
}
