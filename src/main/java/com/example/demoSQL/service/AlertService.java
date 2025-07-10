package com.example.demoSQL.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.dto.alert.AlertDTO;
import com.example.demoSQL.entity.Alert;
import com.example.demoSQL.enums.AlertStatus;
import com.example.demoSQL.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlertService {
    void detectAbnormalTransactions();
    ApiResponse<Object> getAll(Pageable pageable);
    ApiResponse<Object> getByTransactionId(Long transactionId, Pageable pageable);
    ApiResponse<Object> getByTransactionIdAndType(Long transactionId, AlertType type, Pageable pageable);
    ApiResponse<Object> getByTransactionIdAndStatus(Long transactionId, AlertStatus status, Pageable pageable);
    ApiResponse<Object> getByTransactionIdAndTypeAndStatus(Long transactionId, AlertType type, AlertStatus status, Pageable pageable);
    ApiResponse<Object> getByAccountId(Long accountId, Pageable pageable);
    ApiResponse<Object> getByAccountIdAndType(Long accountId, AlertType type, Pageable pageable);
    ApiResponse<Object> getByAccountIdAndStatus(Long accountId, AlertStatus status, Pageable pageable);
    ApiResponse<Object> getByAccountIdAndTypeAndStatus(Long accountId, AlertType type, AlertStatus status, Pageable pageable);
}
